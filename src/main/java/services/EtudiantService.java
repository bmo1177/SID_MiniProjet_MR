/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.*;
import models.*;
import utils.PasswordUtils;
import java.sql.*;
import java.util.List;

public class EtudiantService {
    private EtudiantDAO etudiantDAO;
    private InscriptionDAO inscriptionDAO;
    private UtilisateurDAO utilisateurDAO;
    private BaseDAO<?> baseDAO; // Pour la gestion des transactions
    
    public EtudiantService() {
        this.etudiantDAO = new EtudiantDAO();
        this.inscriptionDAO = new InscriptionDAO();
        this.utilisateurDAO = new UtilisateurDAO();
        this.baseDAO = new BaseDAO<Object>() {
            @Override
            public List<Object> findAll() throws SQLException { return null; }
            @Override
            public Object findById(int id) throws SQLException { return null; }
            @Override
            public int insert(Object entity) throws SQLException { return 0; }
            @Override
            public boolean update(Object entity) throws SQLException { return false; }
            @Override
            public boolean delete(int id) throws SQLException { return false; }
        };
    }
    
    /**
     * Inscrit un nouvel étudiant avec création de compte
     * Gère les transactions pour assurer l'intégrité des données
     */
    public int inscrireEtudiant(Etudiant etudiant, String password, 
                               int idProgramme, int idAnnee) throws SQLException {
        Connection conn = null;
        boolean originalAutoCommit = true;
        
        try {
            // Obtenir la connexion et configurer les transactions
            conn = baseDAO.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            
            // 1. Valider les données de l'étudiant
            if (etudiant == null) {
                throw new IllegalArgumentException("L'objet étudiant ne peut pas être null");
            }
            
            if (etudiant.getEmail() == null || !etudiant.getEmail().contains("@")) {
                throw new IllegalArgumentException("Email invalide pour l'étudiant");
            }
            
            // 2. Insérer l'étudiant
            int idEtudiant = etudiantDAO.insert(etudiant);
            if (idEtudiant <= 0) {
                throw new SQLException("Échec de l'insertion de l'étudiant");
            }
            System.out.println("✅ Étudiant inséré avec ID: " + idEtudiant);
            
            // 3. Créer le compte utilisateur
            String login = etudiant.getEmail().split("@")[0].toLowerCase();
            
            // Vérifier si le login existe déjà
            Utilisateur existingUser = utilisateurDAO.findByLogin(login);
            if (existingUser != null) {
                // Ajouter un suffixe numérique si le login existe
                int counter = 1;
                String newLogin;
                do {
                    newLogin = login + counter;
                    existingUser = utilisateurDAO.findByLogin(newLogin);
                    counter++;
                } while (existingUser != null);
                login = newLogin;
            }
            
            Utilisateur user = new Utilisateur();
            user.setLogin(login);
            user.setPasswordHash(PasswordUtils.hashPasswordSHA256(password)); // Utiliser PasswordUtils au lieu d'AuthenticationService
            user.setRole("etudiant");
            user.setIdEtudiant(idEtudiant);
            user.setActif(true);
            
            int idUtilisateur = utilisateurDAO.insert(user);
            if (idUtilisateur <= 0) {
                throw new SQLException("Échec de la création du compte utilisateur");
            }
            System.out.println("✅ Compte utilisateur créé avec login: " + login);
            
            // 4. Créer l'inscription
            Inscription inscription = new Inscription();
            inscription.setIdEtudiant(idEtudiant);
            inscription.setIdProgramme(idProgramme);
            inscription.setIdAnnee(idAnnee);
            inscription.setDateInscription(java.time.LocalDate.now());
            
            int idInscription = inscriptionDAO.insert(inscription);
            if (idInscription <= 0) {
                throw new SQLException("Échec de la création de l'inscription");
            }
            System.out.println("✅ Inscription créée avec ID: " + idInscription);
            
            // 5. Commit de la transaction
            conn.commit();
            System.out.println("✅ Transaction commitée avec succès");
            
            return idEtudiant;
            
        } catch (SQLException | IllegalArgumentException e) {
            // Rollback en cas d'erreur
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("🔄 Rollback effectué");
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur lors du rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("❌ Erreur lors de l'inscription de l'étudiant: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            // Restaurer l'état original de la connexion
            if (conn != null) {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la restauration d'autoCommit: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Recherche des étudiants par critères multiples
     */
    public List<Etudiant> rechercherEtudiants(String critere) throws SQLException {
        if (critere == null || critere.trim().isEmpty()) {
            System.out.println("🔍 Recherche de tous les étudiants");
            return etudiantDAO.findAll();
        }
        
        System.out.println("🔍 Recherche d'étudiants avec critère: " + critere);
        List<Etudiant> resultats = etudiantDAO.search(critere);
        System.out.println("🔍 " + resultats.size() + " étudiants trouvés");
        return resultats;
    }
    
    /**
     * Récupère les inscriptions d'un étudiant
     */
    public List<Inscription> getInscriptionsEtudiant(int idEtudiant) throws SQLException {
        System.out.println("📚 Récupération des inscriptions pour l'étudiant ID: " + idEtudiant);
        
        String sql = "SELECT i.*, p.nom AS programme_name, a.libelle AS annee_scolaire " +
                     "FROM INSCRIPTION i " +
                     "JOIN PROGRAMME p ON i.id_programme = p.id_programme " +
                     "JOIN ANNEE_SCOLAIRE a ON i.id_annee = a.id_annee " +
                     "WHERE i.id_etudiant = ? ORDER BY a.date_debut DESC";
        
        try {
            List<Inscription> inscriptions = inscriptionDAO.executeQuery(sql, rs -> {
                Inscription ins = new Inscription();
                ins.setIdInscription(rs.getInt("id_inscription"));
                ins.setIdEtudiant(rs.getInt("id_etudiant"));
                ins.setIdProgramme(rs.getInt("id_programme"));
                ins.setIdAnnee(rs.getInt("id_annee"));
                
                // Gestion des valeurs NULL pour moyenne_generale
                if (rs.getObject("moyenne_generale") != null) {
                    ins.setMoyenneGenerale(rs.getDouble("moyenne_generale"));
                }
                
                // Gestion des valeurs NULL pour statut_fin_annee
                if (rs.getObject("statut_fin_annee") != null) {
                    ins.setStatutFinAnnee(rs.getString("statut_fin_annee"));
                }
                
                ins.setDateInscription(rs.getDate("date_inscription").toLocalDate());
                ins.setProgrammeName(rs.getString("programme_name"));
                ins.setAnneeScolaire(rs.getString("annee_scolaire"));
                return ins;
            }, idEtudiant);
            
            System.out.println("📚 " + inscriptions.size() + " inscriptions trouvées");
            return inscriptions;
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des inscriptions: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Met à jour les informations d'un étudiant
     */
    public boolean modifierEtudiant(Etudiant etudiant) throws SQLException {
        if (etudiant == null || etudiant.getIdEtudiant() <= 0) {
            throw new IllegalArgumentException("Étudiant invalide pour la mise à jour");
        }
        
        System.out.println("✏️ Mise à jour de l'étudiant ID: " + etudiant.getIdEtudiant());
        boolean success = etudiantDAO.update(etudiant);
        
        if (success) {
            System.out.println("✅ Étudiant mis à jour avec succès");
        } else {
            System.out.println("❌ Échec de la mise à jour de l'étudiant");
        }
        
        return success;
    }
    
    /**
     * Supprime un étudiant (et toutes ses données liées)
     */
    public boolean supprimerEtudiant(int idEtudiant) throws SQLException {
        if (idEtudiant <= 0) {
            throw new IllegalArgumentException("ID étudiant invalide");
        }
        
        System.out.println("🗑️ Suppression de l'étudiant ID: " + idEtudiant);
        boolean success = etudiantDAO.delete(idEtudiant);
        
        if (success) {
            System.out.println("✅ Étudiant supprimé avec succès");
        } else {
            System.out.println("❌ Échec de la suppression de l'étudiant");
        }
        
        return success;
    }
    
    /**
     * Compte le nombre d'étudiants
     */
    public int compterEtudiants() throws SQLException {
        int count = etudiantDAO.count();
        System.out.println("📊 Nombre total d'étudiants: " + count);
        return count;
    }
    
    /**
     * Récupère les étudiants par origine scolaire
     */
    public List<Etudiant> getEtudiantsParOrigine(String origine) throws SQLException {
        if (origine == null || origine.trim().isEmpty()) {
            throw new IllegalArgumentException("Origine scolaire invalide");
        }
        
        System.out.println("🏫 Récupération des étudiants en origine: " + origine);
        List<Etudiant> etudiants = etudiantDAO.findByOrigine(origine);
        System.out.println("🏫 " + etudiants.size() + " étudiants trouvés");
        return etudiants;
    }
    
    /**
     * Vérifie si un email existe déjà
     */
    public boolean emailExiste(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) AS count FROM ETUDIANT WHERE email = ?";
        List<Integer> resultats = etudiantDAO.executeQuery(sql, rs -> rs.getInt("count"), email.trim());
        
        return resultats != null && !resultats.isEmpty() && resultats.get(0) > 0;
    }
    
    /**
     * Génère un login unique à partir de l'email
     */
    private String genererLoginUnique(String emailBase) throws SQLException {
        String loginBase = emailBase.split("@")[0].toLowerCase().replaceAll("[^a-z0-9]", "");
        String login = loginBase;
        int compteur = 1;
        
        while (utilisateurDAO.findByLogin(login) != null) {
            login = loginBase + compteur;
            compteur++;
        }
        
        return login;
    }
}