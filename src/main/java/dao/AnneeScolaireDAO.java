/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
        
import models.AnneeScolaire;
import java.sql.*;
import java.util.List;

public class AnneeScolaireDAO extends BaseDAO<AnneeScolaire> {
    
    @Override
    public List<AnneeScolaire> findAll() throws SQLException {
        String sql = "SELECT * FROM ANNEE_SCOLAIRE ORDER BY date_debut DESC";
        return executeQuery(sql, this::mapAnneeScolaire);
    }
    
    @Override
    public AnneeScolaire findById(int id) throws SQLException {
        String sql = "SELECT * FROM ANNEE_SCOLAIRE WHERE id_annee = ?";
        List<AnneeScolaire> results = executeQuery(sql, this::mapAnneeScolaire, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public AnneeScolaire findActive() throws SQLException {
        String sql = "SELECT * FROM ANNEE_SCOLAIRE WHERE active = TRUE LIMIT 1";
        List<AnneeScolaire> results = executeQuery(sql, this::mapAnneeScolaire);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public int insert(AnneeScolaire annee) throws SQLException {
        String sql = "INSERT INTO ANNEE_SCOLAIRE (libelle, date_debut, date_fin, active) " +
                     "VALUES (?, ?, ?, ?)";
        return executeUpdate(sql, annee.getLibelle(), annee.getDateDebut(),
                           annee.getDateFin(), annee.isActive());
    }
    
    @Override
    public boolean update(AnneeScolaire annee) throws SQLException {
        String sql = "UPDATE ANNEE_SCOLAIRE SET libelle = ?, date_debut = ?, " +
                     "date_fin = ?, active = ? WHERE id_annee = ?";
        return executeUpdate(sql, annee.getLibelle(), annee.getDateDebut(),
                           annee.getDateFin(), annee.isActive(), 
                           annee.getIdAnnee()) > 0;
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM ANNEE_SCOLAIRE WHERE id_annee = ?";
        return executeUpdate(sql, id) > 0;
    }
    
    private AnneeScolaire mapAnneeScolaire(ResultSet rs) throws SQLException {
        AnneeScolaire a = new AnneeScolaire();
        a.setIdAnnee(rs.getInt("id_annee"));
        a.setLibelle(rs.getString("libelle"));
        a.setDateDebut(rs.getDate("date_debut").toLocalDate());
        a.setDateFin(rs.getDate("date_fin").toLocalDate());
        a.setActive(rs.getBoolean("active"));
        return a;
    }
}