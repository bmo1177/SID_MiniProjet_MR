/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.direction;


import models.*;
import services.*;
import views.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class DirectionDashboard extends JPanel {
    private Utilisateur currentUser;
    private ProgrammeService programmeService;
    private StatistiquesService statsService;
    
    public DirectionDashboard(Utilisateur user) {
        this.currentUser = user;
        this.programmeService = new ProgrammeService();
        this.statsService = new StatistiquesService();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // En-tête
        JLabel lblTitle = new JLabel("👔 Direction des Études");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);
        
        // Onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        tabbedPane.addTab("📊 Vue d'ensemble", createOverviewPanel());
        tabbedPane.addTab("📚 Programmes", createProgrammesPanel());
        tabbedPane.addTab("📖 Matières", createMatieresPanel());
        tabbedPane.addTab("✅ Validation", createValidationPanel());
        tabbedPane.addTab("📈 Statistiques", createStatistiquesPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Statistiques en haut
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        
        statsPanel.add(new StatCard("Programmes", "12", "📚", new Color(52, 152, 219)));
        statsPanel.add(new StatCard("Matières", "45", "📖", new Color(46, 204, 113)));
        statsPanel.add(new StatCard("Étudiants", "520", "👨‍🎓", new Color(155, 89, 182)));
        statsPanel.add(new StatCard("Taux Réussite", "78%", "✅", new Color(230, 126, 34)));
        
        panel.add(statsPanel, BorderLayout.NORTH);
        
        // Tableau récapitulatif
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTableTitle = new JLabel("📊 Répartition par Programme");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        centerPanel.add(lblTableTitle, BorderLayout.NORTH);
        
        String[] columns = {"Programme", "Étudiants", "Admis", "Redoublants", "Exclus", "Taux Réussite"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Données exemple
        model.addRow(new Object[]{"ING1 TC", "150", "120", "25", "5", "80%"});
        model.addRow(new Object[]{"ING2 GI", "85", "70", "12", "3", "82%"});
        model.addRow(new Object[]{"ISIN", "45", "38", "5", "2", "84%"});
        
        CustomTable table = new CustomTable(model);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProgrammesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // En-tête
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("📚 Gestion des Programmes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(lblTitle, BorderLayout.WEST);
        
        JButton btnNouveau = new JButton("➕ Nouveau Programme");
        btnNouveau.setBackground(new Color(52, 152, 219));
        btnNouveau.setForeground(Color.WHITE);
        btnNouveau.setFocusPainted(false);
        btnNouveau.setBorderPainted(false);
        btnNouveau.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNouveau.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNouveau.addActionListener(e -> ouvrirDialogNouveauProgramme());
        topPanel.add(btnNouveau, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Tableau
        String[] columns = {"Code", "Nom", "Année", "Nb Matières", "Nb Étudiants", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        
        // Données exemple
        model.addRow(new Object[]{"ING1_TC", "Ingénieur 1ère année - TC", "1", "8", "150", "Modifier | Supprimer"});
        model.addRow(new Object[]{"ING2_GI", "Ingénieur 2ème année - GI", "2", "6", "85", "Modifier | Supprimer"});
        model.addRow(new Object[]{"ISIN", "Ing. Systèmes Info et Réseaux", "3", "10", "45", "Modifier | Supprimer"});
        
        CustomTable table = new CustomTable(model);
        table.makeSortable();
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void ouvrirDialogNouveauProgramme() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Nouveau Programme", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        FormField fieldCode = new FormField("Code (ex: ING1_TC)", true);
        FormField fieldNom = new FormField("Nom complet", true);
        
        JLabel lblAnnee = new JLabel("Année d'étude *");
        JComboBox<String> cmbAnnee = new JComboBox<>(new String[]{"1", "2", "3"});
        
        JLabel lblDescription = new JLabel("Description");
        JTextArea txtDescription = new JTextArea(4, 20);
        txtDescription.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        
        formPanel.add(fieldCode);
        formPanel.add(fieldNom);
        formPanel.add(lblAnnee);
        formPanel.add(cmbAnnee);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(lblDescription);
        formPanel.add(new JScrollPane(txtDescription));
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(248, 249, 250));
        
        JButton btnSave = new JButton("Enregistrer");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> {
            dialog.dispose();
            NotificationToast.show((JFrame) SwingUtilities.getWindowAncestor(this),
                "Programme créé avec succès", NotificationToast.Type.SUCCESS);
        });
        
        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private JPanel createMatieresPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("📖 Gestion des Matières");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Tableau
        String[] columns = {"Nom", "Objectif", "Semestre", "Programmes", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        CustomTable table = new CustomTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createValidationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("✅ Validation des Moyennes et Statuts");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        
        filterPanel.add(new JLabel("Programme:"));
        JComboBox<String> cmbProgramme = new JComboBox<>();
        cmbProgramme.addItem("Tous");
        filterPanel.add(cmbProgramme);
        
        filterPanel.add(new JLabel("Année:"));
        JComboBox<String> cmbAnnee = new JComboBox<>();
        cmbAnnee.addItem("2025/2026");
        filterPanel.add(cmbAnnee);
        
        JButton btnValider = new JButton("✅ Valider Tout");
        btnValider.setBackground(new Color(46, 204, 113));
        btnValider.setForeground(Color.WHITE);
        btnValider.setFocusPainted(false);
        filterPanel.add(btnValider);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Tableau
        String[] columns = {"Étudiant", "Programme", "Moyenne", "Statut Calculé", "Valider"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        CustomTable table = new CustomTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatistiquesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Graphique 1 : Taux de réussite par programme
        JPanel chartPanel1 = new JPanel(new BorderLayout());
        chartPanel1.setBackground(Color.WHITE);
        chartPanel1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblChart1 = new JLabel("📊 Taux de Réussite par Programme", SwingConstants.CENTER);
        lblChart1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chartPanel1.add(lblChart1, BorderLayout.NORTH);
        
        JLabel lblPlaceholder1 = new JLabel("Graphique à implémenter (JFreeChart)", 
                                           SwingConstants.CENTER);
        lblPlaceholder1.setForeground(Color.GRAY);
        chartPanel1.add(lblPlaceholder1, BorderLayout.CENTER);
        
        // Graphique 2 : Évolution des effectifs
        JPanel chartPanel2 = new JPanel(new BorderLayout());
        chartPanel2.setBackground(Color.WHITE);
        chartPanel2.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblChart2 = new JLabel("📈 Évolution des Effectifs", SwingConstants.CENTER);
        lblChart2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chartPanel2.add(lblChart2, BorderLayout.NORTH);
        
        JLabel lblPlaceholder2 = new JLabel("Graphique à implémenter (JFreeChart)", 
                                           SwingConstants.CENTER);
        lblPlaceholder2.setForeground(Color.GRAY);
        chartPanel2.add(lblPlaceholder2, BorderLayout.CENTER);
        
        panel.add(chartPanel1);
        panel.add(chartPanel2);
        
        return panel;
    }
}
