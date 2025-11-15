/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.etudiant;

import models.*;
import services.*;
import dao.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Dashboard pour les étudiants
 * Affiche: informations personnelles, notes, moyennes, statut
 */
public class EtudiantDashboard extends JPanel {
    private Utilisateur currentUser;
    private Etudiant etudiant;
    private JTabbedPane tabbedPane;
    private EtudiantDAO etudiantDAO;
    private NoteService noteService;
    
    public EtudiantDashboard(Utilisateur user) {
        this.currentUser = user;
        this.etudiantDAO = new EtudiantDAO();
        this.noteService = new NoteService();
        
        loadEtudiantData();
        initComponents();
    }
    
    private void loadEtudiantData() {
        try {
            etudiant = etudiantDAO.findById(currentUser.getIdEtudiant());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des données: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // En-tête avec informations de l'étudiant
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Onglets pour les différentes sections
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setBackground(Color.WHITE);
        
        tabbedPane.addTab("📊 Vue d'ensemble", createOverviewPanel());
        tabbedPane.addTab("📝 Mes Notes", createNotesPanel());
        tabbedPane.addTab("👤 Informations", createInfoPanel());
        tabbedPane.addTab("📄 Bulletin", createBulletinPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Informations principales
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel lblName = new JLabel(etudiant != null ? etudiant.getNomComplet() : "Nom inconnu");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblName.setForeground(new Color(44, 62, 80));
        
        JLabel lblOrigine = new JLabel("Origine: " + (etudiant != null ? etudiant.getOrigineScolaire() : "-"));
        lblOrigine.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblOrigine.setForeground(new Color(127, 140, 141));
        
        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblOrigine);
        
        panel.add(infoPanel, BorderLayout.WEST);
        
        // Statistiques rapides
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        
        try {
            double moyenneGenerale = noteService.getMoyenneGenerale(etudiant.getIdEtudiant(), 2); // Année active
            String statut = noteService.getStatut(etudiant.getIdEtudiant(), 2);
            int nbMatieresValidees = noteService.countMatieresValidees(etudiant.getIdEtudiant(), 2);
            
            statsPanel.add(createStatCard("Moyenne Générale", 
                String.format("%.2f/20", moyenneGenerale), 
                new Color(52, 152, 219)));
            statsPanel.add(createStatCard("Statut", statut, getStatutColor(statut)));
            statsPanel.add(createStatCard("Matières Validées", 
                String.valueOf(nbMatieresValidees), 
                new Color(46, 204, 113)));
        } catch (Exception e) {
            // Valeurs par défaut si erreur
            statsPanel.add(createStatCard("Moyenne Générale", "N/A", Color.GRAY));
            statsPanel.add(createStatCard("Statut", "N/A", Color.GRAY));
            statsPanel.add(createStatCard("Matières Validées", "0", Color.GRAY));
        }
        
        panel.add(statsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(Color.WHITE);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(255, 255, 255, 200));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(lblValue);
        card.add(Box.createVerticalStrut(5));
        card.add(lblTitle);
        
        return card;
    }
    
    private Color getStatutColor(String statut) {
        if (statut == null) return Color.GRAY;
        switch (statut.toLowerCase()) {
            case "admis": return new Color(46, 204, 113);
            case "redoublant": return new Color(230, 126, 34);
            case "exclu": return new Color(231, 76, 60);
            default: return Color.GRAY;
        }
    }
    
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("📊 Tableau de Bord");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Graphique ou statistiques
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        contentPanel.setOpaque(false);
        
        contentPanel.add(createInfoCard("Année en cours", "2025/2026"));
        contentPanel.add(createInfoCard("Programme", "ING2 - Génie Informatique"));
        contentPanel.add(createInfoCard("Semestre actuel", "Semestre 1"));
        contentPanel.add(createInfoCard("Nombre d'épreuves", "12"));
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(127, 140, 141));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValue.setForeground(new Color(44, 62, 80));
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createNotesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Titre
        JLabel lblTitle = new JLabel("📝 Mes Notes par Épreuve");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Tableau des notes
        String[] columns = {"Matière", "Type d'épreuve", "Note", "Coefficient", "Date", "Enseignant"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Charger les notes
        try {
            List<NoteEpreuve> notes = noteService.getNotesEtudiant(etudiant.getIdEtudiant(), 2);
            for (NoteEpreuve note : notes) {
                model.addRow(new Object[]{
                    note.getMatiereName(),
                    note.getTypeEpreuve(),
                    String.format("%.2f/20", note.getNote()),
                    note.getCoefficient(),
                    note.getDateEpreuve(),
                    note.getEnseignantName()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des notes: " + e.getMessage());
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("👤 Mes Informations Personnelles");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        if (etudiant != null) {
            addInfoRow(formPanel, "Nom complet:", etudiant.getNomComplet());
            addInfoRow(formPanel, "Date de naissance:", etudiant.getDateNaissance() != null ? etudiant.getDateNaissance().toString() : "Non renseignée");
            addInfoRow(formPanel, "Origine scolaire:", etudiant.getOrigineScolaire());
            addInfoRow(formPanel, "Email:", etudiant.getEmail() != null ? etudiant.getEmail() : "Non renseigné");
            addInfoRow(formPanel, "Téléphone:", etudiant.getTelephone() != null ? etudiant.getTelephone() : "Non renseigné");
            addInfoRow(formPanel, "Date d'inscription:", etudiant.getDateInscription() != null ? etudiant.getDateInscription().toString() : "Non renseignée");
        }
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lblField = new JLabel(label);
        lblField.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblField.setForeground(new Color(52, 73, 94));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblValue.setForeground(new Color(127, 140, 141));
        
        panel.add(lblField);
        panel.add(lblValue);
    }
    
    private JPanel createBulletinPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("📄 Bulletin de Notes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        JButton btnGenerate = new JButton("Générer mon bulletin");
        btnGenerate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGenerate.setBackground(new Color(52, 152, 219));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);
        btnGenerate.addActionListener(e -> generateBulletin());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnGenerate);
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void generateBulletin() {
        if (etudiant == null) {
            JOptionPane.showMessageDialog(this,
                "Aucune information étudiant disponible",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Dialog pour choisir l'emplacement
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le bulletin");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers PDF", "pdf"));
        fileChooser.setSelectedFile(new java.io.File("bulletin_" + 
            etudiant.getNom().toLowerCase() + "_" + 
            etudiant.getPrenom().toLowerCase() + ".pdf"));
        
        int userChoice = fileChooser.showSaveDialog(this);
        if (userChoice != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        String outputPath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!outputPath.toLowerCase().endsWith(".pdf")) {
            outputPath += ".pdf";
        }
        
        // Génération en arrière-plan
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                BulletinService bulletinService = new BulletinService();
                services.BulletinPDFService pdfService = new services.BulletinPDFService();
                
                // Récupérer les données du bulletin
                BulletinService.BulletinData bulletinData = bulletinService.genererBulletin(
                    etudiant.getIdEtudiant(), 1, 2); // Programme et année par défaut
                
                // Générer le PDF
                return pdfService.generateBulletinPDF(bulletinData, 
                    fileChooser.getSelectedFile().getParent());
            }
            
            @Override
            protected void done() {
                try {
                    String fileName = get();
                    JOptionPane.showMessageDialog(EtudiantDashboard.this,
                        "Bulletin généré avec succès !\nFichier: " + fileName,
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Proposer d'ouvrir le fichier
                    int choice = JOptionPane.showConfirmDialog(
                        EtudiantDashboard.this,
                        "Bulletin généré avec succès !\nVoulez-vous l'ouvrir ?",
                        "Génération terminée",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (choice == JOptionPane.YES_OPTION) {
                        try {
                            Desktop.getDesktop().open(new java.io.File(fileName));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(EtudiantDashboard.this,
                                "Impossible d'ouvrir le fichier automatiquement.\n" +
                                "Fichier sauvegardé : " + fileName,
                                "Information", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(EtudiantDashboard.this,
                        "Erreur lors de la génération du bulletin :\n" + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
        
        // Afficher un dialogue de progression
        JDialog progressDialog = new JDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Génération du bulletin", true);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Génération en cours...");
        
        progressDialog.add(progressBar);
        progressDialog.setSize(300, 80);
        progressDialog.setLocationRelativeTo(this);
        
        worker.addPropertyChangeListener(evt -> {
            if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                progressDialog.dispose();
            }
        });
        
        progressDialog.setVisible(true);
    }
}