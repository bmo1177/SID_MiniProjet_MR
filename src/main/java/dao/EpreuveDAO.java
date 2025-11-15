/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.*;
import java.sql.*;
import java.util.List;

public class EpreuveDAO extends BaseDAO<Epreuve> {
    
    @Override
    public List<Epreuve> findAll() throws SQLException {
        String sql = "SELECT ep.*, m.nom AS matiere_name, " +
                     "CONCAT(ens.nom, ' ', ens.prenom) AS enseignant_name, " +
                     "a.libelle AS annee_scolaire " +
                     "FROM EPREUVE ep " +
                     "JOIN MATIERE m ON ep.id_matiere = m.id_matiere " +
                     "JOIN ENSEIGNANT ens ON ep.id_enseignant = ens.id_enseignant " +
                     "JOIN ANNEE_SCOLAIRE a ON ep.id_annee = a.id_annee " +
                     "ORDER BY ep.date_epreuve DESC";
        return executeQuery(sql, this::mapEpreuve);
    }
    
    @Override
    public Epreuve findById(int id) throws SQLException {
        String sql = "SELECT ep.*, m.nom AS matiere_name, " +
                     "CONCAT(ens.nom, ' ', ens.prenom) AS enseignant_name " +
                     "FROM EPREUVE ep " +
                     "JOIN MATIERE m ON ep.id_matiere = m.id_matiere " +
                     "JOIN ENSEIGNANT ens ON ep.id_enseignant = ens.id_enseignant " +
                     "WHERE ep.id_epreuve = ?";
        List<Epreuve> results = executeQuery(sql, this::mapEpreuve, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public int insert(Epreuve epreuve) throws SQLException {
        String sql = "INSERT INTO EPREUVE (type_epreuve, intitule, date_epreuve, " +
                     "coefficient, id_matiere, id_enseignant, id_annee) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql, epreuve.getTypeEpreuve(), epreuve.getIntitule(),
                           epreuve.getDateEpreuve(), epreuve.getCoefficient(),
                           epreuve.getIdMatiere(), epreuve.getIdEnseignant(),
                           epreuve.getIdAnnee());
    }
    
    @Override
    public boolean update(Epreuve epreuve) throws SQLException {
        String sql = "UPDATE EPREUVE SET type_epreuve = ?, intitule = ?, " +
                     "date_epreuve = ?, coefficient = ? WHERE id_epreuve = ?";
        return executeUpdate(sql, epreuve.getTypeEpreuve(), epreuve.getIntitule(),
                           epreuve.getDateEpreuve(), epreuve.getCoefficient(),
                           epreuve.getIdEpreuve()) > 0;
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM EPREUVE WHERE id_epreuve = ?";
        return executeUpdate(sql, id) > 0;
    }
    
    public List<Epreuve> findByEnseignant(int idEnseignant) throws SQLException {
        String sql = "SELECT ep.*, m.nom AS matiere_name " +
                     "FROM EPREUVE ep " +
                     "JOIN MATIERE m ON ep.id_matiere = m.id_matiere " +
                     "WHERE ep.id_enseignant = ? " +
                     "ORDER BY ep.date_epreuve DESC";
        return executeQuery(sql, this::mapEpreuve, idEnseignant);
    }
    
    public List<Epreuve> findByMatiereAndAnnee(int idMatiere, int idAnnee) throws SQLException {
        String sql = "SELECT ep.*, CONCAT(ens.nom, ' ', ens.prenom) AS enseignant_name " +
                     "FROM EPREUVE ep " +
                     "JOIN ENSEIGNANT ens ON ep.id_enseignant = ens.id_enseignant " +
                     "WHERE ep.id_matiere = ? AND ep.id_annee = ? " +
                     "ORDER BY ep.date_epreuve";
        return executeQuery(sql, this::mapEpreuve, idMatiere, idAnnee);
    }
    
    public int countByEnseignant(int idEnseignant) throws SQLException {
        String sql = "SELECT COUNT(*) FROM EPREUVE WHERE id_enseignant = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, idEnseignant);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }
    
    public int countMatieresEnseignees(int idEnseignant) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT id_matiere) FROM EPREUVE " +
                     "WHERE id_enseignant = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, idEnseignant);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }
    
    private Epreuve mapEpreuve(ResultSet rs) throws SQLException {
        Epreuve ep = new Epreuve();
        ep.setIdEpreuve(rs.getInt("id_epreuve"));
        ep.setTypeEpreuve(rs.getString("type_epreuve"));
        ep.setIntitule(rs.getString("intitule"));
        
        Date dateEpreuve = rs.getDate("date_epreuve");
        if (dateEpreuve != null) {
            ep.setDateEpreuve(dateEpreuve.toLocalDate());
        }
        
        ep.setCoefficient(rs.getDouble("coefficient"));
        ep.setIdMatiere(rs.getInt("id_matiere"));
        ep.setIdEnseignant(rs.getInt("id_enseignant"));
        ep.setIdAnnee(rs.getInt("id_annee"));
        
        // Champs de jointure
        try {
            ep.setMatiereName(rs.getString("matiere_name"));
        } catch (SQLException ignored) {}
        
        try {
            ep.setEnseignantName(rs.getString("enseignant_name"));
        } catch (SQLException ignored) {}
        
        try {
            ep.setAnneeScolaire(rs.getString("annee_scolaire"));
        } catch (SQLException ignored) {}
        
        return ep;
    }
}