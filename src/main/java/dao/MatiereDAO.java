/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.*;
import java.sql.*;
import java.util.List;

public class MatiereDAO extends BaseDAO<Matiere> {
    
    @Override
    public List<Matiere> findAll() throws SQLException {
        String sql = "SELECT * FROM MATIERE ORDER BY nom";
        return executeQuery(sql, this::mapMatiere);
    }
    
    @Override
    public Matiere findById(int id) throws SQLException {
        String sql = "SELECT * FROM MATIERE WHERE id_matiere = ?";
        List<Matiere> results = executeQuery(sql, this::mapMatiere, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public int insert(Matiere matiere) throws SQLException {
        String sql = "INSERT INTO MATIERE (nom, objectif, semestre) VALUES (?, ?, ?)";
        return executeUpdate(sql, matiere.getNom(), matiere.getObjectif(), 
                           matiere.getSemestre());
    }
    
    @Override
    public boolean update(Matiere matiere) throws SQLException {
        String sql = "UPDATE MATIERE SET nom = ?, objectif = ?, semestre = ? " +
                     "WHERE id_matiere = ?";
        return executeUpdate(sql, matiere.getNom(), matiere.getObjectif(),
                           matiere.getSemestre(), matiere.getIdMatiere()) > 0;
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM MATIERE WHERE id_matiere = ?";
        return executeUpdate(sql, id) > 0;
    }
    
    public List<Matiere> findBySemestre(int semestre) throws SQLException {
        String sql = "SELECT * FROM MATIERE WHERE semestre = ? ORDER BY nom";
        return executeQuery(sql, this::mapMatiere, semestre);
    }
    
    private Matiere mapMatiere(ResultSet rs) throws SQLException {
        Matiere m = new Matiere();
        m.setIdMatiere(rs.getInt("id_matiere"));
        m.setNom(rs.getString("nom"));
        m.setObjectif(rs.getString("objectif"));
        m.setSemestre((Integer) rs.getObject("semestre"));
        return m;
    }
}