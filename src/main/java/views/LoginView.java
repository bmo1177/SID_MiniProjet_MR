package views;

import services.AuthenticationService;
import models.Utilisateur;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;

public class LoginView extends JFrame {
    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox chkRememberMe;
    private JLabel lblStatus;
    private JProgressBar progressBar;
    private AuthenticationService authService;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public LoginView() {
        authService = AuthenticationService.getInstance();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Gestion Scolarité - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel principal avec gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                Color color1 = new Color(52, 152, 219);
                Color color2 = new Color(41, 128, 185);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        
        // Panel de connexion (carte blanche)
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setPreferredSize(new Dimension(350, 420));
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        
        // Logo et titre
        JLabel lblTitle = new JLabel("🎓 Connexion", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(52, 73, 94));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Système de Gestion de Scolarité", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(127, 140, 141));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginPanel.add(lblTitle);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(lblSubtitle);
        loginPanel.add(Box.createVerticalStrut(20));
        
        // Statut
        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(52, 152, 219));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(lblStatus);
        loginPanel.add(Box.createVerticalStrut(10));
        
        // Barre de progression (initialement invisible)
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(300, 5));
        progressBar.setBackground(new Color(41, 128, 185));
        progressBar.setForeground(new Color(52, 152, 219));
        loginPanel.add(progressBar);
        loginPanel.add(Box.createVerticalStrut(15));
        
        // Champ login
        JLabel lblLogin = new JLabel("Identifiant");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLogin.setForeground(new Color(52, 73, 94));
        lblLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtLogin = new JTextField();
        txtLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtLogin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtLogin.setText("admin");
        
        loginPanel.add(lblLogin);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(txtLogin);
        loginPanel.add(Box.createVerticalStrut(15));
        
        // Champ mot de passe
        JLabel lblPassword = new JLabel("Mot de passe");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(new Color(52, 73, 94));
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtPassword.setText("admin123");
        
        // Enter pour se connecter
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });
        
        loginPanel.add(lblPassword);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(txtPassword);
        loginPanel.add(Box.createVerticalStrut(10));
        
        // Remember me
        chkRememberMe = new JCheckBox("Se souvenir de moi");
        chkRememberMe.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkRememberMe.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkRememberMe.setBackground(Color.WHITE);
        loginPanel.add(chkRememberMe);
        loginPanel.add(Box.createVerticalStrut(25));
        
        // Bouton connexion
        btnLogin = new JButton("Se connecter");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> login());
        
        // Effet hover
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(52, 152, 219));
            }
        });
        
        loginPanel.add(btnLogin);
        loginPanel.add(Box.createVerticalStrut(15));
        
        // Lien mot de passe oublié
        JLabel lblForgot = new JLabel("<html><u>Mot de passe oublié?</u></html>", SwingConstants.CENTER);
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblForgot.setForeground(new Color(52, 152, 219));
        lblForgot.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetPassword();
            }
        });
        
        loginPanel.add(lblForgot);
        
        mainPanel.add(loginPanel);
        add(mainPanel);
        
        // Ajouter un shutdown hook pour fermer l'executor
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
        }));
    }
    
    private void login() {
        String login = txtLogin.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        
        if (login.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }
        
        // Désactiver le bouton et afficher la progression
        setLoginUIEnabled(false);
        lblStatus.setText("🔄 Connexion en cours...");
        lblStatus.setForeground(new Color(52, 152, 219));
        progressBar.setVisible(true);
        
        // Soumettre la tâche d'authentification au thread pool
        Future<Boolean> authFuture = executor.submit(() -> {
            try {
                return authService.login(login, password);
            } catch (Exception e) {
                System.err.println("Erreur d'authentification: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
        
        // Vérifier le résultat périodiquement sans bloquer l'UI
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authFuture.isDone()) {
                    ((Timer)e.getSource()).stop();
                    
                    try {
                        boolean success = authFuture.get();
                        handleLoginResult(success, login);
                    } catch (Exception ex) {
                        handleLoginResult(false, login);
                    }
                }
            }
        });
        timer.start();
    }
    
    private void handleLoginResult(boolean success, String login) {
        SwingUtilities.invokeLater(() -> {
            if (success) {
                Utilisateur user = authService.getCurrentUser();
                lblStatus.setText("✅ Connexion réussie !");
                lblStatus.setForeground(new Color(46, 204, 113));
                
                // Utiliser les paramètres corrects pour MainFrame
                String username = user.getLogin();
                String role = user.getRole();
                
                // Fermer après un court délai pour montrer le succès
                Timer successTimer = new Timer(1000, e1 -> {
                    dispose();
                    openMainFrame(username, role);
                });
                successTimer.setRepeats(false);
                successTimer.start();
                
            } else {
                lblStatus.setText("❌ Identifiant ou mot de passe incorrect");
                lblStatus.setForeground(new Color(231, 76, 60));
                setLoginUIEnabled(true);
            }
            
            progressBar.setVisible(false);
        });
    }
    
    private void setLoginUIEnabled(boolean enabled) {
        txtLogin.setEnabled(enabled);
        txtPassword.setEnabled(enabled);
        btnLogin.setEnabled(enabled);
        chkRememberMe.setEnabled(enabled);
    }
    
    private void resetPassword() {
        String login = JOptionPane.showInputDialog(this, 
            "Entrez votre identifiant pour réinitialiser le mot de passe:",
            "Réinitialisation de mot de passe",
            JOptionPane.QUESTION_MESSAGE);
        
        if (login != null && !login.trim().isEmpty()) {
            setLoginUIEnabled(false);
            lblStatus.setText("🔄 Réinitialisation en cours...");
            lblStatus.setForeground(new Color(52, 152, 219));
            progressBar.setVisible(true);
            
            Future<String> resetFuture = executor.submit(() -> {
                try {
                    return authService.resetPassword(login.trim());
                } catch (Exception e) {
                    System.err.println("Erreur réinitialisation: " + e.getMessage());
                    return null;
                }
            });
            
            Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (resetFuture.isDone()) {
                        ((Timer)e.getSource()).stop();
                        
                        try {
                            String newPassword = resetFuture.get();
                            handleResetResult(newPassword, login);
                        } catch (Exception ex) {
                            handleResetResult(null, login);
                        }
                    }
                }
            });
            timer.start();
        }
    }
    
    private void handleResetResult(String newPassword, String login) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setVisible(false);
            setLoginUIEnabled(true);
            
            if (newPassword != null) {
                JOptionPane.showMessageDialog(this,
                    "✅ Mot de passe réinitialisé avec succès!\n\n" +
                    "Votre nouveau mot de passe temporaire est:\n" +
                    newPassword + "\n\n" +
                    "Connectez-vous avec ce mot de passe et changez-le immédiatement.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                
                txtLogin.setText(login.trim());
                txtPassword.setText(newPassword);
            } else {
                showError("❌ Impossible de réinitialiser le mot de passe pour cet identifiant");
            }
        });
    }
    
    private void openMainFrame(String username, String role) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(username, role);
            mainFrame.setVisible(true);
        });
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Erreur de connexion", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        // Fermer proprement l'executor
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
    
    public static void main(String[] args) {
        try {
            // Configurer le Look and Feel
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("PasswordField.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("CheckBox.font", new Font("Segoe UI", Font.PLAIN, 11));
            UIManager.put("ProgressBar.selectionBackground", new Color(52, 152, 219));
            UIManager.put("ProgressBar.selectionForeground", Color.WHITE);
            
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}