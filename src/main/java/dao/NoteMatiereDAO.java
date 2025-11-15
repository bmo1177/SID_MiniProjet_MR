/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.NoteMatiere;
import java.sql.*;
import java.util.List;

public class NoteMatiereDAO extends BaseDAO<NoteMatiere> {
    
    @Override
    public List<NoteMatiere> findAll() throws SQLException {
        String sql = "SELECT * FROM NOTE_MATIERE ORDER BY id_etudiant";
        return executeQuery(sql, this::mapNoteMatiere);
    }
    
    @Override
    public NoteMatiere findById(int id) throws SQLException {
        String sql = "SELECT * FROM NOTE_MATIERE WHERE id_note_matiere = ?";
        List<NoteMatiere> results = executeQuery(sql, this::mapNoteMatiere, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public NoteMatiere findByEtudiantMatiereAnnee(int idEtudiant, int idMatiere, 
                                                   int idAnnee) throws SQLException {
        String sql = "SELECT * FROM NOTE_MATIERE " +
                     "WHERE id_etudiant = ? AND id_matiere = ? AND id_annee = ?";
        List<NoteMatiere> results = executeQuery(sql, this::mapNoteMatiere, 
                                                idEtudiant, idMatiere, idAnnee);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public int insert(NoteMatiere note) throws SQLException {
        String sql = "INSERT INTO NOTE_MATIERE (id_etudiant, id_matiere, id_annee, " +
                     "note_finale, validee) VALUES (?, ?, ?, ?, ?)";
        return executeUpdate(sql, note.getIdEtudiant(), note.getIdMatiere(),
                           note.getIdAnnee(), note.getNoteFinale(), note.isValidee());
    }
    
    @Override
    public boolean update(NoteMatiere note) throws SQLException {
        String sql = "UPDATE NOTE_MATIERE SET note_finale = ?, validee = ?, " +
                     "date_validation = ? WHERE id_note_matiere = ?";
        return executeUpdate(sql, note.getNoteFinale(), note.isValidee(),
                           note.getDateValidation(), note.getIdNoteMatiere()) > 0;
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM NOTE_MATIERE WHERE id_note_matiere = ?";
        return executeUpdate(sql, id) > 0;
    }
    
    private NoteMatiere mapNoteMatiere(ResultSet rs) throws SQLException {
        NoteMatiere nm = new NoteMatiere();
        nm.setIdNoteMatiere(rs.getInt("id_note_matiere"));
        nm.setIdEtudiant(rs.getInt("id_etudiant"));
        nm.setIdMatiere(rs.getInt("id_matiere"));
        nm.setIdAnnee(rs.getInt("id_annee"));
        nm.setNoteFinale(rs.getDouble("note_finale"));
        nm.setValidee(rs.getBoolean("validee"));
        
        Date dateValidation = rs.getDate("date_validation");
        if (dateValidation != null) {
            nm.setDateValidation(dateValidation.toLocalDate());
        }
        
        return nm;
    }
}