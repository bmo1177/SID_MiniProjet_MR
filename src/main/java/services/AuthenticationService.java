/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.UtilisateurDAO;
import models.Utilisateur;
import utils.PasswordUtils;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service d'authentification des utilisateurs
 * Gère la connexion, déconnexion, et gestion des comptes utilisateurs
 * Pattern Singleton pour une seule instance partagée
 */
public class AuthenticationService {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    private static volatile AuthenticationService instance;
    private final UtilisateurDAO utilisateurDAO;
    private Utilisateur currentUser;
    
    /**
     * Constructeur privé (Singleton)
     */
    private AuthenticationService() {
        this.utilisateurDAO = new UtilisateurDAO();
        LOGGER.info("🔒 Service d'authentification initialisé");
    }
    
    /**
     * Récupère l'instance unique thread-safe
     */
    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            synchronized (AuthenticationService.class) {
                if (instance == null) {
                    instance = new AuthenticationService();
                }
            }
        }
        return instance;
    }
    
    /**
     * Authentifie un utilisateur avec login et mot de passe
     * @param login Identifiant de l'utilisateur
     * @param password Mot de passe en clair
     * @return true si authentification réussie, false sinon
     */
    public boolean login(String login, String password) {
        if (login == null || password == null || login.trim().isEmpty() || password.trim().isEmpty()) {
            LOGGER.warning("Tentative de connexion avec des paramètres vides");
            return false;
        }
        
        try {
            login = login.trim().toLowerCase();
            Utilisateur user = utilisateurDAO.findByLogin(login);
            
            if (user == null) {
                LOGGER.warning("❌ Utilisateur non trouvé: " + login);
                return false;
            }
            
            if (!user.isActif()) {
                LOGGER.warning("❌ Compte désactivé: " + login);
                return false;
            }
            
            if (!verifyPassword(password, user.getPasswordHash())) {
                LOGGER.warning("❌ Mot de passe incorrect pour: " + login);
                return false;
            }
            
            // Connexion réussie
            currentUser = user;
            updateLastLogin(user.getIdUtilisateur());
            LOGGER.info(String.format("✅ Connexion réussie: %s (%s)", 
                user.getLogin(), user.getRole()));
            
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur base de données lors de l'authentification", e);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de l'authentification", e);
            return false;
        }
    }
    
    /**
     * Vérifie le mot de passe avec journalisation sécurisée
     */
    private boolean verifyPassword(String plainPassword, String storedHash) {
        try {
            boolean isValid = PasswordUtils.verifyPassword(plainPassword, storedHash);
            
            if (!isValid) {
                LOGGER.info(String.format("Tentative de connexion échouée - Hash entré: %s (tronqué), Hash stocké: %s (tronqué)",
                    PasswordUtils.hashPasswordSHA256(plainPassword).substring(0, 8) + "...",
                    storedHash.substring(0, 8) + "..."));
            }
            
            return isValid;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification du mot de passe", e);
            return false;
        }
    }
    
    /**
     * Met à jour la date de dernière connexion
     */
    private void updateLastLogin(int userId) {
        try {
            utilisateurDAO.updateLastLogin(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Impossible de mettre à jour la dernière connexion pour l'utilisateur ID: " + userId, e);
        }
    }
    
    /**
     * Déconnecte l'utilisateur actuel
     */
    public void logout() {
        if (currentUser != null) {
            String username = currentUser.getLogin();
            currentUser = null;
            LOGGER.info("🔓 Déconnexion réussie: " + username);
        }
    }
    
    /**
     * Retourne l'utilisateur connecté
     * @return Utilisateur connecté ou null si non connecté
     */
    public Utilisateur getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Vérifie si un utilisateur est connecté
     * @return true si connecté, false sinon
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     * @param role Rôle à vérifier
     * @return true si l'utilisateur a ce rôle, false sinon
     */
    public boolean hasRole(String role) {
        return currentUser != null && 
               role != null && 
               currentUser.getRole().equalsIgnoreCase(role.trim());
    }
    
    /**
     * Vérifie si l'utilisateur peut accéder à une fonctionnalité
     * @param allowedRoles Rôles autorisés
     * @return true si autorisé, false sinon
     */
    public boolean canAccess(String... allowedRoles) {
        if (currentUser == null || allowedRoles == null || allowedRoles.length == 0) {
            return false;
        }
        
        for (String role : allowedRoles) {
            if (role != null && currentUser.getRole().equalsIgnoreCase(role.trim())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Change le mot de passe de l'utilisateur connecté
     * @param oldPassword Ancien mot de passe
     * @param newPassword Nouveau mot de passe
     * @return true si changement réussi, false sinon
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            LOGGER.warning("Tentative de changement de mot de passe sans utilisateur connecté");
            return false;
        }
        
        if (oldPassword == null || newPassword == null) {
            LOGGER.warning("Paramètres null pour le changement de mot de passe");
            return false;
        }
        
        try {
            // Vérifier l'ancien mot de passe
            if (!PasswordUtils.verifyPassword(oldPassword, currentUser.getPasswordHash())) {
                LOGGER.warning("❌ Ancien mot de passe incorrect pour: " + currentUser.getLogin());
                return false;
            }
            
            // Valider le nouveau mot de passe
            if (!PasswordUtils.isValidPassword(newPassword)) {
                String errorMessage = PasswordUtils.getPasswordErrorMessage(newPassword);
                LOGGER.warning("❌ Nouveau mot de passe invalide: " + errorMessage);
                return false;
            }
            
            // Mettre à jour le mot de passe
            String newHash = PasswordUtils.hashPasswordSHA256(newPassword);
            boolean success = utilisateurDAO.updatePassword(
                currentUser.getIdUtilisateur(), 
                newHash
            );
            
            if (success) {
                currentUser.setPasswordHash(newHash);
                LOGGER.info("✅ Mot de passe changé avec succès pour: " + currentUser.getLogin());
            } else {
                LOGGER.warning("❌ Échec de la mise à jour du mot de passe pour: " + currentUser.getLogin());
            }
            
            return success;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur base de données lors du changement de mot de passe", e);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors du changement de mot de passe", e);
            return false;
        }
    }
    
    /**
     * Crée un nouveau compte utilisateur
     * @param newUser Utilisateur à créer
     * @param password Mot de passe initial
     * @return true si création réussie, false sinon
     */
    public boolean register(Utilisateur newUser, String password) {
        if (newUser == null || password == null) {
            LOGGER.warning("Paramètres null pour la création de compte");
            return false;
        }
        
        if (newUser.getLogin() == null || newUser.getLogin().trim().isEmpty()) {
            LOGGER.warning("Login vide pour la création de compte");
            return false;
        }
        
        try {
            String login = newUser.getLogin().trim().toLowerCase();
            
            // Vérifier si le login existe déjà
            if (utilisateurDAO.findByLogin(login) != null) {
                LOGGER.warning("❌ Login déjà utilisé: " + login);
                return false;
            }
            
            // Valider le mot de passe
            if (!PasswordUtils.isValidPassword(password)) {
                String errorMessage = PasswordUtils.getPasswordErrorMessage(password);
                LOGGER.warning("❌ Mot de passe invalide pour '" + login + "': " + errorMessage);
                return false;
            }
            
            // Hasher et définir le mot de passe
            newUser.setLogin(login);
            newUser.setPasswordHash(PasswordUtils.hashPasswordSHA256(password));
            
            // Insérer l'utilisateur
            int id = utilisateurDAO.insert(newUser);
            boolean success = id > 0;
            
            if (success) {
                newUser.setIdUtilisateur(id);
                LOGGER.info("✅ Compte créé avec succès: " + login + " (ID: " + id + ")");
            } else {
                LOGGER.warning("❌ Échec de la création du compte pour: " + login);
            }
            
            return success;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur base de données lors de la création du compte", e);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de la création du compte", e);
            return false;
        }
    }
    
    /**
     * Réinitialise le mot de passe d'un utilisateur
     * @param login Login de l'utilisateur
     * @return Nouveau mot de passe temporaire ou null en cas d'échec
     */
    public String resetPassword(String login) {
        if (login == null || login.trim().isEmpty()) {
            LOGGER.warning("Login vide pour la réinitialisation");
            return null;
        }
        
        try {
            login = login.trim().toLowerCase();
            Utilisateur user = utilisateurDAO.findByLogin(login);
            
            if (user == null) {
                LOGGER.warning("❌ Utilisateur non trouvé pour réinitialisation: " + login);
                return null;
            }
            
            // Générer un nouveau mot de passe sécurisé
            String newPassword = PasswordUtils.generatePassword(12);
            String hashedPassword = PasswordUtils.hashPasswordSHA256(newPassword);
            
            // Mettre à jour le mot de passe
            boolean success = utilisateurDAO.updatePassword(user.getIdUtilisateur(), hashedPassword);
            
            if (success) {
                LOGGER.info("✅ Mot de passe réinitialisé pour: " + login);
                return newPassword;
            } else {
                LOGGER.warning("❌ Échec de la réinitialisation du mot de passe pour: " + login);
                return null;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur base de données lors de la réinitialisation", e);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de la réinitialisation", e);
            return null;
        }
    }
}    
