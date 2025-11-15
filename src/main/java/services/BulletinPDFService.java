/*
 * Service de génération de bulletins PDF avec iText
 * Crée des bulletins de notes formatés professionnellement
 */
package services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import models.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BulletinPDFService {
    
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    
    /**
     * Génère un bulletin PDF pour un étudiant
     */
    public String generateBulletinPDF(BulletinService.BulletinData bulletinData, String outputPath) 
            throws DocumentException, IOException {
        
        Document document = new Document(PageSize.A4);
        String fileName = outputPath + "/bulletin_" + 
                         bulletinData.getEtudiant().getNom().toLowerCase() + "_" +
                         bulletinData.getEtudiant().getPrenom().toLowerCase() + "_" +
                         bulletinData.getAnneeScolaire().getLibelle().replace("/", "-") + ".pdf";
        
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
        
        // En-tête et pied de page
        writer.setPageEvent(new BulletinPageEvent(bulletinData));
        
        document.open();
        
        // Contenu principal
        addUniversityHeader(document);
        addStudentInfo(document, bulletinData);
        addNotesTable(document, bulletinData);
        addSummary(document, bulletinData);
        addFooter(document);
        
        document.close();
        
        return fileName;
    }
    
    private void addUniversityHeader(Document document) throws DocumentException {
        // Logo et nom de l'université
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20);
        
        // Colonne gauche - Informations université
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        
        Paragraph university = new Paragraph("UNIVERSITÉ IBN KHALDOUN - TIARET", TITLE_FONT);
        university.setAlignment(Element.ALIGN_LEFT);
        leftCell.addElement(university);
        
        Paragraph faculty = new Paragraph("Faculté des Mathématiques et d'Informatique", NORMAL_FONT);
        faculty.setAlignment(Element.ALIGN_LEFT);
        leftCell.addElement(faculty);
        
        Paragraph dept = new Paragraph("Département d'Informatique", NORMAL_FONT);
        dept.setAlignment(Element.ALIGN_LEFT);
        leftCell.addElement(dept);
        
        // Colonne droite - Date
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Paragraph date = new Paragraph("Édité le : " + dateFormat.format(new Date()), NORMAL_FONT);
        date.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(date);
        
        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);
        
        document.add(headerTable);
        
        // Titre du bulletin
        Paragraph title = new Paragraph("BULLETIN DE NOTES", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(20);
        title.setSpacingAfter(20);
        document.add(title);
    }
    
    private void addStudentInfo(Document document, BulletinService.BulletinData data) 
            throws DocumentException {
        
        Etudiant etudiant = data.getEtudiant();
        Programme programme = data.getProgramme();
        AnneeScolaire annee = data.getAnneeScolaire();
        
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20);
        infoTable.setWidths(new float[]{2, 3, 2, 3});
        
        // Style des cellules d'en-tête
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(new BaseColor(52, 152, 219));
        headerCell.setPadding(8);
        
        // Informations étudiant
        addInfoRow(infoTable, "Nom et Prénom:", etudiant.getNomComplet());
        addInfoRow(infoTable, "Origine scolaire:", etudiant.getOrigineScolaire());
        addInfoRow(infoTable, "Programme:", programme != null ? programme.getNom() : "N/A");
        addInfoRow(infoTable, "Année universitaire:", annee != null ? annee.getLibelle() : "N/A");
        
        if (etudiant.getDateNaissance() != null) {
            addInfoRow(infoTable, "Date de naissance:", etudiant.getDateNaissance().toString());
        }
        
        document.add(infoTable);
    }
    
    private void addInfoRow(PdfPTable table, String label, String value) {
        // Cellule label
        PdfPCell labelCell = new PdfPCell(new Phrase(label, HEADER_FONT));
        labelCell.setBackgroundColor(new BaseColor(236, 240, 241));
        labelCell.setPadding(5);
        
        // Cellule valeur
        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private void addNotesTable(Document document, BulletinService.BulletinData data) 
            throws DocumentException {
        
        Paragraph notesTitle = new Paragraph("DÉTAIL DES NOTES PAR MATIÈRE", HEADER_FONT);
        notesTitle.setAlignment(Element.ALIGN_LEFT);
        notesTitle.setSpacingBefore(15);
        notesTitle.setSpacingAfter(10);
        document.add(notesTitle);
        
        PdfPTable notesTable = new PdfPTable(5);
        notesTable.setWidthPercentage(100);
        notesTable.setWidths(new float[]{4, 2, 2, 2, 2});
        notesTable.setSpacingAfter(20);
        
        // En-tête du tableau
        addTableHeader(notesTable, "Matière");
        addTableHeader(notesTable, "Note /20");
        addTableHeader(notesTable, "Coefficient");
        addTableHeader(notesTable, "Points");
        addTableHeader(notesTable, "Mention");
        
        // Données des matières
        double totalPoints = 0.0;
        double totalCoefficients = 0.0;
        
        for (NoteMatiere note : data.getNotesMatiere()) {
            // Supposons un coefficient de 1 par défaut si non spécifié
            double coefficient = 1.0; // À récupérer depuis la base si disponible
            double points = note.getNoteFinale() * coefficient;
            
            addTableCell(notesTable, note.getMatiereName(), NORMAL_FONT);
            addTableCell(notesTable, String.format("%.2f", note.getNoteFinale()), NORMAL_FONT);
            addTableCell(notesTable, String.format("%.1f", coefficient), NORMAL_FONT);
            addTableCell(notesTable, String.format("%.2f", points), NORMAL_FONT);
            addTableCell(notesTable, getMention(note.getNoteFinale()), NORMAL_FONT);
            
            totalPoints += points;
            totalCoefficients += coefficient;
        }
        
        // Ligne de total
        addTableCell(notesTable, "MOYENNE GÉNÉRALE", HEADER_FONT);
        addTableCell(notesTable, String.format("%.2f", data.getMoyenneGenerale()), HEADER_FONT);
        addTableCell(notesTable, String.format("%.1f", totalCoefficients), HEADER_FONT);
        addTableCell(notesTable, String.format("%.2f", totalPoints), HEADER_FONT);
        addTableCell(notesTable, getMention(data.getMoyenneGenerale()), HEADER_FONT);
        
        document.add(notesTable);
    }
    
    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(new BaseColor(52, 152, 219));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        table.addCell(cell);
    }
    
    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    private String getMention(double note) {
        if (note >= 16) return "Très Bien";
        if (note >= 14) return "Bien";
        if (note >= 12) return "Assez Bien";
        if (note >= 10) return "Passable";
        return "Insuffisant";
    }
    
    private void addSummary(Document document, BulletinService.BulletinData data) 
            throws DocumentException {
        
        Paragraph summaryTitle = new Paragraph("RÉSUMÉ DE L'ANNÉE", HEADER_FONT);
        summaryTitle.setSpacingBefore(20);
        summaryTitle.setSpacingAfter(10);
        document.add(summaryTitle);
        
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(70);
        summaryTable.setWidths(new float[]{3, 2});
        
        addInfoRow(summaryTable, "Moyenne générale:", String.format("%.2f/20", data.getMoyenneGenerale()));
        addInfoRow(summaryTable, "Mention:", getMention(data.getMoyenneGenerale()));
        addInfoRow(summaryTable, "Statut de fin d'année:", data.getStatut().toUpperCase());
        
        // Couleur selon le statut
        PdfPCell statusCell = new PdfPCell(new Phrase(getStatutMessage(data.getStatut()), HEADER_FONT));
        if (data.getStatut().equalsIgnoreCase("admis")) {
            statusCell.setBackgroundColor(new BaseColor(46, 204, 113));
        } else if (data.getStatut().equalsIgnoreCase("redoublant")) {
            statusCell.setBackgroundColor(new BaseColor(230, 126, 34));
        } else {
            statusCell.setBackgroundColor(new BaseColor(231, 76, 60));
        }
        statusCell.setPadding(8);
        statusCell.setColspan(2);
        statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        summaryTable.addCell(statusCell);
        
        document.add(summaryTable);
    }
    
    private String getStatutMessage(String statut) {
        switch (statut.toLowerCase()) {
            case "admis": return "FÉLICITATIONS - ADMIS(E) EN ANNÉE SUPÉRIEURE";
            case "redoublant": return "REDOUBLEMENT - DROIT À UNE SESSION DE RATTRAPAGE";
            case "exclu": return "EXCLUSION - MOYENNE INSUFFISANTE";
            default: return "STATUT EN COURS D'ÉVALUATION";
        }
    }
    
    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph("\n\nCe document est généré automatiquement par le système de gestion de scolarité.", SMALL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
        
        Paragraph signature = new Paragraph("Service de la Scolarité\nUniversité Ibn Khaldoun - Tiaret", SMALL_FONT);
        signature.setAlignment(Element.ALIGN_RIGHT);
        signature.setSpacingBefore(20);
        document.add(signature);
    }
    
    /**
     * Classe pour gérer les en-têtes et pieds de page
     */
    private static class BulletinPageEvent extends PdfPageEventHelper {
        private BulletinService.BulletinData bulletinData;
        
        public BulletinPageEvent(BulletinService.BulletinData bulletinData) {
            this.bulletinData = bulletinData;
        }
        
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            // Pied de page avec numéro de page
            PdfPTable footer = new PdfPTable(1);
            footer.setTotalWidth(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
            
            PdfPCell cell = new PdfPCell(new Phrase("Page " + writer.getPageNumber(), SMALL_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            footer.addCell(cell);
            
            footer.writeSelectedRows(0, -1, document.leftMargin(), 
                document.bottomMargin() - 10, writer.getDirectContent());
        }
    }
}