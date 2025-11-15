/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.*;
import java.sql.*;
import java.util.List;
import models.Utilisateur;

public class UtilisateurDAO extends BaseDAO<Utilisateur> {
    
    @Override
    public List<Utilisateur> findAll() throws SQLException {
        String sql = "SELECT * FROM UTILISATEUR ORDER BY login";
        return executeQuery(sql, this::mapUtilisateur);
    }
    
    @Override
    public Utilisateur findById(int id) throws SQLException {
        String sql = "SELECT * FROM UTILISATEUR WHERE id_utilisateur = ?";
        List<Utilisateur> results = executeQuery(sql, this::mapUtilisateur, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public Utilisateur findByLogin(String login) throws SQLException {
        String sql = "SELECT * FROM UTILISATEUR WHERE login = ?";
        List<Utilisateur> results = executeQuery(sql, this::mapUtilisateur, login);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public int insert(Utilisateur user) throws SQLException {
        String sql = "INSERT INTO UTILISATEUR (login, password_hash, role, id_etudiant, " +
                     "id_enseignant, actif) VALUES (?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql, user.getLogin(), user.getPasswordHash(), user.getRole(),
                           user.getIdEtudiant(), user.getIdEnseignant(), user.isActif());
    }
    
    @Override
    public boolean update(Utilisateur user) throws SQLException {
        String sql = "UPDATE UTILISATEUR SET login = ?, role = ?, actif = ? " +
                     "WHERE id_utilisateur = ?";
        return executeUpdate(sql, user.getLogin(), user.getRole(), 
                           user.isActif(), user.getIdUtilisateur()) > 0;
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM UTILISATEUR WHERE id_utilisateur = ?";
        return executeUpdate(sql, id) > 0;
    }
    
    public boolean updatePassword(int idUtilisateur, String newPasswordHash) throws SQLException {
        String sql = "UPDATE UTILISATEUR SET password_hash = ? WHERE id_utilisateur = ?";
        return executeUpdate(sql, newPasswordHash, idUtilisateur) > 0;
    }
    
    public boolean updateLastLogin(int idUtilisateur) throws SQLException {
        String sql = "UPDATE UTILISATEUR SET derniere_connexion = CURRENT_TIMESTAMP " +
                     "WHERE id_utilisateur = ?";
        return executeUpdate(sql, idUtilisateur) > 0;
    }
    
    public List<Utilisateur> findByRole(String role) throws SQLException {
        String sql = "SELECT * FROM UTILISATEUR WHERE role = ? ORDER BY login";
        return executeQuery(sql, this::mapUtilisateur, role);
    }
    
    private Utilisateur mapUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur user = new Utilisateur();
        user.setIdUtilisateur(rs.getInt("id_utilisateur"));
        user.setLogin(rs.getString("login"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setIdEtudiant((Integer) rs.getObject("id_etudiant"));
        user.setIdEnseignant((Integer) rs.getObject("id_enseignant"));
        user.setActif(rs.getBoolean("actif"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            user.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        Timestamp derniereConnexion = rs.getTimestamp("derniere_connexion");
        if (derniereConnexion != null) {
            user.setDerniereConnexion(derniereConnexion.toLocalDateTime());
        }
        
        return user;
    }
}

