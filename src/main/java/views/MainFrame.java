package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private String username;
    private String role;
    private JMenuBar menuBar;
    private JPanel contentPanel;
    
    public MainFrame(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Gestion Scolarité - " + username + " (" + role + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Créer la barre de menu selon le rôle
        menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // Panel de contenu principal
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 246, 247));
        
        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));
        header.setPreferredSize(new Dimension(1000, 80));
        
        JLabel lblWelcome = new JLabel("🎉 Bienvenue, " + username + " !", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblWelcome.setForeground(Color.WHITE);
        
        JLabel lblRole = new JLabel("Rôle: " + role.toUpperCase(), SwingConstants.CENTER);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRole.setForeground(new Color(255, 255, 255, 200));
        
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(lblWelcome);
        header.add(Box.createVerticalStrut(5));
        header.add(lblRole);
        
        // Dashboard principal
        JPanel dashboard = new JPanel();
        dashboard.setLayout(new GridLayout(2, 3, 20, 20));
        dashboard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Cards pour différentes fonctionnalités
        String[] cardTitles = {"📈 Tableau de bord", "👥 Gestion étudiants", "📚 Gestion matières", 
                              "📝 Notes et examens", "📋 Inscriptions", "⚙️ Paramètres"};
        Color[] cardColors = {new Color(52, 152, 219), new Color(46, 204, 113), new Color(155, 89, 182),
                             new Color(241, 196, 15), new Color(230, 126, 34), new Color(44, 62, 80)};
        
        for (int i = 0; i < cardTitles.length; i++) {
            JPanel card = createDashboardCard(cardTitles[i], cardColors[i]);
            dashboard.add(card);
        }
        
        // Statistiques rapides
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createLineBorder(new Color(236, 240, 241), 1));
        
        statsPanel.add(createStatCard("Étudiants", "152", new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Enseignants", "24", new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Matières", "48", new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Programmes", "12", new Color(230, 126, 34)));
        
        // Ajouter au panel de contenu
        contentPanel.add(header, BorderLayout.NORTH);
        contentPanel.add(dashboard, BorderLayout.CENTER);
        contentPanel.add(statsPanel, BorderLayout.SOUTH);
        
        add(contentPanel);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Fichier
        JMenu menuFile = new JMenu("📋 Fichier");
        menuFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem menuItemExit = new JMenuItem("🔒 Déconnexion");
        menuItemExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment vous déconnecter?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginView().setVisible(true);
            }
        });
        
        JMenuItem menuItemQuit = new JMenuItem("❌ Quitter");
        menuItemQuit.addActionListener(e -> System.exit(0));
        
        menuFile.add(menuItemExit);
        menuFile.add(menuItemQuit);
        menuBar.add(menuFile);
        
        // Menu selon le rôle
        switch (role.toLowerCase()) {
            case "admin":
                addAdminMenu(menuBar);
                break;
            case "scolarite":
                addRegistrationMenu(menuBar);
                break;
            case "enseignant":
                addTeacherMenu(menuBar);
                break;
            case "etudiant":
                addStudentMenu(menuBar);
                break;
        }
        
        // Menu Aide
        JMenu menuHelp = new JMenu("❓ Aide");
        menuHelp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem menuItemAbout = new JMenuItem("ℹ️ À propos");
        menuItemAbout.addActionListener(e -> showAboutDialog());
        
        JMenuItem menuItemHelp = new JMenuItem("📖 Manuel d'utilisation");
        menuItemHelp.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Manuel d'utilisation en cours de développement...",
            "Aide",
            JOptionPane.INFORMATION_MESSAGE));
        
        menuHelp.add(menuItemAbout);
        menuHelp.add(menuItemHelp);
        menuBar.add(menuHelp);
        
        return menuBar;
    }
    
    private void addAdminMenu(JMenuBar menuBar) {
        JMenu menuAdmin = new JMenu("👑 Administration");
        menuAdmin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem menuItemUsers = new JMenuItem("👥 Gestion des utilisateurs");
        JMenuItem menuItemRoles = new JMenuItem("🔑 Gestion des rôles");
        JMenuItem menuItemBackup = new JMenuItem("💾 Sauvegardes");
        
        menuAdmin.add(menuItemUsers);
        menuAdmin.add(menuItemRoles);
        menuAdmin.add(menuItemBackup);
        menuBar.add(menuAdmin);
    }
    
    private void addRegistrationMenu(JMenuBar menuBar) {
        JMenu menuReg = new JMenu("📝 Scolarité");
        menuReg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem menuItemStudents = new JMenuItem("👥 Gestion étudiants");
        JMenuItem menuItemPrograms = new JMenuItem("📚 Programmes");
        JMenuItem menuItemInscriptions = new JMenuItem("📋 Inscriptions");
        
        menuReg.add(menuItemStudents);
        menuReg.add(menuItemPrograms);
        menuReg.add(menuItemInscriptions);
        menuBar.add(menuReg);
    }
    
    private void addTeacherMenu(JMenuBar menuBar) {
        JMenu menuTeacher = new JMenu("👨‍🏫 Enseignant");
        menuTeacher.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem menuItemCourses = new JMenuItem("📖 Mes cours");
        JMenuItem menuItemGrades = new JMenuItem("📊 Notes");
        JMenuItem menuItemExams = new JMenuItem("📝 Examens");
        
        menuTeacher.add(menuItemCourses);
        menuTeacher.add(menuItemGrades);
        menuTeacher.add(menuItemExams);
        menuBar.add(menuTeacher);
    }
    
    private void addStudentMenu(JMenuBar menuBar) {
        JMenu menuStudent = new JMenu("🎓 Étudiant");
        menuStudent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JMenuItem menuItemMyGrades = new JMenuItem("📊 Mes notes");
        JMenuItem menuItemMyCourses = new JMenuItem("📖 Mes cours");
        JMenuItem menuItemRegistration = new JMenuItem("📋 Inscription");
        
        menuStudent.add(menuItemMyGrades);
        menuStudent.add(menuItemMyCourses);
        menuStudent.add(menuItemRegistration);
        menuBar.add(menuStudent);
    }
    
    private JPanel createDashboardCard(String title, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(236, 240, 241), 1));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Header de la carte
        JPanel cardHeader = new JPanel();
        cardHeader.setBackground(color);
        cardHeader.setPreferredSize(new Dimension(200, 50));
        
        JLabel lblIcon = new JLabel("📊", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        lblIcon.setForeground(Color.WHITE);
        
        cardHeader.add(lblIcon);
        card.add(cardHeader, BorderLayout.NORTH);
        
        // Contenu de la carte
        JPanel cardContent = new JPanel();
        cardContent.setLayout(new BoxLayout(cardContent, BoxLayout.Y_AXIS));
        cardContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(52, 73, 94));
        
        JLabel lblDescription = new JLabel("Cliquer pour accéder", SwingConstants.CENTER);
        lblDescription.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescription.setForeground(new Color(127, 140, 141));
        
        cardContent.add(lblTitle);
        cardContent.add(Box.createVerticalStrut(10));
        cardContent.add(lblDescription);
        card.add(cardContent, BorderLayout.CENTER);
        
        // Effet hover
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(color, 2));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(236, 240, 241), 1));
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this,
                    "Fonctionnalité '" + title + "' en cours de développement",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        return card;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(color);
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(127, 140, 141));
        
        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "À propos", true);
        aboutDialog.setSize(400, 300);
        aboutDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblLogo = new JLabel("🎓", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        
        JLabel lblTitle = new JLabel("Gestion Scolarité", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel lblInfo = new JLabel("<html><center>" +
            "Version 1.0<br>" +
            "Université Ibn Khladoun - Tiaret<br>" +
            "Département d'Informatique<br>" +
            "3ème année Licence ISIL<br>" +
            "Module: SID - Systèmes d'Information et Bases de Données<br><br>" +
            "© 2025 - Tous droits réservés</center></html>");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton btnClose = new JButton("Fermer");
        btnClose.setBackground(new Color(52, 152, 219));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> aboutDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnClose);
        
        panel.add(lblLogo, BorderLayout.NORTH);
        panel.add(lblTitle, BorderLayout.CENTER);
        panel.add(lblInfo, BorderLayout.SOUTH);
        panel.add(buttonPanel, BorderLayout.AFTER_LAST_LINE);
        
        aboutDialog.add(panel);
        aboutDialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame("admin", "admin").setVisible(true);
        });
    }
}