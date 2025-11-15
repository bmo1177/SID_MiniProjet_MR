/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class NoteMatiere {
    private int idNoteMatiere;
    private int idEtudiant;
    private int idMatiere;
    private int idAnnee;
    private double noteFinale;
    private boolean validee;
    private LocalDate dateValidation;
    
    // Champs pour jointures
    private String etudiantName;
    private String matiereName;
    private String anneeScolaire;
    private Double coefficient;
    
    public NoteMatiere() {}
    
    // Getters & Setters
    public int getIdNoteMatiere() { return idNoteMatiere; }
    public void setIdNoteMatiere(int idNoteMatiere) { this.idNoteMatiere = idNoteMatiere; }
    
    public int getIdEtudiant() { return idEtudiant; }
    public void setIdEtudiant(int idEtudiant) { this.idEtudiant = idEtudiant; }
    
    public int getIdMatiere() { return idMatiere; }
    public void setIdMatiere(int idMatiere) { this.idMatiere = idMatiere; }
    
    public int getIdAnnee() { return idAnnee; }
    public void setIdAnnee(int idAnnee) { this.idAnnee = idAnnee; }
    
    public double getNoteFinale() { return noteFinale; }
    public void setNoteFinale(double noteFinale) { this.noteFinale = noteFinale; }
    
    public boolean isValidee() { return validee; }
    public void setValidee(boolean validee) { this.validee = validee; }
    
    public LocalDate getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDate dateValidation) { this.dateValidation = dateValidation; }
    
    // Jointures
    public String getEtudiantName() { return etudiantName; }
    public void setEtudiantName(String etudiantName) { this.etudiantName = etudiantName; }
    
    public String getMatiereName() { return matiereName; }
    public void setMatiereName(String matiereName) { this.matiereName = matiereName; }
    
    public String getAnneeScolaire() { return anneeScolaire; }
    public void setAnneeScolaire(String anneeScolaire) { this.anneeScolaire = anneeScolaire; }
    
    public Double getCoefficient() { return coefficient; }
    public void setCoefficient(Double coefficient) { this.coefficient = coefficient; }
    
    public String getNoteSur20() {
        return String.format("%.2f/20", noteFinale);
    }
    
    public String getStatutValidation() {
        return validee ? "✓ Validée" : "⏳ En attente";
    }
}