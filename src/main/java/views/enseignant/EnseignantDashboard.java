/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.enseignant;


import models.*;
import dao.*;
import views.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Dashboard pour les enseignants
 * Fonctionnalités : Gestion des épreuves, saisie des notes, consultation des résultats
 */
public class EnseignantDashboard extends JPanel {
    private Utilisateur currentUser;
    private Enseignant enseignant;
    private JTabbedPane tabbedPane;
    
    private EnseignantDAO enseignantDAO;
    private EpreuveDAO epreuveDAO;
    private NoteEpreuveDAO noteEpreuveDAO;
    // Services/DAO complémentaires
    private services.NoteService noteService;
    private dao.NoteMatiereDAO noteMatiereDAO;

    // État pour la saisie des notes
    private JComboBox<Epreuve> cmbEpreuveNotes;
    private javax.swing.table.DefaultTableModel notesModel;
    private JTable notesTable;
    private Epreuve selectedEpreuve;
    
    public EnseignantDashboard(Utilisateur user) {
        this.currentUser = user;
        this.enseignantDAO = new EnseignantDAO();
        this.epreuveDAO = new EpreuveDAO();
        this.noteEpreuveDAO = new NoteEpreuveDAO();
        this.noteService = new services.NoteService();
        this.noteMatiereDAO = new dao.NoteMatiereDAO();
        
        loadEnseignantData();
        initComponents();
    }
    
    private void loadEnseignantData() {
        try {
            enseignant = enseignantDAO.findById(currentUser.getIdEnseignant());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // En-tête
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Onglets
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        tabbedPane.addTab("📊 Vue d'ensemble", createOverviewPanel());
        tabbedPane.addTab("📝 Mes Épreuves", createEpreuvesPanel());
        tabbedPane.addTab("✍️ Saisie des Notes", createSaisieNotesPanel());
        tabbedPane.addTab("📈 Résultats", createResultatsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Info enseignant
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel lblName = new JLabel(enseignant != null ? enseignant.getNomComplet() : "");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        JLabel lblGrade = new JLabel(enseignant != null ? enseignant.getGrade() : "");
        lblGrade.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblGrade.setForeground(new Color(127, 140, 141));
        
        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblGrade);
        
        panel.add(infoPanel, BorderLayout.WEST);
        
        // Statistiques rapides
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        
        try {
            int nbEpreuves = epreuveDAO.countByEnseignant(enseignant.getIdEnseignant());
            int nbNotesASaisir = noteEpreuveDAO.countNotesManquantes(enseignant.getIdEnseignant());
            int nbMatieresEnseigne = epreuveDAO.countMatieresEnseignees(enseignant.getIdEnseignant());
            
            statsPanel.add(createStatCard("Épreuves créées", String.valueOf(nbEpreuves), 
                new Color(52, 152, 219)));
            statsPanel.add(createStatCard("Notes à saisir", String.valueOf(nbNotesASaisir), 
                new Color(230, 126, 34)));
            statsPanel.add(createStatCard("Matières enseignées", String.valueOf(nbMatieresEnseigne), 
                new Color(46, 204, 113)));
        } catch (Exception e) {
            // Valeurs par défaut
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
    
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("📊 Tableau de Bord Enseignant");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Prochaines épreuves
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        contentPanel.setOpaque(false);
        
        contentPanel.add(createProchainEpreuvesPanel());
        contentPanel.add(createMatieresPanel());
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProchainEpreuvesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTitle = new JLabel("📅 Prochaines Épreuves");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Liste des épreuves à venir
        JTextArea txtEpreuves = new JTextArea();
        txtEpreuves.setEditable(false);
        txtEpreuves.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEpreuves.setText("- Examen Final - Algorithmique (20/11/2025)\n" +
                           "- Contrôle 2 - Base de Données (25/11/2025)\n" +
                           "- TP Noté - Réseaux (28/11/2025)");
        
        panel.add(new JScrollPane(txtEpreuves), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMatieresPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTitle = new JLabel("📚 Mes Matières");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        JTextArea txtMatieres = new JTextArea();
        txtMatieres.setEditable(false);
        txtMatieres.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMatieres.setText("- Algorithmique Avancée\n" +
                           "- Base de Données\n" +
                           "- Réseaux Informatiques");
        
        panel.add(new JScrollPane(txtMatieres), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEpreuvesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Titre et boutons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("📝 Gestion des Épreuves");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(lblTitle, BorderLayout.WEST);
        
        JButton btnNouvelle = new JButton("➕ Nouvelle Épreuve");
        btnNouvelle.setBackground(new Color(52, 152, 219));
        btnNouvelle.setForeground(Color.WHITE);
        btnNouvelle.setFocusPainted(false);
        btnNouvelle.addActionListener(e -> creerEpreuve());
        topPanel.add(btnNouvelle, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Tableau des épreuves
        String[] columns = {"Type", "Intitulé", "Matière", "Date", "Coefficient", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Seule la colonne Actions est éditable
            }
        };
        
        // Charger les épreuves
        try {
            List<Epreuve> epreuves = epreuveDAO.findByEnseignant(enseignant.getIdEnseignant());
            for (Epreuve ep : epreuves) {
                model.addRow(new Object[]{
                    ep.getTypeEpreuve(),
                    ep.getIntitule(),
                    ep.getMatiereName(),
                    ep.getDateEpreuve(),
                    ep.getCoefficient(),
                    "Modifier | Supprimer"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSaisieNotesPanel() {
        return new NoteSaisiePanel(enseignant);
    }
    
    private JPanel createResultatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("📈 Consultation des Résultats");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        
        filterPanel.add(new JLabel("Matière :"));
        JComboBox<String> cmbMatiere = new JComboBox<>(new String[]{"Toutes", "Algorithmique", "BDD"});
        filterPanel.add(cmbMatiere);
        
        JButton btnExport = new JButton("📊 Exporter Excel");
        btnExport.setBackground(new Color(52, 152, 219));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        filterPanel.add(btnExport);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Tableau statistiques
        String[] columns = {"Épreuve", "Nb Étudiants", "Moyenne", "Min", "Max", "Taux réussite"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        model.addRow(new Object[]{"Contrôle 1", "45", "12.5", "4.0", "18.5", "75%"});
        model.addRow(new Object[]{"Examen Final", "45", "11.8", "3.5", "19.0", "68%"});
        
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void creerEpreuve() {
        // Dialog pour créer une épreuve
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Nouvelle Épreuve", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        // Formulaire
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        formPanel.add(new JLabel("Type d'épreuve :"));
        JComboBox<String> cmbType = new JComboBox<>(
            new String[]{"Contrôle", "Examen", "TP", "Projet"});
        formPanel.add(cmbType);
        
        formPanel.add(new JLabel("Intitulé :"));
        JTextField txtIntitule = new JTextField();
        formPanel.add(txtIntitule);
        
        formPanel.add(new JLabel("Matière :"));
        JComboBox<String> cmbMatiere = new JComboBox<>();
        formPanel.add(cmbMatiere);
        
        formPanel.add(new JLabel("Date :"));
        JTextField txtDate = new JTextField();
        formPanel.add(txtDate);
        
        formPanel.add(new JLabel("Coefficient :"));
        JSpinner spinCoef = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 5.0, 0.5));
        formPanel.add(spinCoef);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Enregistrer");
        JButton btnCancel = new JButton("Annuler");
        
        btnSave.addActionListener(e -> {
            // Sauvegarder l'épreuve
            dialog.dispose();
            NotificationToast.show((JFrame) SwingUtilities.getWindowAncestor(this),
                "Épreuve créée avec succès", NotificationToast.Type.SUCCESS);
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void enregistrerNotes() {
        NotificationToast.show((JFrame) SwingUtilities.getWindowAncestor(this),
            "Notes enregistrées avec succès", NotificationToast.Type.SUCCESS);
    }
}

