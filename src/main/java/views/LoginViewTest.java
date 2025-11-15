/*
 * Version de test de la vue de connexion
 * Fonctionne sans base de données
 */
package views;

import services.MockAuthenticationService;
import models.Utilisateur;
import views.admin.AdminDashboard;
import views.direction.DirectionDashboard;
import views.enseignant.EnseignantDashboard;
import views.etudiant.EtudiantDashboard;
import views.scolarite.ScolariteDashboard;
import views.components.NotificationToast;
import utils.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginViewTest extends JFrame {
    private MockAuthenticationService authService;
    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;
    private JProgressBar progressBar;

    public LoginViewTest() {
        this.authService = MockAuthenticationService.getInstance();
        
        initComponents();
        setupEventHandlers();
        
        Logger.info("🔐 Vue de connexion TEST initialisée");
    }
    
    private void initComponents() {
        setTitle("🎓 Gestion Scolarité - Université Ibn Khaldoun (MODE TEST)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Icône de l'application
        try {
            setIconImage(Toolkit.getDefaultToolkit().createImage("icon.png"));
        } catch (Exception e) {
            // Icône par défaut si non trouvée
        }
        
        // Panel principal avec gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(52, 152, 219),
                    0, getHeight(), new Color(41, 128, 185)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Panel de connexion
        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        
        // Panel d'information test
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        // Titre
        JLabel titleLabel = new JLabel("GESTION SCOLARITÉ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Université Ibn Khaldoun - Tiaret");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel testLabel = new JLabel("MODE TEST - DÉMONSTRATION");
        testLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        testLabel.setForeground(new Color(230, 126, 34));
        testLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(testLabel);
        panel.add(Box.createVerticalStrut(30));
        
        // Formulaire de connexion dans un panneau blanc
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setMaximumSize(new Dimension(350, 280));
        
        // Champ login
        JLabel lblLogin = new JLabel("Nom d'utilisateur :");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtLogin = new JTextField();
        txtLogin.setPreferredSize(new Dimension(270, 35));
        txtLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLogin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Champ mot de passe
        JLabel lblPassword = new JLabel("Mot de passe :");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(270, 35));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Bouton de connexion
        btnLogin = new JButton("Se connecter");
        btnLogin.setPreferredSize(new Dimension(270, 40));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Barre de progression
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Connexion en cours...");
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        // Label de statut
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Assembler le formulaire
        formPanel.add(lblLogin);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtLogin);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(lblPassword);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtPassword);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(progressBar);
        formPanel.add(btnLogin);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(lblStatus);
        
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(formPanel);
        
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setOpaque(false);
        
        JButton btnInfo = new JButton("ℹ️ Comptes de test");
        btnInfo.setBackground(new Color(52, 152, 219));
        btnInfo.setForeground(Color.WHITE);
        btnInfo.setFocusPainted(false);
        btnInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnInfo.addActionListener(e -> showTestAccounts());
        
        panel.add(btnInfo);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Connexion au clic
        btnLogin.addActionListener(e -> login());
        
        // Connexion avec Entrée
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        };
        
        txtLogin.addKeyListener(enterKeyListener);
        txtPassword.addKeyListener(enterKeyListener);
        
        // Focus automatique
        SwingUtilities.invokeLater(() -> txtLogin.requestFocus());
    }
    
    private void login() {
        String login = txtLogin.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (login.isEmpty() || password.isEmpty()) {
            showError("Veuillez saisir le nom d'utilisateur et le mot de passe");
            return;
        }
        
        // Interface de chargement
        btnLogin.setEnabled(false);
        progressBar.setVisible(true);
        lblStatus.setText("");
        
        // Authentification en arrière-plan
        SwingWorker<Utilisateur, Void> worker = new SwingWorker<Utilisateur, Void>() {
            @Override
            protected Utilisateur doInBackground() throws Exception {
                return authService.login(login, password);
            }
            
            @Override
            protected void done() {
                try {
                    Utilisateur utilisateur = get();
                    progressBar.setVisible(false);
                    btnLogin.setEnabled(true);
                    
                    if (utilisateur != null) {
                        showSuccess("Connexion réussie !");
                        ouvrirDashboard(utilisateur);
                    } else {
                        showError("Nom d'utilisateur ou mot de passe incorrect");
                        txtPassword.setText("");
                        txtLogin.selectAll();
                        txtLogin.requestFocus();
                    }
                    
                } catch (Exception e) {
                    progressBar.setVisible(false);
                    btnLogin.setEnabled(true);
                    showError("Erreur lors de la connexion: " + e.getMessage());
                    Logger.error("Erreur login: " + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    private void ouvrirDashboard(Utilisateur utilisateur) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame dashboardFrame = new JFrame("Gestion Scolarité - " + utilisateur.getRole());
                JPanel dashboard = null;
                
                switch (utilisateur.getRole()) {
                    case "ADMIN":
                        dashboard = new AdminDashboard(utilisateur);
                        break;
                    case "ETUDIANT":
                        // Créer un utilisateur étudiant de test
                        models.Utilisateur userEtudiant = new models.Utilisateur();
                        userEtudiant.setIdUtilisateur(1);
                        userEtudiant.setLogin("etudiant");
                        userEtudiant.setRole("ETUDIANT");
                        userEtudiant.setIdEtudiant(1);
                        userEtudiant.setActif(true);
                        dashboard = new EtudiantDashboard(userEtudiant);
                        break;
                    case "ENSEIGNANT":
                        // Créer un utilisateur enseignant de test
                        models.Utilisateur userEnseignant = new models.Utilisateur();
                        userEnseignant.setIdUtilisateur(2);
                        userEnseignant.setLogin("enseignant");
                        userEnseignant.setRole("ENSEIGNANT");
                        userEnseignant.setIdEnseignant(1);
                        userEnseignant.setActif(true);
                        dashboard = new EnseignantDashboard(userEnseignant);
                        break;
                    case "SCOLARITE":
                        dashboard = new ScolariteDashboard(utilisateur);
                        break;
                    case "DIRECTION":
                        dashboard = new DirectionDashboard(utilisateur);
                        break;
                    default:
                        showError("Rôle utilisateur non reconnu: " + utilisateur.getRole());
                        return;
                }
                
                if (dashboard != null) {
                    dashboardFrame.setContentPane(dashboard);
                    dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    dashboardFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    dashboardFrame.setVisible(true);
                    this.dispose();
                    Logger.info("✅ Dashboard " + utilisateur.getRole() + " ouvert");
                }
                
            } catch (Exception e) {
                showError("Erreur lors de l'ouverture du dashboard: " + e.getMessage());
                Logger.error("Erreur dashboard: " + e.getMessage());
            }
        });
    }
    
    private void showError(String message) {
        lblStatus.setText(message);
        lblStatus.setForeground(new Color(231, 76, 60));
        
        NotificationToast.show(this, message, NotificationToast.Type.ERROR);
    }
    
    private void showSuccess(String message) {
        lblStatus.setText(message);
        lblStatus.setForeground(new Color(46, 204, 113));
        
        NotificationToast.show(this, message, NotificationToast.Type.SUCCESS);
    }
    
    private void showTestAccounts() {
        String[] accounts = authService.getTestCredentials();
        StringBuilder message = new StringBuilder();
        
        for (String account : accounts) {
            message.append(account).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, 
            message.toString(),
            "Comptes de Test Disponibles",
            JOptionPane.INFORMATION_MESSAGE);
    }
}