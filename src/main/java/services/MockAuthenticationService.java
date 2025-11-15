/*
 * Service d'authentification de test
 * Fonctionne sans base de données pour démonstration
 */
package services;

import models.Utilisateur;
import utils.Logger;

public class MockAuthenticationService {
    private static MockAuthenticationService instance;
    private Utilisateur utilisateurConnecte;
    
    private MockAuthenticationService() {
        Logger.info("🔧 Service d'authentification MOCK initialisé");
    }
    
    public static synchronized MockAuthenticationService getInstance() {
        if (instance == null) {
            instance = new MockAuthenticationService();
        }
        return instance;
    }
    
    public Utilisateur login(String login, String motDePasse) {
        try {
            Thread.sleep(500); // Simuler délai réseau
            
            // Utilisateurs de test en dur
            if ("admin".equals(login) && "admin123".equals(motDePasse)) {
                utilisateurConnecte = createTestUser(1, "admin", "ADMIN");
            } else if ("etudiant".equals(login) && "etudiant123".equals(motDePasse)) {
                utilisateurConnecte = createTestUser(2, "etudiant", "ETUDIANT");
            } else if ("enseignant".equals(login) && "enseignant123".equals(motDePasse)) {
                utilisateurConnecte = createTestUser(3, "enseignant", "ENSEIGNANT");
            } else if ("scolarite".equals(login) && "scolarite123".equals(motDePasse)) {
                utilisateurConnecte = createTestUser(4, "scolarite", "SCOLARITE");
            } else if ("direction".equals(login) && "direction123".equals(motDePasse)) {
                utilisateurConnecte = createTestUser(5, "direction", "DIRECTION");
            } else {
                Logger.warning("❌ Tentative de connexion échouée pour: " + login);
                return null;
            }
            
            Logger.info("✅ Connexion réussie pour: " + login + " (rôle: " + utilisateurConnecte.getRole() + ")");
            return utilisateurConnecte;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Erreur lors de la simulation de connexion: " + e.getMessage());
            return null;
        }
    }
    
    private Utilisateur createTestUser(int id, String login, String role) {
        Utilisateur user = new Utilisateur();
        user.setIdUtilisateur(id);
        user.setLogin(login);
        user.setRole(role);
        user.setActif(true);
        return user;
    }
    
    public void logout() {
        if (utilisateurConnecte != null) {
            Logger.info("👋 Déconnexion de: " + utilisateurConnecte.getLogin());
            utilisateurConnecte = null;
        }
    }
    
    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    public boolean isConnected() {
        return utilisateurConnecte != null;
    }
    
    public boolean hasRole(String role) {
        return utilisateurConnecte != null && 
               utilisateurConnecte.getRole().equals(role);
    }
    
    public String[] getTestCredentials() {
        return new String[]{
            "Comptes de test disponibles:",
            "admin / admin123 (Administrateur)",
            "enseignant / enseignant123 (Enseignant)", 
            "etudiant / etudiant123 (Étudiant)",
            "scolarite / scolarite123 (Scolarité)",
            "direction / direction123 (Direction)"
        };
    }
}