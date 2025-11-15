/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.*;
import models.*;
import java.sql.SQLException;
import java.util.List;
import java.io.IOException;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class StatistiquesService {
    private InscriptionDAO inscriptionDAO;
    private NoteEpreuveDAO noteEpreuveDAO;
    private EtudiantDAO etudiantDAO;
    
    public StatistiquesService() {
        this.inscriptionDAO = new InscriptionDAO();
        this.noteEpreuveDAO = new NoteEpreuveDAO();
        this.etudiantDAO = new EtudiantDAO();
    }
    
    /**
     * Statistiques globales par programme
     */
    public List<StatProgramme> getStatistiquesParProgramme(int idAnnee) throws SQLException {
        String sql = "SELECT p.nom AS programme, " +
                     "COUNT(DISTINCT i.id_etudiant) AS nb_etudiants, " +
                     "COUNT(CASE WHEN i.statut_fin_annee = 'admis' THEN 1 END) AS nb_admis, " +
                     "COUNT(CASE WHEN i.statut_fin_annee = 'redoublant' THEN 1 END) AS nb_redoublants, " +
                     "COUNT(CASE WHEN i.statut_fin_annee = 'exclu' THEN 1 END) AS nb_exclus, " +
                     "ROUND(AVG(i.moyenne_generale), 2) AS moyenne_programme " +
                     "FROM INSCRIPTION i " +
                     "JOIN PROGRAMME p ON i.id_programme = p.id_programme " +
                     "WHERE i.id_annee = ? " +
                     "GROUP BY p.id_programme, p.nom";
        
        return inscriptionDAO.executeQuery(sql, rs -> {
            StatProgramme stat = new StatProgramme();
            stat.setProgramme(rs.getString("programme"));
            stat.setNbEtudiants(rs.getInt("nb_etudiants"));
            stat.setNbAdmis(rs.getInt("nb_admis"));
            stat.setNbRedoublants(rs.getInt("nb_redoublants"));
            stat.setNbExclus(rs.getInt("nb_exclus"));
            stat.setMoyenneProgramme(rs.getDouble("moyenne_programme"));
            return stat;
        }, idAnnee);
    }
    
    /**
     * Top N étudiants par moyenne
     */
    public List<TopEtudiant> getTopEtudiants(int idAnnee, int limit) throws SQLException {
        String sql = "SELECT CONCAT(e.nom, ' ', e.prenom) AS etudiant, " +
                     "p.nom AS programme, i.moyenne_generale, i.statut_fin_annee " +
                     "FROM INSCRIPTION i " +
                     "JOIN ETUDIANT e ON i.id_etudiant = e.id_etudiant " +
                     "JOIN PROGRAMME p ON i.id_programme = p.id_programme " +
                     "WHERE i.id_annee = ? " +
                     "ORDER BY i.moyenne_generale DESC LIMIT ?";
        
        return inscriptionDAO.executeQuery(sql, rs -> {
            TopEtudiant top = new TopEtudiant();
            top.setEtudiant(rs.getString("etudiant"));
            top.setProgramme(rs.getString("programme"));
            top.setMoyenne(rs.getDouble("moyenne_generale"));
            top.setStatut(rs.getString("statut_fin_annee"));
            return top;
        }, idAnnee, limit);
    }
    
    /**
     * Taux de réussite global
     */
    public TauxReussite getTauxReussite(int idAnnee) throws SQLException {
        String sql = "SELECT " +
                     "COUNT(*) AS total, " +
                     "COUNT(CASE WHEN statut_fin_annee = 'admis' THEN 1 END) AS admis, " +
                     "COUNT(CASE WHEN statut_fin_annee = 'redoublant' THEN 1 END) AS redoublants, " +
                     "COUNT(CASE WHEN statut_fin_annee = 'exclu' THEN 1 END) AS exclus " +
                     "FROM INSCRIPTION WHERE id_annee = ?";
        
        List<TauxReussite> results = inscriptionDAO.executeQuery(sql, rs -> {
            TauxReussite taux = new TauxReussite();
            int total = rs.getInt("total");
            int admis = rs.getInt("admis");
            int redoublants = rs.getInt("redoublants");
            int exclus = rs.getInt("exclus");
            
            taux.setTotal(total);
            taux.setAdmis(admis);
            taux.setRedoublants(redoublants);
            taux.setExclus(exclus);
            
            if (total > 0) {
                taux.setPourcentageAdmis((admis * 100.0) / total);
                taux.setPourcentageRedoublants((redoublants * 100.0) / total);
                taux.setPourcentageExclus((exclus * 100.0) / total);
            }
            
            return taux;
        }, idAnnee);
        
        return results.isEmpty() ? new TauxReussite() : results.get(0);
    }
    
    // Classes internes pour les statistiques
    public static class StatProgramme {
        private String programme;
        private int nbEtudiants;
        private int nbAdmis;
        private int nbRedoublants;
        private int nbExclus;
        private double moyenneProgramme;
        
        // Getters & Setters
        public String getProgramme() { return programme; }
        public void setProgramme(String programme) { this.programme = programme; }
        
        public int getNbEtudiants() { return nbEtudiants; }
        public void setNbEtudiants(int nbEtudiants) { this.nbEtudiants = nbEtudiants; }
        
        public int getNbAdmis() { return nbAdmis; }
        public void setNbAdmis(int nbAdmis) { this.nbAdmis = nbAdmis; }
        
        public int getNbRedoublants() { return nbRedoublants; }
        public void setNbRedoublants(int nbRedoublants) { this.nbRedoublants = nbRedoublants; }
        
        public int getNbExclus() { return nbExclus; }
        public void setNbExclus(int nbExclus) { this.nbExclus = nbExclus; }
        
        public double getMoyenneProgramme() { return moyenneProgramme; }
        public void setMoyenneProgramme(double moyenneProgramme) { 
            this.moyenneProgramme = moyenneProgramme; 
        }
        
        public double getTauxReussite() {
            return nbEtudiants > 0 ? (nbAdmis * 100.0) / nbEtudiants : 0.0;
        }
    }
    
    public static class TopEtudiant {
        private String etudiant;
        private String programme;
        private double moyenne;
        private String statut;
        
        // Getters & Setters
        public String getEtudiant() { return etudiant; }
        public void setEtudiant(String etudiant) { this.etudiant = etudiant; }
        
        public String getProgramme() { return programme; }
        public void setProgramme(String programme) { this.programme = programme; }
        
        public double getMoyenne() { return moyenne; }
        public void setMoyenne(double moyenne) { this.moyenne = moyenne; }
        
        public String getStatut() { return statut; }
        public void setStatut(String statut) { this.statut = statut; }
    }
    
    public static class TauxReussite {
        private int total;
        private int admis;
        private int redoublants;
        private int exclus;
        private double pourcentageAdmis;
        private double pourcentageRedoublants;
        private double pourcentageExclus;
        
        // Getters & Setters
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        
        public int getAdmis() { return admis; }
        public void setAdmis(int admis) { this.admis = admis; }
        
        public int getRedoublants() { return redoublants; }
        public void setRedoublants(int redoublants) { this.redoublants = redoublants; }
        
        public int getExclus() { return exclus; }
        public void setExclus(int exclus) { this.exclus = exclus; }
        
        public double getPourcentageAdmis() { return pourcentageAdmis; }
        public void setPourcentageAdmis(double pourcentageAdmis) { 
            this.pourcentageAdmis = pourcentageAdmis; 
        }
        
        public double getPourcentageRedoublants() { return pourcentageRedoublants; }
        public void setPourcentageRedoublants(double pourcentageRedoublants) { 
            this.pourcentageRedoublants = pourcentageRedoublants; 
        }
        
        public double getPourcentageExclus() { return pourcentageExclus; }
        public void setPourcentageExclus(double pourcentageExclus) { 
            this.pourcentageExclus = pourcentageExclus; 
        }
    }
    
    /**
     * Export statistics to Excel
     */
    public void exporterStatistiquesExcel(int idAnnee, String filePath) throws SQLException, IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Statistiques");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Programme");
            headerRow.createCell(1).setCellValue("Nb Etudiants");
            headerRow.createCell(2).setCellValue("Nb Admis");
            headerRow.createCell(3).setCellValue("Nb Redoublants");
            headerRow.createCell(4).setCellValue("Nb Exclus");
            headerRow.createCell(5).setCellValue("Moyenne Programme");
            headerRow.createCell(6).setCellValue("Taux Réussite");
            
            // Fill data
            List<StatProgramme> stats = getStatistiquesParProgramme(idAnnee);
            int rowNum = 1;
            for (StatProgramme stat : stats) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stat.getProgramme());
                row.createCell(1).setCellValue(stat.getNbEtudiants());
                row.createCell(2).setCellValue(stat.getNbAdmis());
                row.createCell(3).setCellValue(stat.getNbRedoublants());
                row.createCell(4).setCellValue(stat.getNbExclus());
                row.createCell(5).setCellValue(stat.getMoyenneProgramme());
                row.createCell(6).setCellValue(stat.getTauxReussite());
            }
            
            // Auto-size columns
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }
}