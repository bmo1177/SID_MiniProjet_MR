/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.*;
import models.*;
import java.sql.SQLException;
import java.util.List;

public class BulletinService {
    private NoteService noteService;
    private EtudiantDAO etudiantDAO;
    private ProgrammeDAO programmeDAO;
    private AnneeScolaireDAO anneeScolaireDAO;
    
    public BulletinService() {
        this.noteService = new NoteService();
        this.etudiantDAO = new EtudiantDAO();
        this.programmeDAO = new ProgrammeDAO();
        this.anneeScolaireDAO = new AnneeScolaireDAO();
    }
    
    /**
     * Génère les données du bulletin pour un étudiant
     */
    public BulletinData genererBulletin(int idEtudiant, int idProgramme, int idAnnee) 
            throws SQLException {
        
        BulletinData bulletin = new BulletinData();
        
        // Informations étudiant
        Etudiant etudiant = etudiantDAO.findById(idEtudiant);
        bulletin.setEtudiant(etudiant);
        
        // Informations programme et année
        Programme programme = programmeDAO.findById(idProgramme);
        bulletin.setProgramme(programme);
        
        AnneeScolaire annee = anneeScolaireDAO.findById(idAnnee);
        bulletin.setAnneeScolaire(annee);
        
        // Notes des matières
        List<NoteMatiere> notes = noteService.getNotesMatieresEtudiant(idEtudiant, idAnnee);
        bulletin.setNotesMatiere(notes);
        
        // Moyenne générale et statut
        double moyenne = noteService.getMoyenneGenerale(idEtudiant, idAnnee);
        bulletin.setMoyenneGenerale(moyenne);
        
        String statut = noteService.getStatut(idEtudiant, idAnnee);
        bulletin.setStatut(statut);
        
        return bulletin;
    }
    
    /**
     * Classe interne pour les données du bulletin
     */
    public static class BulletinData {
        private Etudiant etudiant;
        private Programme programme;
        private AnneeScolaire anneeScolaire;
        private List<NoteMatiere> notesMatiere;
        private double moyenneGenerale;
        private String statut;
        
        // Getters & Setters
        public Etudiant getEtudiant() { return etudiant; }
        public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }
        
        public Programme getProgramme() { return programme; }
        public void setProgramme(Programme programme) { this.programme = programme; }
        
        public AnneeScolaire getAnneeScolaire() { return anneeScolaire; }
        public void setAnneeScolaire(AnneeScolaire anneeScolaire) { 
            this.anneeScolaire = anneeScolaire; 
        }
        
        public List<NoteMatiere> getNotesMatiere() { return notesMatiere; }
        public void setNotesMatiere(List<NoteMatiere> notesMatiere) { 
            this.notesMatiere = notesMatiere; 
        }
        
        public double getMoyenneGenerale() { return moyenneGenerale; }
        public void setMoyenneGenerale(double moyenneGenerale) { 
            this.moyenneGenerale = moyenneGenerale; 
        }
        
        public String getStatut() { return statut; }
        public void setStatut(String statut) { this.statut = statut; }
    }
}

