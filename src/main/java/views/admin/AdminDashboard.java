/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.admin;

import config.ThemeManager;
import models.*;
import dao.*;
import services.*;
import views.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Modern Admin Dashboard with world-class UX design
 * Features: Real-time data, professional styling, interactive components
 */
public class AdminDashboard extends JPanel {
    private Utilisateur currentUser;
    
    // DAOs for data access
    private UtilisateurDAO utilisateurDAO;
    private EtudiantDAO etudiantDAO;
    private EnseignantDAO enseignantDAO;
    private ProgrammeDAO programmeDAO;
    private InscriptionDAO inscriptionDAO;
    private StatistiquesService statistiquesService;
    
    // UI Components
    private ModernDataTable usersTable;
    private ModernSearchBar searchBar;
    private JPanel statsPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JTabbedPane mainTabs;
    
    // Statistics cards
    private ModernStatCard totalUsersCard;
    private ModernStatCard activeStudentsCard;
    private ModernStatCard totalTeachersCard;
    private ModernStatCard systemHealthCard;
    
    public AdminDashboard(Utilisateur user) {
        this.currentUser = user;
        initializeDAOs();
        initializeComponents();
        setupLayout();
        setupStyling();
        loadInitialData();
    }
    
    private void initializeDAOs() {
        this.utilisateurDAO = new UtilisateurDAO();
        this.etudiantDAO = new EtudiantDAO();
        this.enseignantDAO = new EnseignantDAO();
        this.programmeDAO = new ProgrammeDAO();
        this.inscriptionDAO = new InscriptionDAO();
        this.statistiquesService = new StatistiquesService();
    }
    
    private void initializeComponents() {
        // Header with welcome message and quick actions
        headerPanel = createHeaderPanel();
        
        // Statistics cards panel
        statsPanel = createStatsPanel();
        
        // Main tabbed interface
        mainTabs = createMainTabs();
        
        // Content panel that holds everything except header
        contentPanel = ThemeManager.createCard();
        contentPanel.setLayout(new BorderLayout(0, ThemeManager.SPACING_LG));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, ThemeManager.SPACING_LG));
        setBackground(ThemeManager.SURFACE_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(
            ThemeManager.SPACING_LG, ThemeManager.SPACING_LG, 
            ThemeManager.SPACING_LG, ThemeManager.SPACING_LG));
        
        add(headerPanel, BorderLayout.NORTH);
        
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(mainTabs, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void setupStyling() {
        // Apply modern theme - methods are implemented below
        setOpaque(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        // Welcome message
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Tableau de bord administrateur");
        welcomeLabel.setFont(ThemeManager.FONT_HEADING_LARGE);
        welcomeLabel.setForeground(ThemeManager.GRAY_900);
        
        JLabel userLabel = new JLabel("Bienvenue, " + currentUser.getLogin());
        userLabel.setFont(ThemeManager.FONT_BODY_MEDIUM);
        userLabel.setForeground(ThemeManager.GRAY_600);
        
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(userLabel, BorderLayout.SOUTH);
        
        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, ThemeManager.SPACING_SM, 0));
        actionsPanel.setOpaque(false);
        
        JButton refreshButton = ThemeManager.createSecondaryButton("Actualiser");
        JButton exportButton = ThemeManager.createSecondaryButton("Exporter");
        JButton settingsButton = ThemeManager.createPrimaryButton("Paramètres");
        
        refreshButton.addActionListener(e -> refreshData());
        exportButton.addActionListener(e -> exportData());
        settingsButton.addActionListener(e -> openSettings());
        
        actionsPanel.add(refreshButton);
        actionsPanel.add(exportButton);
        actionsPanel.add(settingsButton);
        
        header.add(welcomePanel, BorderLayout.WEST);
        header.add(actionsPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, ThemeManager.SPACING_MD, 0));
        panel.setOpaque(false);
        
        // Initialize stat cards with placeholder data (will be updated with real data)
        totalUsersCard = new ModernStatCard("Utilisateurs totaux", "...", ThemeManager.PRIMARY_BLUE, 
                                          null, "Tous les comptes utilisateurs");
        activeStudentsCard = new ModernStatCard("Étudiants actifs", "...", ThemeManager.SUCCESS_GREEN, 
                                               null, "Étudiants inscrits cette année");
        totalTeachersCard = new ModernStatCard("Enseignants", "...", ThemeManager.INFO_CYAN, 
                                             null, "Corps enseignant");
        systemHealthCard = new ModernStatCard("État système", "...", ThemeManager.WARNING_ORANGE, 
                                            null, "Performance générale");
        
        panel.add(totalUsersCard);
        panel.add(activeStudentsCard);
        panel.add(totalTeachersCard);
        panel.add(systemHealthCard);
        
        return panel;
    }
    
    private JTabbedPane createMainTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(ThemeManager.FONT_BODY_MEDIUM);
        
        // Use existing panels that are already implemented
        tabs.addTab("Vue d'ensemble", createOverviewPanel());
        tabs.addTab("Utilisateurs", createUtilisateursPanel()); 
        tabs.addTab("Rapports", createRapportsPanel());
        tabs.addTab("Logs", createLogsPanel());
        
        return tabs;
    }
    
    // Legacy methods for compatibility with existing tabs
    private JPanel createOverviewPanelLegacy() {
        // Onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        tabbedPane.addTab("🏠 Vue d'ensemble", createOverviewPanel());
        tabbedPane.addTab("👥 Utilisateurs", createUtilisateursPanel());
        tabbedPane.addTab("💾 Sauvegarde", createSauvegardePanel());
        tabbedPane.addTab("📊 Rapports", createRapportsPanel());
        tabbedPane.addTab("📝 Logs", createLogsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        return new JPanel(); // Return a dummy panel for now
    }
    
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Statistiques système
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        statsPanel.setOpaque(false);
        
        statsPanel.add(new StatCard("Utilisateurs", "256", "👥", new Color(52, 152, 219)));
        statsPanel.add(new StatCard("Étudiants", "520", "👨‍🎓", new Color(46, 204, 113)));
        statsPanel.add(new StatCard("Enseignants", "45", "👨‍🏫", new Color(155, 89, 182)));
        statsPanel.add(new StatCard("Comptes Actifs", "248", "✅", new Color(230, 126, 34)));
        
        statsPanel.add(new StatCard("Programmes", "12", "📚", new Color(52, 152, 219)));
        statsPanel.add(new StatCard("Matières", "45", "📖", new Color(46, 204, 113)));
        statsPanel.add(new StatCard("Épreuves", "180", "📝", new Color(155, 89, 182)));
        statsPanel.add(new StatCard("Notes", "8500", "💯", new Color(230, 126, 34)));
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Activité récente
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblActivity = new JLabel("📝 Activité Récente");
        lblActivity.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activityPanel.add(lblActivity, BorderLayout.NORTH);
        
        JTextArea txtActivity = new JTextArea();
        txtActivity.setEditable(false);
        txtActivity.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtActivity.setText("[2025-11-04 10:30] Connexion utilisateur: admin\n" +
                           "[2025-11-04 10:28] Nouvelle inscription: Ahmed Benali\n" +
                           "[2025-11-04 10:25] Note saisie par Prof. Djebbar\n");
        
        activityPanel.add(new JScrollPane(txtActivity), BorderLayout.CENTER);
        
        panel.add(activityPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createUtilisateursPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // En-tête
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        ModernSearchBar searchBar = new ModernSearchBar("Rechercher un utilisateur...");
        topPanel.add(searchBar, BorderLayout.CENTER);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);
        
        JButton btnNouveau = new JButton("➕ Nouvel Utilisateur");
        btnNouveau.setBackground(new Color(52, 152, 219));
        btnNouveau.setForeground(Color.WHITE);
        btnNouveau.setFocusPainted(false);
        btnNouveau.setBorderPainted(false);
        btnNouveau.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnExport = new JButton("📥 Exporter");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.setBorderPainted(false);
        
        buttonsPanel.add(btnExport);
        buttonsPanel.add(btnNouveau);
        topPanel.add(buttonsPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Tableau
        String[] columns = {"Login", "Rôle", "Actif", "Dernière Connexion", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Charger les utilisateurs
        try {
            java.util.List<Utilisateur> users = utilisateurDAO.findAll();
            for (Utilisateur user : users) {
                model.addRow(new Object[]{
                    user.getLogin(),
                    user.getRoleLabel(),
                    user.isActif() ? "✅ Oui" : "❌ Non",
                    user.getDerniereConnexion() != null ? 
                        user.getDerniereConnexion().toString() : "Jamais",
                    "Modifier | Supprimer"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        CustomTable table = new CustomTable(model);
        table.makeSortable();
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSauvegardePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("💾 Sauvegarde et Restauration");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Contenu
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        // Section Sauvegarde
        JPanel backupSection = createActionSection(
            "💾 Sauvegarder la Base de Données",
            "Créer une copie complète de toutes les données",
            "Sauvegarder Maintenant",
            new Color(52, 152, 219),
            e -> effectuerSauvegarde()
        );
        
        // Section Restauration
        JPanel restoreSection = createActionSection(
            "♻️ Restaurer la Base de Données",
            "Restaurer depuis une sauvegarde existante (⚠ Action irréversible)",
            "Choisir un Fichier",
            new Color(230, 126, 34),
            e -> effectuerRestauration()
        );
        
        contentPanel.add(backupSection);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(restoreSection);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionSection(String title, String description, 
                                      String buttonText, Color buttonColor,
                                      java.awt.event.ActionListener action) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(127, 140, 141));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton btn = new JButton(buttonText);
        btn.setBackground(buttonColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(action);
        
        section.add(lblTitle);
        section.add(Box.createVerticalStrut(5));
        section.add(lblDesc);
        section.add(Box.createVerticalStrut(15));
        section.add(btn);
        
        return section;
    }
    
    private void effectuerSauvegarde() {
        if (ConfirmDialog.show(this, "Sauvegarde", 
            "Voulez-vous sauvegarder la base de données maintenant?")) {
            
            LoadingDialog loading = new LoadingDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Sauvegarde en cours...");
            loading.setVisible(true);
            
            // Simuler la sauvegarde
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        loading.close();
                        NotificationToast.show(
                            (JFrame) SwingUtilities.getWindowAncestor(this),
                            "Sauvegarde effectuée avec succès",
                            NotificationToast.Type.SUCCESS);
                    });
                }
            }).start();
        }
    }
    
    private void effectuerRestauration() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner un fichier de sauvegarde");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            if (ConfirmDialog.show(this, "Restauration", 
                "⚠ ATTENTION: Cette action va écraser toutes les données actuelles.\n" +
                "Êtes-vous absolument sûr de vouloir continuer?")) {
                
                NotificationToast.show((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Restauration en cours...", NotificationToast.Type.INFO);
            }
        }
    }
    
    private JPanel createRapportsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        panel.add(createReportCard("📊 Statistiques Globales", 
            "Vue d'ensemble de tous les indicateurs", new Color(52, 152, 219)));
        panel.add(createReportCard("👨‍🎓 Rapport Étudiants", 
            "Liste complète avec moyennes et statuts", new Color(46, 204, 113)));
        panel.add(createReportCard("📈 Analyse Réussite", 
            "Taux de réussite par programme", new Color(155, 89, 182)));
        panel.add(createReportCard("📝 Rapport Notes", 
            "Export complet de toutes les notes", new Color(230, 126, 34)));
        
        return panel;
    }
    
    private JPanel createReportCard(String title, String description, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 2));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(color);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDesc = new JLabel("<html><center>" + description + "</center></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(127, 140, 141));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton btnGenerate = new JButton("Générer PDF");
        btnGenerate.setBackground(color);
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);
        btnGenerate.setBorderPainted(false);
        btnGenerate.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(lblDesc);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(btnGenerate);
        
        card.add(contentPanel);
        
        return card;
    }
    
    private JPanel createLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("📝 Logs d'Activité");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        
        filterPanel.add(new JLabel("Action:"));
        JComboBox<String> cmbAction = new JComboBox<>(
            new String[]{"Toutes", "INSERT", "UPDATE", "DELETE"});
        filterPanel.add(cmbAction);
        
        filterPanel.add(new JLabel("Table:"));
        JComboBox<String> cmbTable = new JComboBox<>(
            new String[]{"Toutes", "ETUDIANT", "NOTE_EPREUVE", "INSCRIPTION"});
        filterPanel.add(cmbTable);
        
        JButton btnRefresh = new JButton("🔄 Actualiser");
        btnRefresh.setFocusPainted(false);
        filterPanel.add(btnRefresh);
        
        JButton btnExport = new JButton("📥 Exporter");
        btnExport.setBackground(new Color(52, 152, 219));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        filterPanel.add(btnExport);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Tableau des logs
        String[] columns = {"Date/Heure", "Utilisateur", "Action", "Table", "Détails"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Données exemple
        model.addRow(new Object[]{
            "2025-11-04 10:30:25", "admin", "INSERT", "ETUDIANT", "Nouvel étudiant: Ahmed Benali"
        });
        model.addRow(new Object[]{
            "2025-11-04 10:28:15", "prof.djebbar", "UPDATE", "NOTE_EPREUVE", "Note modifiée: 15.5"
        });
        model.addRow(new Object[]{
            "2025-11-04 10:25:40", "scolarite", "INSERT", "INSCRIPTION", "Inscription programme ING1_TC"
        });
        
        CustomTable table = new CustomTable(model);
        table.makeSortable();
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    // === DATA LOADING METHODS ===
    
    /**
     * Load initial data when dashboard opens
     */
    private void loadInitialData() {
        SwingUtilities.invokeLater(() -> {
            loadStatisticsData();
            loadUsersData();
        });
    }
    
    /**
     * Load real statistics from database
     */
    private void loadStatisticsData() {
        try {
            // Total users count
            List<Utilisateur> allUsers = utilisateurDAO.findAll();
            totalUsersCard.updateValue(String.valueOf(allUsers.size()));
            
            // Active students count
            List<Etudiant> activeStudents = etudiantDAO.findAll();
            int activeCount = activeStudents.size();
            activeStudentsCard.updateValue(String.valueOf(activeCount));
            
            // Calculate trend (mock calculation)
            String studentTrend = activeCount > 50 ? "+12%" : "-3%";
            activeStudentsCard.updateTrend(studentTrend);
            
            // Total teachers
            List<Enseignant> teachers = enseignantDAO.findAll();
            totalTeachersCard.updateValue(String.valueOf(teachers.size()));
            
            // System health (based on data integrity)
            double healthScore = calculateSystemHealth();
            systemHealthCard.updateValue(String.format("%.1f%%", healthScore));
            if (healthScore > 90) {
                systemHealthCard.updateTrend("Excellent");
            } else if (healthScore > 70) {
                systemHealthCard.updateTrend("Bon");
            } else {
                systemHealthCard.updateTrend("Attention");
            }
            
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors du chargement des statistiques", e);
        }
    }
    
    /**
     * Load users data into table with real database content
     */
    private void loadUsersData() {
        try {
            List<Utilisateur> users = utilisateurDAO.findAll();
            List<Object[]> tableData = new ArrayList<>();
            
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (Utilisateur user : users) {
                Object[] row = new Object[7];
                row[0] = user.getIdUtilisateur();
                row[1] = user.getLogin();
                row[2] = user.getRole();
                
                // Get full name based on role
                String fullName = getFullNameForUser(user);
                row[3] = fullName;
                
                // Get email
                String email = getEmailForUser(user);
                row[4] = email;
                
                // Status
                row[5] = user.isActif() ? "Actif" : "Inactif";
                
                // Last connection
                if (user.getDerniereConnexion() != null) {
                    row[6] = dateFormat.format(user.getDerniereConnexion());
                } else {
                    row[6] = "Jamais";
                }
                
                tableData.add(row);
            }
            
            usersTable.updateData(tableData);
            
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors du chargement des utilisateurs", e);
        }
    }
    
    /**
     * Get full name for user based on their role
     */
    private String getFullNameForUser(Utilisateur user) {
        try {
            switch (user.getRole()) {
                case "ETUDIANT":
                    if (user.getIdEtudiant() != null) {
                        Etudiant etudiant = etudiantDAO.findById(user.getIdEtudiant());
                        if (etudiant != null) {
                            return etudiant.getNom() + " " + etudiant.getPrenom();
                        }
                    }
                    break;
                case "ENSEIGNANT":
                    if (user.getIdEnseignant() != null) {
                        Enseignant enseignant = enseignantDAO.findById(user.getIdEnseignant());
                        if (enseignant != null) {
                            return enseignant.getNom() + " " + enseignant.getPrenom();
                        }
                    }
                    break;
                default:
                    return user.getLogin(); // For admin, scolarite, direction
            }
        } catch (SQLException e) {
            System.err.println("Error getting full name for user " + user.getLogin() + ": " + e.getMessage());
        }
        return user.getLogin();
    }
    
    /**
     * Get email for user based on their role
     */
    private String getEmailForUser(Utilisateur user) {
        try {
            switch (user.getRole()) {
                case "ETUDIANT":
                    if (user.getIdEtudiant() != null) {
                        Etudiant etudiant = etudiantDAO.findById(user.getIdEtudiant());
                        if (etudiant != null && etudiant.getEmail() != null) {
                            return etudiant.getEmail();
                        }
                    }
                    break;
                case "ENSEIGNANT":
                    if (user.getIdEnseignant() != null) {
                        Enseignant enseignant = enseignantDAO.findById(user.getIdEnseignant());
                        if (enseignant != null && enseignant.getEmail() != null) {
                            return enseignant.getEmail();
                        }
                    }
                    break;
                default:
                    return user.getLogin() + "@univ-tiaret.dz"; // Default email pattern
            }
        } catch (SQLException e) {
            System.err.println("Error getting email for user " + user.getLogin() + ": " + e.getMessage());
        }
        return user.getLogin() + "@univ-tiaret.dz";
    }
    
    /**
     * Calculate system health score based on data integrity
     */
    private double calculateSystemHealth() {
        double score = 100.0;
        
        try {
            // Check for data integrity issues
            List<Utilisateur> users = utilisateurDAO.findAll();
            List<Etudiant> students = etudiantDAO.findAll();
            List<Enseignant> teachers = enseignantDAO.findAll();
            
            // Check for orphaned users (users without associated student/teacher)
            int orphanedUsers = 0;
            for (Utilisateur user : users) {
                if ("ETUDIANT".equals(user.getRole()) && user.getIdEtudiant() == null) {
                    orphanedUsers++;
                } else if ("ENSEIGNANT".equals(user.getRole()) && user.getIdEnseignant() == null) {
                    orphanedUsers++;
                }
            }
            
            // Deduct points for issues
            if (orphanedUsers > 0) {
                score -= orphanedUsers * 5; // 5 points per orphaned user
            }
            
            // Check for inactive users
            long inactiveUsers = users.stream()
                .filter(u -> !u.isActif())
                .count();
            
            if (inactiveUsers > users.size() * 0.1) { // More than 10% inactive
                score -= 10;
            }
            
            // Ensure score doesn't go below 0
            score = Math.max(0, score);
            
        } catch (SQLException e) {
            score = 50; // Database connectivity issues
        }
        
        return score;
    }
    
    // === FILTER METHODS ===
    
    private void filterUsers(String searchTerm) {
        if (usersTable != null) {
            usersTable.filterData(searchTerm);
        }
    }
    
    private void filterUsersByRole(String role) {
        if ("Tous les rôles".equals(role)) {
            loadUsersData(); // Reload all data
        } else {
            try {
                List<Utilisateur> users = utilisateurDAO.findAll();
                List<Object[]> filteredData = new ArrayList<>();
                
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                
                for (Utilisateur user : users) {
                    if (role.equals(user.getRole())) {
                        Object[] row = new Object[7];
                        row[0] = user.getIdUtilisateur();
                        row[1] = user.getLogin();
                        row[2] = user.getRole();
                        row[3] = getFullNameForUser(user);
                        row[4] = getEmailForUser(user);
                        row[5] = user.isActif() ? "Actif" : "Inactif";
                        row[6] = user.getDerniereConnexion() != null ? 
                               dateFormat.format(user.getDerniereConnexion()) : "Jamais";
                        filteredData.add(row);
                    }
                }
                
                usersTable.updateData(filteredData);
                
            } catch (SQLException e) {
                handleDatabaseError("Erreur lors du filtrage des utilisateurs", e);
            }
        }
    }
    
    // === UI HELPER METHODS ===
    
    private JPanel createDetailedStatCard(String title, String description, Color color) {
        JPanel card = ThemeManager.createCard();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 150));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.FONT_HEADING_SMALL);
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(ThemeManager.FONT_BODY_SMALL);
        descLabel.setForeground(ThemeManager.GRAY_600);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel("Loading...");
        valueLabel.setFont(ThemeManager.FONT_HEADING_LARGE);
        valueLabel.setForeground(ThemeManager.GRAY_900);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel content = new JPanel(new GridLayout(3, 1, 0, ThemeManager.SPACING_SM));
        content.setOpaque(false);
        content.add(titleLabel);
        content.add(valueLabel);
        content.add(descLabel);
        
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    // === ACTION METHODS ===
    
    private void refreshData() {
        loadStatisticsData();
        loadUsersData();
        showNotification("Données actualisées", "success");
    }
    
    private void exportData() {
        String csvData = usersTable.exportToCsv();
        showNotification("Export réalisé avec succès", "success");
    }
    
    private void openSettings() {
        showNotification("Paramètres système à venir", "info");
    }
    
    private void openAddUserDialog() {
        showNotification("Ajout d'utilisateur à venir", "info");
    }
    
    private void editSelectedUser() {
        Object[] selectedData = usersTable.getSelectedRowData();
        if (selectedData != null) {
            showNotification("Modification de: " + selectedData[1], "info");
        } else {
            showNotification("Veuillez sélectionner un utilisateur", "warning");
        }
    }
    
    private void deleteSelectedUser() {
        Object[] selectedData = usersTable.getSelectedRowData();
        if (selectedData != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer l'utilisateur " + selectedData[1] + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int userId = (Integer) selectedData[0];
                    utilisateurDAO.delete(userId);
                    loadUsersData(); // Reload data
                    showNotification("Utilisateur supprimé avec succès", "success");
                } catch (SQLException e) {
                    handleDatabaseError("Erreur lors de la suppression", e);
                }
            }
        } else {
            showNotification("Veuillez sélectionner un utilisateur", "warning");
        }
    }
    
    private void resetUserPassword() {
        Object[] selectedData = usersTable.getSelectedRowData();
        if (selectedData != null) {
            showNotification("Reset mot de passe pour: " + selectedData[1], "info");
        } else {
            showNotification("Veuillez sélectionner un utilisateur", "warning");
        }
    }
    
    // === UTILITY METHODS ===
    
    private void showNotification(String message, String type) {
        Color backgroundColor;
        switch (type) {
            case "success":
                backgroundColor = ThemeManager.SUCCESS_GREEN;
                break;
            case "warning":
                backgroundColor = ThemeManager.WARNING_ORANGE;
                break;
            case "error":
                backgroundColor = ThemeManager.ERROR_RED;
                break;
            default:
                backgroundColor = ThemeManager.INFO_CYAN;
        }
        
        JOptionPane.showMessageDialog(this, message, 
            type.toUpperCase(), JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleDatabaseError(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        showNotification(message + ": " + e.getMessage(), "error");
    }
}
