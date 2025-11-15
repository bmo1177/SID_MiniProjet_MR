/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.*;
import java.sql.*;
import java.util.List;

public class ProgrammeDAO extends BaseDAO<Programme> {
    
    @Override
    public List<Programme> findAll() throws SQLException {
        String sql = "SELECT * FROM PROGRAMME ORDER BY annee_etude, nom";
        return executeQuery(sql, this::mapProgramme);
    }
    
    @Override
    public Programme findById(int id) throws SQLException {
        String sql = "SELECT * FROM PROGRAMME WHERE id_programme = ?";
        List<Programme> results = executeQuery(sql, this::mapProgramme, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public int insert(Programme programme) throws SQLException {
        String sql = "INSERT INTO PROGRAMME (code, nom, annee_etude, description) " +
                     "VALUES (?, ?, ?, ?)";
        return executeUpdate(sql, programme.getCode(), programme.getNom(),
                           programme.getAnneeEtude(), programme.getDescription());
    }
    
    @Override
    public boolean update(Programme programme) throws SQLException {
        String sql = "UPDATE PROGRAMME SET code = ?, nom = ?, annee_etude = ?, " +
                     "description = ? WHERE id_programme = ?";
        return executeUpdate(sql, programme.getCode(), programme.getNom(),
                           programme.getAnneeEtude(), programme.getDescription(),
                           programme.getIdProgramme()) > 0;
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM PROGRAMME WHERE id_programme = ?";
        return executeUpdate(sql, id) > 0;
    }
    
    public List<Programme> findByAnneeEtude(int anneeEtude) throws SQLException {
        String sql = "SELECT * FROM PROGRAMME WHERE annee_etude = ? ORDER BY nom";
        return executeQuery(sql, this::mapProgramme, anneeEtude);
    }
    
    public Programme findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM PROGRAMME WHERE code = ?";
        List<Programme> results = executeQuery(sql, this::mapProgramme, code);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public List<Matiere> getMatieresByProgramme(int idProgramme) throws SQLException {
        String sql = "SELECT m.* FROM MATIERE m " +
                     "JOIN PROGRAMME_MATIERE pm ON m.id_matiere = pm.id_matiere " +
                     "WHERE pm.id_programme = ? ORDER BY m.nom";
        
        MatiereDAO matiereDAO = new MatiereDAO();
        return executeQuery(sql, rs -> {
            Matiere m = new Matiere();
            m.setIdMatiere(rs.getInt("id_matiere"));
            m.setNom(rs.getString("nom"));
            m.setObjectif(rs.getString("objectif"));
            m.setSemestre((Integer) rs.getObject("semestre"));
            return m;
        }, idProgramme);
    }
    
    private Programme mapProgramme(ResultSet rs) throws SQLException {
        Programme p = new Programme();
        p.setIdProgramme(rs.getInt("id_programme"));
        p.setCode(rs.getString("code"));
        p.setNom(rs.getString("nom"));
        p.setAnneeEtude(rs.getInt("annee_etude"));
        p.setDescription(rs.getString("description"));
        return p;
    }
}
