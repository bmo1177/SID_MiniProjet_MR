/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class NoteEpreuve {
    private int idNoteEpreuve;
    private int idEtudiant;
    private int idEpreuve;
    private double note;
    private LocalDateTime dateSaisie;
    private Integer modifiePar;
    private String commentaire;
    
    // Champs pour jointures
    private String etudiantName;
    private String matiereName;
    private String typeEpreuve;
    private double coefficient;
    private LocalDate dateEpreuve;
    private String enseignantName;
    
    public NoteEpreuve() {}
    
    // Getters & Setters
    public int getIdNoteEpreuve() { return idNoteEpreuve; }
    public void setIdNoteEpreuve(int idNoteEpreuve) { this.idNoteEpreuve = idNoteEpreuve; }
    
    public int getIdEtudiant() { return idEtudiant; }
    public void setIdEtudiant(int idEtudiant) { this.idEtudiant = idEtudiant; }
    
    public int getIdEpreuve() { return idEpreuve; }
    public void setIdEpreuve(int idEpreuve) { this.idEpreuve = idEpreuve; }
    
    public double getNote() { return note; }
    public void setNote(double note) { this.note = note; }
    
    public LocalDateTime getDateSaisie() { return dateSaisie; }
    public void setDateSaisie(LocalDateTime dateSaisie) { this.dateSaisie = dateSaisie; }
    
    public Integer getModifiePar() { return modifiePar; }
    public void setModifiePar(Integer modifiePar) { this.modifiePar = modifiePar; }
    
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    
    // Jointures
    public String getEtudiantName() { return etudiantName; }
    public void setEtudiantName(String etudiantName) { this.etudiantName = etudiantName; }
    
    public String getMatiereName() { return matiereName; }
    public void setMatiereName(String matiereName) { this.matiereName = matiereName; }
    
    public String getTypeEpreuve() { return typeEpreuve; }
    public void setTypeEpreuve(String typeEpreuve) { this.typeEpreuve = typeEpreuve; }
    
    public double getCoefficient() { return coefficient; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }
    
    public LocalDate getDateEpreuve() { return dateEpreuve; }
    public void setDateEpreuve(LocalDate dateEpreuve) { this.dateEpreuve = dateEpreuve; }
    
    public String getEnseignantName() { return enseignantName; }
    public void setEnseignantName(String enseignantName) { this.enseignantName = enseignantName; }
    
    public String getNoteSur20() {
        return String.format("%.2f/20", note);
    }
}