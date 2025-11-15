/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import models.*;
import java.sql.*;
import java.util.List;

public class NoteEpreuveDAO extends BaseDAO<NoteEpreuve> {
    
    @Override
    public List<NoteEpreuve> findAll() throws SQLException {
        String sql = "SELECT ne.*, CONCAT(e.nom, ' ', e.prenom) AS etudiant_name, " +
                     "m.nom AS matiere_name, ep.type_epreuve, ep.coefficient " +
                     "FROM NOTE_EPREUVE ne " +
                     "JOIN ETUDIANT e ON ne.id_etudiant = e.id_etudiant " +
                     "JOIN EPREUVE ep ON ne.id_epreuve = ep.id_epreuve " +
                     "JOIN MATIERE m ON ep.id_matiere = m.id_matiere " +
                     "ORDER BY ne.date_saisie DESC";
        return executeQuery(sql, this::mapNoteEpreuve);
    }
    
    @Override
    public NoteEpreuve findById(int id) throws SQLException {
        String sql = "SELECT * FROM NOTE_EPREUVE WHERE id_note_epreuve = ?";
        List<NoteEpreuve> results = executeQuery(sql, this::mapNoteEpreuve, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public List<NoteEpreuve> findByEtudiantMatiereAnnee(int idEtudiant, int idMatiere, 
                                                         int idAnnee) throws SQLException {
        String sql = "SELECT ne.*, ep.coefficient, ep.type_epreuve " +
                     "FROM NOTE_EPREUVE ne " +
                     "JOIN EPREUVE ep ON ne.id_epreuve = ep.id_epreuve " +
                     "WHERE ne.id_etudiant = ? AND ep.id_matiere = ? AND ep.id_annee = ?";
        return executeQuery(sql, this::mapNoteEpreuve, idEtudiant, idMatiere, idAnnee);
    }

    /**
     * Liste les étudiants concernés par une épreuve donnée, avec leurs notes existantes si présentes.
     * Les étudiants sont déterminés via leur inscription au(x) programme(s) liés à la matière de l'épreuve,
     * pour l'année scolaire de l'épreuve. Les étudiants sans note apparaissent avec note NULL.
     */
    public List<NoteEpreuve> findEtudiantsPourEpreuve(int idEpreuve) throws SQLException {
        String sql = "SELECT e.id_etudiant, CONCAT(e.nom, ' ', e.prenom) AS etudiant_name, " +
                     "ne.id_note_epreuve, ne.id_epreuve, ne.note, ne.commentaire, ne.date_saisie " +
                     "FROM ETUDIANT e " +
                     "JOIN INSCRIPTION i ON e.id_etudiant = i.id_etudiant " +
                     "JOIN EPREUVE ep ON ep.id_epreuve = ? " +
                     "JOIN PROGRAMME_MATIERE pgm ON pgm.id_matiere = ep.id_matiere " +
                     "AND pgm.id_programme = i.id_programme " +
                     "AND i.id_annee = ep.id_annee " +
                     "LEFT JOIN NOTE_EPREUVE ne ON ne.id_epreuve = ep.id_epreuve " +
                     "AND ne.id_etudiant = e.id_etudiant " +
                     "ORDER BY e.nom, e.prenom";

        return executeQuery(sql, rs -> {
            NoteEpreuve ne = new NoteEpreuve();
            int idEtudiant = rs.getInt("id_etudiant");
            ne.setIdEtudiant(idEtudiant);
            ne.setIdEpreuve(idEpreuve);

            // Champs existants si la note est saisie
            Object idNoteObj = rs.getObject("id_note_epreuve");
            if (idNoteObj != null) {
                ne.setIdNoteEpreuve(((Number) idNoteObj).intValue());
            }

            Object noteObj = rs.getObject("note");
            if (noteObj != null) {
                ne.setNote(((Number) noteObj).doubleValue());
            }

            ne.setCommentaire(rs.getString("commentaire"));
            Timestamp ts = rs.getTimestamp("date_saisie");
            if (ts != null) {
                ne.setDateSaisie(ts.toLocalDateTime());
            }

            // Jointure affichage
            ne.setEtudiantName(rs.getString("etudiant_name"));
            return ne;
        }, idEpreuve);
    }
    
    @Override
    public int insert(NoteEpreuve note) throws SQLException {
        String sql = "INSERT INTO NOTE_EPREUVE (id_etudiant, id_epreuve, note, " +
                     "modifie_par, commentaire) VALUES (?, ?, ?, ?, ?)";
        return executeUpdate(sql, note.getIdEtudiant(), note.getIdEpreuve(),
                           note.getNote(), note.getModifiePar(), note.getCommentaire());
    }
    
    @Override
    public boolean update(NoteEpreuve note) throws SQLException {
        String sql = "UPDATE NOTE_EPREUVE SET note = ?, commentaire = ?, " +
                     "modifie_par = ?, date_saisie = CURRENT_TIMESTAMP " +
                     "WHERE id_note_epreuve = ?";
        return executeUpdate(sql, note.getNote(), note.getCommentaire(),
                           note.getModifiePar(), note.getIdNoteEpreuve()) > 0;
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM NOTE_EPREUVE WHERE id_note_epreuve = ?";
        return executeUpdate(sql, id) > 0;
    }
    
    public int countNotesManquantes(int idEnseignant) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT e.id_etudiant) " +
                     "FROM ETUDIANT e " +
                     "JOIN INSCRIPTION i ON e.id_etudiant = i.id_etudiant " +
                     "CROSS JOIN EPREUVE ep " +
                     "LEFT JOIN NOTE_EPREUVE ne ON e.id_etudiant = ne.id_etudiant " +
                     "AND ep.id_epreuve = ne.id_epreuve " +
                     "WHERE ep.id_enseignant = ? AND ne.id_note_epreuve IS NULL";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, idEnseignant);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }
    
    private NoteEpreuve mapNoteEpreuve(ResultSet rs) throws SQLException {
        NoteEpreuve ne = new NoteEpreuve();
        ne.setIdNoteEpreuve(rs.getInt("id_note_epreuve"));
        ne.setIdEtudiant(rs.getInt("id_etudiant"));
        ne.setIdEpreuve(rs.getInt("id_epreuve"));
        ne.setNote(rs.getDouble("note"));
        
        Timestamp dateSaisie = rs.getTimestamp("date_saisie");
        if (dateSaisie != null) {
            ne.setDateSaisie(dateSaisie.toLocalDateTime());
        }
        
        ne.setModifiePar((Integer) rs.getObject("modifie_par"));
        ne.setCommentaire(rs.getString("commentaire"));
        
        // Jointures
        try { ne.setEtudiantName(rs.getString("etudiant_name")); } catch (SQLException ignored) {}
        try { ne.setMatiereName(rs.getString("matiere_name")); } catch (SQLException ignored) {}
        try { ne.setTypeEpreuve(rs.getString("type_epreuve")); } catch (SQLException ignored) {}
        try { ne.setCoefficient(rs.getDouble("coefficient")); } catch (SQLException ignored) {}
        
        return ne;
    }
}
