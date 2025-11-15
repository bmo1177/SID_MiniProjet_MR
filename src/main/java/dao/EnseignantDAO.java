/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.Enseignant;
import java.sql.*;
import java.util.List;

public class EnseignantDAO extends BaseDAO<Enseignant> {
    
    @Override
    public List<Enseignant> findAll() throws SQLException {
        String sql = "SELECT * FROM ENSEIGNANT ORDER BY nom, prenom";
        return executeQuery(sql, this::mapEnseignant);
    }
    
    @Override
    public Enseignant findById(int id) throws SQLException {
        String sql = "SELECT * FROM ENSEIGNANT WHERE id_enseignant = ?";
        List<Enseignant> results = executeQuery(sql, this::mapEnseignant, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public int insert(Enseignant ens) throws SQLException {
        String sql = "INSERT INTO ENSEIGNANT (nom, prenom, grade, email, " +
                     "telephone, specialite) VALUES (?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql, ens.getNom(), ens.getPrenom(), ens.getGrade(),
                           ens.getEmail(), ens.getTelephone(), ens.getSpecialite());
    }
    
    @Override
    public boolean update(Enseignant ens) throws SQLException {
        String sql = "UPDATE ENSEIGNANT SET nom = ?, prenom = ?, grade = ?, " +
                     "email = ?, telephone = ?, specialite = ? WHERE id_enseignant = ?";
        return executeUpdate(sql, ens.getNom(), ens.getPrenom(), ens.getGrade(),
                           ens.getEmail(), ens.getTelephone(), ens.getSpecialite(),
                           ens.getIdEnseignant()) > 0;
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM ENSEIGNANT WHERE id_enseignant = ?";
        return executeUpdate(sql, id) > 0;
    }
    
    private Enseignant mapEnseignant(ResultSet rs) throws SQLException {
        Enseignant e = new Enseignant();
        e.setIdEnseignant(rs.getInt("id_enseignant"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        e.setGrade(rs.getString("grade"));
        e.setEmail(rs.getString("email"));
        e.setTelephone(rs.getString("telephone"));
        e.setSpecialite(rs.getString("specialite"));
        return e;
    }
}