/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;


import dao.*;
import models.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Service de gestion des notes
 * Contient toute la logique métier liée aux notes et aux calculs
 */

public class NoteService {
    private NoteEpreuveDAO noteEpreuveDAO;
    private NoteMatiereDAO noteMatiereDAO;
    private EpreuveDAO epreuveDAO;
    private InscriptionDAO inscriptionDAO;
    private ProgrammeDAO programmeDAO;
    
    public NoteService() {
        this.noteEpreuveDAO = new NoteEpreuveDAO();
        this.noteMatiereDAO = new NoteMatiereDAO();
        this.epreuveDAO = new EpreuveDAO();
        this.inscriptionDAO = new InscriptionDAO();
        this.programmeDAO = new ProgrammeDAO();
    }
    
    /**
     * Récupère toutes les notes d'un étudiant pour une année
     */
    public List<NoteEpreuve> getNotesEtudiant(int idEtudiant, int idAnnee) throws SQLException {
        String sql = "SELECT ne.*, m.nom AS matiere_name, ep.type_epreuve, " +
                     "ep.coefficient, ep.date_epreuve, " +
                     "CONCAT(ens.nom, ' ', ens.prenom) AS enseignant_name " +
                     "FROM NOTE_EPREUVE ne " +
                     "JOIN EPREUVE ep ON ne.id_epreuve = ep.id_epreuve " +
                     "JOIN MATIERE m ON ep.id_matiere = m.id_matiere " +
                     "JOIN ENSEIGNANT ens ON ep.id_enseignant = ens.id_enseignant " +
                     "WHERE ne.id_etudiant = ? AND ep.id_annee = ? " +
                     "ORDER BY m.nom, ep.date_epreuve";
        
        return noteEpreuveDAO.executeQuery(sql, rs -> {
            NoteEpreuve note = new NoteEpreuve();
            note.setIdNoteEpreuve(rs.getInt("id_note_epreuve"));
            note.setIdEtudiant(rs.getInt("id_etudiant"));
            note.setIdEpreuve(rs.getInt("id_epreuve"));
            note.setNote(rs.getDouble("note"));
            note.setDateSaisie(rs.getTimestamp("date_saisie").toLocalDateTime());
            note.setMatiereName(rs.getString("matiere_name"));
            note.setTypeEpreuve(rs.getString("type_epreuve"));
            note.setCoefficient(rs.getDouble("coefficient"));
            
            java.sql.Date dateEpreuve = rs.getDate("date_epreuve");
            if (dateEpreuve != null) {
                note.setDateEpreuve(dateEpreuve.toLocalDate());
            }
            
            note.setEnseignantName(rs.getString("enseignant_name"));
            return note;
        }, idEtudiant, idAnnee);
    }
    
    /**
     * Calcule la note finale d'une matière pour un étudiant
     * Formule: Moyenne pondérée des épreuves
     */
    public double calculerNoteMatiere(int idEtudiant, int idMatiere, int idAnnee) 
            throws SQLException {
        
        List<NoteEpreuve> notes = noteEpreuveDAO.findByEtudiantMatiereAnnee(
            idEtudiant, idMatiere, idAnnee);
        
        if (notes.isEmpty()) {
            return 0.0;
        }
        
        double sommeNotesPonderees = 0.0;
        double sommeCoefficients = 0.0;
        
        for (NoteEpreuve note : notes) {
            sommeNotesPonderees += note.getNote() * note.getCoefficient();
            sommeCoefficients += note.getCoefficient();
        }
        
        return sommeCoefficients > 0 ? 
            Math.round((sommeNotesPonderees / sommeCoefficients) * 100.0) / 100.0 : 0.0;
    }
    
    /**
     * Calcule et enregistre toutes les notes de matières pour un étudiant
     */
    public void calculerToutesLesNotesMatieres(int idEtudiant, int idProgramme, int idAnnee) 
            throws SQLException {
        
        // Récupérer toutes les matières du programme
        List<Matiere> matieres = programmeDAO.getMatieresByProgramme(idProgramme);
        
        for (Matiere matiere : matieres) {
            double noteFinale = calculerNoteMatiere(
                idEtudiant, matiere.getIdMatiere(), idAnnee);
            
            // Vérifier si la note existe déjà
            NoteMatiere nm = noteMatiereDAO.findByEtudiantMatiereAnnee(
                idEtudiant, matiere.getIdMatiere(), idAnnee);
            
            if (nm == null) {
                // Créer nouvelle note
                nm = new NoteMatiere();
                nm.setIdEtudiant(idEtudiant);
                nm.setIdMatiere(matiere.getIdMatiere());
                nm.setIdAnnee(idAnnee);
                nm.setNoteFinale(noteFinale);
                nm.setValidee(false);
                noteMatiereDAO.insert(nm);
            } else {
                // Mettre à jour note existante
                nm.setNoteFinale(noteFinale);
                noteMatiereDAO.update(nm);
            }
        }
    }
    
    /**
     * Calcule la moyenne générale annuelle (pondérée par les coefficients des matières)
     */
    public double calculerMoyenneGenerale(int idEtudiant, int idProgramme, int idAnnee) 
            throws SQLException {
        
        String sql = "SELECT nm.note_finale, COALESCE(pm.coefficient, 1.0) AS coefficient " +
                     "FROM NOTE_MATIERE nm " +
                     "JOIN PROGRAMME_MATIERE pgm ON nm.id_matiere = pgm.id_matiere " +
                     "LEFT JOIN PONDERATION_MATIERE pm ON pgm.id_matiere = pm.id_matiere " +
                     "AND pm.id_programme = ? AND pm.id_annee = ? " +
                     "WHERE nm.id_etudiant = ? AND nm.id_annee = ? " +
                     "AND pgm.id_programme = ? AND nm.validee = TRUE";
        
        List<Double[]> notesCoefs = noteMatiereDAO.executeQuery(sql, rs -> 
            new Double[]{rs.getDouble("note_finale"), rs.getDouble("coefficient")},
            idProgramme, idAnnee, idEtudiant, idAnnee, idProgramme
        );
        
        if (notesCoefs.isEmpty()) {
            return 0.0;
        }
        
        double sommeNotesPonderees = 0.0;
        double sommeCoefficients = 0.0;
        
        for (Double[] noteCoef : notesCoefs) {
            double note = noteCoef[0];
            double coef = noteCoef[1];
            sommeNotesPonderees += note * coef;
            sommeCoefficients += coef;
        }
        
        double moyenneGenerale = sommeCoefficients > 0 ? 
            Math.round((sommeNotesPonderees / sommeCoefficients) * 100.0) / 100.0 : 0.0;
        
        // Mettre à jour la moyenne dans INSCRIPTION
        inscriptionDAO.updateMoyenneGenerale(idEtudiant, idProgramme, idAnnee, moyenneGenerale);
        
        return moyenneGenerale;
    }
    
    /**
     * Détermine le statut de fin d'année basé sur la moyenne
     */
    public String determinerStatut(int idEtudiant, int idProgramme, int idAnnee) 
            throws SQLException {
        
        double moyenne = getMoyenneGenerale(idEtudiant, idAnnee);
        
        String statut;
        if (moyenne >= 10.0) {
            statut = "admis";
        } else if (moyenne >= 8.0) {
            statut = "redoublant";
        } else {
            statut = "exclu";
        }
        
        // Mettre à jour le statut dans INSCRIPTION
        inscriptionDAO.updateStatut(idEtudiant, idProgramme, idAnnee, statut);
        
        return statut;
    }
    
    /**
     * Récupère la moyenne générale d'un étudiant
     */
    public double getMoyenneGenerale(int idEtudiant, int idAnnee) throws SQLException {
        String sql = "SELECT moyenne_generale FROM INSCRIPTION " +
                     "WHERE id_etudiant = ? AND id_annee = ?";
        
        List<Double> results = inscriptionDAO.executeQuery(sql, 
            rs -> rs.getDouble("moyenne_generale"), idEtudiant, idAnnee);
        
        return results.isEmpty() ? 0.0 : results.get(0);
    }
    
    /**
     * Récupère le statut de fin d'année d'un étudiant
     */
    public String getStatut(int idEtudiant, int idAnnee) throws SQLException {
        String sql = "SELECT statut_fin_annee FROM INSCRIPTION " +
                     "WHERE id_etudiant = ? AND id_annee = ?";
        
        List<String> results = inscriptionDAO.executeQuery(sql, 
            rs -> rs.getString("statut_fin_annee"), idEtudiant, idAnnee);
        
        return results.isEmpty() ? "En cours" : results.get(0);
    }
    
    /**
     * Compte le nombre de matières validées
     */
    public int countMatieresValidees(int idEtudiant, int idAnnee) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM NOTE_MATIERE " +
                     "WHERE id_etudiant = ? AND id_annee = ? AND validee = TRUE " +
                     "AND note_finale >= 10.0";
        
        List<Integer> results = noteMatiereDAO.executeQuery(sql, 
            rs -> rs.getInt("count"), idEtudiant, idAnnee);
        
        return results.isEmpty() ? 0 : results.get(0);
    }
    
    /**
     * Valide une note de matière
     */
    public boolean validerNoteMatiere(int idNoteMatiere) throws SQLException {
        NoteMatiere nm = noteMatiereDAO.findById(idNoteMatiere);
        if (nm == null) {
            return false;
        }
        
        nm.setValidee(true);
        nm.setDateValidation(java.time.LocalDate.now());
        return noteMatiereDAO.update(nm);
    }
    
    /**
     * Récupère les notes de matières d'un étudiant
     */
    public List<NoteMatiere> getNotesMatieresEtudiant(int idEtudiant, int idAnnee) 
            throws SQLException {
        String sql = "SELECT nm.*, m.nom AS matiere_name " +
                     "FROM NOTE_MATIERE nm " +
                     "JOIN MATIERE m ON nm.id_matiere = m.id_matiere " +
                     "WHERE nm.id_etudiant = ? AND nm.id_annee = ? " +
                     "ORDER BY m.nom";
        
        return noteMatiereDAO.executeQuery(sql, rs -> {
            NoteMatiere nm = new NoteMatiere();
            nm.setIdNoteMatiere(rs.getInt("id_note_matiere"));
            nm.setIdEtudiant(rs.getInt("id_etudiant"));
            nm.setIdMatiere(rs.getInt("id_matiere"));
            nm.setIdAnnee(rs.getInt("id_annee"));
            nm.setNoteFinale(rs.getDouble("note_finale"));
            nm.setValidee(rs.getBoolean("validee"));
            
            java.sql.Date dateValidation = rs.getDate("date_validation");
            if (dateValidation != null) {
                nm.setDateValidation(dateValidation.toLocalDate());
            }
            
            nm.setMatiereName(rs.getString("matiere_name"));
            return nm;
        }, idEtudiant, idAnnee);
    }
    
    /**
     * Saisir ou modifier une note d'épreuve
     */
    public boolean saisirNote(int idEtudiant, int idEpreuve, double note, 
                             int idEnseignant, String commentaire) throws SQLException {
        
        // Validation
        if (note < 0 || note > 20) {
            throw new IllegalArgumentException("La note doit être entre 0 et 20");
        }
        
        // Vérifier si la note existe déjà
        String checkSql = "SELECT id_note_epreuve FROM NOTE_EPREUVE " +
                         "WHERE id_etudiant = ? AND id_epreuve = ?";
        
        List<Integer> existing = noteEpreuveDAO.executeQuery(checkSql, 
            rs -> rs.getInt("id_note_epreuve"), idEtudiant, idEpreuve);
        
        if (existing.isEmpty()) {
            // Insertion
            NoteEpreuve ne = new NoteEpreuve();
            ne.setIdEtudiant(idEtudiant);
            ne.setIdEpreuve(idEpreuve);
            ne.setNote(note);
            ne.setModifiePar(idEnseignant);
            ne.setCommentaire(commentaire);
            return noteEpreuveDAO.insert(ne) > 0;
        } else {
            // Mise à jour
            NoteEpreuve ne = noteEpreuveDAO.findById(existing.get(0));
            ne.setNote(note);
            ne.setModifiePar(idEnseignant);
            ne.setCommentaire(commentaire);
            return noteEpreuveDAO.update(ne);
        }
    }
    
    /**
     * Statistiques globales pour une matière
     */
    public StatistiqueMatiere getStatistiquesMatiere(int idMatiere, int idAnnee) 
            throws SQLException {
        
        String sql = "SELECT " +
                     "COUNT(*) AS nb_etudiants, " +
                     "AVG(nm.note_finale) AS moyenne, " +
                     "MIN(nm.note_finale) AS note_min, " +
                     "MAX(nm.note_finale) AS note_max, " +
                     "SUM(CASE WHEN nm.note_finale >= 10 THEN 1 ELSE 0 END) AS nb_admis " +
                     "FROM NOTE_MATIERE nm " +
                     "WHERE nm.id_matiere = ? AND nm.id_annee = ?";
        
        List<StatistiqueMatiere> stats = noteMatiereDAO.executeQuery(sql, rs -> {
            StatistiqueMatiere stat = new StatistiqueMatiere();
            stat.setNbEtudiants(rs.getInt("nb_etudiants"));
            stat.setMoyenne(rs.getDouble("moyenne"));
            stat.setNoteMin(rs.getDouble("note_min"));
            stat.setNoteMax(rs.getDouble("note_max"));
            stat.setNbAdmis(rs.getInt("nb_admis"));
            return stat;
        }, idMatiere, idAnnee);
        
        return stats.isEmpty() ? new StatistiqueMatiere() : stats.get(0);
    }
    
    /**
     * Classe interne pour les statistiques
     */
    public static class StatistiqueMatiere {
        private int nbEtudiants;
        private double moyenne;
        private double noteMin;
        private double noteMax;
        private int nbAdmis;
        
        public int getNbEtudiants() { return nbEtudiants; }
        public void setNbEtudiants(int nbEtudiants) { this.nbEtudiants = nbEtudiants; }
        
        public double getMoyenne() { return moyenne; }
        public void setMoyenne(double moyenne) { this.moyenne = moyenne; }
        
        public double getNoteMin() { return noteMin; }
        public void setNoteMin(double noteMin) { this.noteMin = noteMin; }
        
        public double getNoteMax() { return noteMax; }
        public void setNoteMax(double noteMax) { this.noteMax = noteMax; }
        
        public int getNbAdmis() { return nbAdmis; }
        public void setNbAdmis(int nbAdmis) { this.nbAdmis = nbAdmis; }
        
        public double getTauxReussite() {
            return nbEtudiants > 0 ? 
                Math.round((nbAdmis * 100.0 / nbEtudiants) * 100.0) / 100.0 : 0.0;
        }
    }
    
    /**
     * Get note for a specific epreuve and student
     */
    public NoteEpreuve getNoteEpreuve(int idEtudiant, int idEpreuve) throws SQLException {
        String sql = "SELECT * FROM NOTE_EPREUVE WHERE id_etudiant = ? AND id_epreuve = ?";
        List<NoteEpreuve> notes = noteEpreuveDAO.executeQuery(sql, rs -> {
            NoteEpreuve note = new NoteEpreuve();
            note.setIdNoteEpreuve(rs.getInt("id_note_epreuve"));
            note.setIdEtudiant(rs.getInt("id_etudiant")); 
            note.setIdEpreuve(rs.getInt("id_epreuve"));
            note.setNote(rs.getDouble("note"));
            return note;
        }, idEtudiant, idEpreuve);
        return notes.isEmpty() ? null : notes.get(0);
    }
}