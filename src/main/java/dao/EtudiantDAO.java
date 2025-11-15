/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.Etudiant;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des étudiants
 */
public class EtudiantDAO extends BaseDAO<Etudiant> {
    
    @Override
    public List<Etudiant> findAll() throws SQLException {
        String sql = "SELECT * FROM ETUDIANT ORDER BY nom, prenom";
        return executeQuery(sql, this::mapEtudiant);
    }
    
    @Override
    public Etudiant findById(int id) throws SQLException {
        String sql = "SELECT * FROM ETUDIANT WHERE id_etudiant = ?";
        List<Etudiant> results = executeQuery(sql, this::mapEtudiant, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public int insert(Etudiant etudiant) throws SQLException {
        String sql = "INSERT INTO ETUDIANT (nom, prenom, origine_scolaire, date_naissance, " +
                     "email, telephone, adresse, date_inscription) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        return executeUpdate(sql,
            etudiant.getNom(),
            etudiant.getPrenom(),
            etudiant.getOrigineScolaire(),
            etudiant.getDateNaissance(),
            etudiant.getEmail(),
            etudiant.getTelephone(),
            etudiant.getAdresse(),
            etudiant.getDateInscription()
        );
    }
    
    @Override
    public boolean update(Etudiant etudiant) throws SQLException {
        String sql = "UPDATE ETUDIANT SET nom = ?, prenom = ?, origine_scolaire = ?, " +
                     "date_naissance = ?, email = ?, telephone = ?, adresse = ? " +
                     "WHERE id_etudiant = ?";
        
        int rowsAffected = executeUpdate(sql,
            etudiant.getNom(),
            etudiant.getPrenom(),
            etudiant.getOrigineScolaire(),
            etudiant.getDateNaissance(),
            etudiant.getEmail(),
            etudiant.getTelephone(),
            etudiant.getAdresse(),
            etudiant.getIdEtudiant()
        );
        
        return rowsAffected > 0;
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM ETUDIANT WHERE id_etudiant = ?";
        return executeUpdate(sql, id) > 0;
    }
    
    /**
     * Recherche des étudiants par nom ou prénom
     */
    public List<Etudiant> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM ETUDIANT WHERE nom LIKE ? OR prenom LIKE ? " +
                     "ORDER BY nom, prenom";
        String pattern = "%" + keyword + "%";
        return executeQuery(sql, this::mapEtudiant, pattern, pattern);
    }
    
    /**
     * Récupère les étudiants par origine scolaire
     */
    public List<Etudiant> findByOrigine(String origine) throws SQLException {
        String sql = "SELECT * FROM ETUDIANT WHERE origine_scolaire = ? ORDER BY nom, prenom";
        return executeQuery(sql, this::mapEtudiant, origine);
    }
    
    /**
     * Récupère les étudiants inscrits dans un programme pour une année donnée
     */
    public List<Etudiant> findByProgrammeAndAnnee(int idProgramme, int idAnnee) throws SQLException {
        String sql = "SELECT e.* FROM ETUDIANT e " +
                     "JOIN INSCRIPTION i ON e.id_etudiant = i.id_etudiant " +
                     "WHERE i.id_programme = ? AND i.id_annee = ? " +
                     "ORDER BY e.nom, e.prenom";
        return executeQuery(sql, this::mapEtudiant, idProgramme, idAnnee);
    }
    
    /**
     * Vérifie si un email existe déjà
     */
    public boolean emailExists(String email, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ETUDIANT WHERE email = ? AND id_etudiant != ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setInt(2, excludeId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } finally {
            closeResources(rs, stmt);
        }
        
        return false;
    }
    
    /**
     * Compte le nombre total d'étudiants
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ETUDIANT";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } finally {
            closeResources(rs, stmt);
        }
        
        return 0;
    }
    
    /**
     * Map un ResultSet vers un objet Etudiant
     */
    private Etudiant mapEtudiant(ResultSet rs) throws SQLException {
        Etudiant etudiant = new Etudiant();
        etudiant.setIdEtudiant(rs.getInt("id_etudiant"));
        etudiant.setNom(rs.getString("nom"));
        etudiant.setPrenom(rs.getString("prenom"));
        etudiant.setOrigineScolaire(rs.getString("origine_scolaire"));
        
        Date dateNaissance = rs.getDate("date_naissance");
        if (dateNaissance != null) {
            etudiant.setDateNaissance(dateNaissance.toLocalDate());
        }
        
        etudiant.setEmail(rs.getString("email"));
        etudiant.setTelephone(rs.getString("telephone"));
        etudiant.setAdresse(rs.getString("adresse"));
        
        Date dateInscription = rs.getDate("date_inscription");
        if (dateInscription != null) {
            etudiant.setDateInscription(dateInscription.toLocalDate());
        }
        
        return etudiant;
    }
}