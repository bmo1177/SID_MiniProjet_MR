/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.*;
import java.sql.*;
import java.util.List;

public class InscriptionDAO extends BaseDAO<Inscription> {
    
    @Override
    public List<Inscription> findAll() throws SQLException {
        String sql = "SELECT i.*, CONCAT(e.nom, ' ', e.prenom) AS etudiant_name, " +
                     "p.nom AS programme_name, a.libelle AS annee_scolaire " +
                     "FROM INSCRIPTION i " +
                     "JOIN ETUDIANT e ON i.id_etudiant = e.id_etudiant " +
                     "JOIN PROGRAMME p ON i.id_programme = p.id_programme " +
                     "JOIN ANNEE_SCOLAIRE a ON i.id_annee = a.id_annee " +
                     "ORDER BY a.libelle DESC, e.nom";
        return executeQuery(sql, this::mapInscription);
    }
    
    @Override
    public Inscription findById(int id) throws SQLException {
        String sql = "SELECT * FROM INSCRIPTION WHERE id_inscription = ?";
        List<Inscription> results = executeQuery(sql, this::mapInscription, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public int insert(Inscription inscription) throws SQLException {
        String sql = "INSERT INTO INSCRIPTION (id_etudiant, id_programme, id_annee) " +
                     "VALUES (?, ?, ?)";
        return executeUpdate(sql, inscription.getIdEtudiant(),
                           inscription.getIdProgramme(), inscription.getIdAnnee());
    }
    
    @Override
    public boolean update(Inscription inscription) throws SQLException {
        String sql = "UPDATE INSCRIPTION SET id_programme = ? WHERE id_inscription = ?";
        return executeUpdate(sql, inscription.getIdProgramme(),
                           inscription.getIdInscription()) > 0;
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM INSCRIPTION WHERE id_inscription = ?";
        return executeUpdate(sql, id) > 0;
    }
    
    public boolean updateMoyenneGenerale(int idEtudiant, int idProgramme, 
                                        int idAnnee, double moyenne) throws SQLException {
        String sql = "UPDATE INSCRIPTION SET moyenne_generale = ? " +
                     "WHERE id_etudiant = ? AND id_programme = ? AND id_annee = ?";
        return executeUpdate(sql, moyenne, idEtudiant, idProgramme, idAnnee) > 0;
    }
    
    public boolean updateStatut(int idEtudiant, int idProgramme, 
                               int idAnnee, String statut) throws SQLException {
        String sql = "UPDATE INSCRIPTION SET statut_fin_annee = ? " +
                     "WHERE id_etudiant = ? AND id_programme = ? AND id_annee = ?";
        return executeUpdate(sql, statut, idEtudiant, idProgramme, idAnnee) > 0;
    }
    
    private Inscription mapInscription(ResultSet rs) throws SQLException {
        Inscription i = new Inscription();
        i.setIdInscription(rs.getInt("id_inscription"));
        i.setIdEtudiant(rs.getInt("id_etudiant"));
        i.setIdProgramme(rs.getInt("id_programme"));
        i.setIdAnnee(rs.getInt("id_annee"));
        i.setMoyenneGenerale((Double) rs.getObject("moyenne_generale"));
        i.setStatutFinAnnee(rs.getString("statut_fin_annee"));
        
        Date dateInscription = rs.getDate("date_inscription");
        if (dateInscription != null) {
            i.setDateInscription(dateInscription.toLocalDate());
        }
        
        try { i.setEtudiantName(rs.getString("etudiant_name")); } catch (SQLException ignored) {}
        try { i.setProgrammeName(rs.getString("programme_name")); } catch (SQLException ignored) {}
        try { i.setAnneeScolaire(rs.getString("annee_scolaire")); } catch (SQLException ignored) {}
        
        return i;
    }
}