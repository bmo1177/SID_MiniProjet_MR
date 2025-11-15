/*
 * Application de Gestion Scolarité - Point d'entrée principal
 * Université Ibn Khaldoun - Tiaret
 * Professional Education Management System with Modern UI
 */


import views.LoginViewTest;
import utils.Logger;
import javax.swing.*;
import java.awt.*;

public class Main {
    
    public static void main(String[] args) {
        // Configuration du logging
        Logger.info("🚀 Démarrage de l'application Gestion Scolarité - MODE TEST");
        Logger.info("📍 Université Ibn Khaldoun - Tiaret");
        Logger.info("🔧 Version de démonstration sans base de données");
        
        // Configuration Look and Feel
        configureLookAndFeel();
        
        // Configuration des propriétés système
        configureSystemProperties();
        
        // Démarrage de l'interface utilisateur
        SwingUtilities.invokeLater(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                // Créer et afficher la fenêtre de connexion de test
                LoginViewTest loginView = new LoginViewTest();
                loginView.setVisible(true);
                
                long loadTime = System.currentTimeMillis() - startTime;
                Logger.info("✅ Application TEST chargée en " + loadTime + "ms");
                Logger.info("🎯 Interface utilisateur prête - MODE DÉMONSTRATION");
                
                // Afficher les informations de test
                showTestInfo();
                
            } catch (Exception e) {
                Logger.error("❌ Erreur critique lors du démarrage: " + e.getMessage());
                e.printStackTrace();
                
                JOptionPane.showMessageDialog(null,
                    "Erreur lors du démarrage de l'application:\n" + e.getMessage(),
                    "Erreur Critique",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
    
    private static void configureLookAndFeel() {
        // Initialize modern theme system
        config.ThemeManager.initializeTheme();
        
        try {
            
            // Configuration des couleurs personnalisées
            UIManager.put("Button.background", new Color(52, 152, 219));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Panel.background", new Color(236, 240, 241));
            UIManager.put("Table.gridColor", new Color(189, 195, 199));
            UIManager.put("Table.selectionBackground", new Color(52, 152, 219));
            UIManager.put("Table.selectionForeground", Color.WHITE);
            
            Logger.info("🎨 Look and Feel système appliqué avec succès");
            
        } catch (Exception e) {
            Logger.warning("⚠️ Impossible d'appliquer le Look and Feel système: " + e.getMessage());
            // Continuer avec le Look and Feel par défaut
        }
    }
    
    private static void configureSystemProperties() {
        // Configuration pour l'affichage optimal
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            // Use default Metal LAF for Java 23 compatibility
            System.setProperty("swing.defaultlaf", "javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            Logger.warning("Could not set system LAF property: " + e.getMessage());
        }
        
        // Configuration pour les hautes résolutions
        System.setProperty("sun.java2d.dpiaware", "true");
        System.setProperty("sun.java2d.uiScale", "1.0");
        
        Logger.info("⚙️ Propriétés système configurées");
    }
    
    private static void showTestInfo() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                "🎓 GESTION SCOLARITÉ - MODE TEST\n\n" +
                "Cette version de démonstration fonctionne sans base de données.\n\n" +
                "📋 COMPTES DE TEST DISPONIBLES:\n" +
                "• admin / admin123 (Administrateur)\n" +
                "• enseignant / enseignant123 (Enseignant)\n" +
                "• etudiant / etudiant123 (Étudiant)\n" +
                "• scolarite / scolarite123 (Scolarité)\n" +
                "• direction / direction123 (Direction)\n\n" +
                "✨ Toutes les interfaces sont fonctionnelles en mode simulation.",
                "Mode Démonstration",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
}