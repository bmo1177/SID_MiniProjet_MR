/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Epreuve {
    private int idEpreuve;
    private String typeEpreuve;
    private String intitule;
    private LocalDate dateEpreuve;
    private double coefficient;
    private int idMatiere;
    private int idEnseignant;
    private int idAnnee;
    
    // Champs pour les jointures
    private String matiereName;
    private String enseignantName;
    private String anneeScolaire;
    
    public Epreuve() {}
    
    public Epreuve(String typeEpreuve, String intitule, double coefficient) {
        this.typeEpreuve = typeEpreuve;
        this.intitule = intitule;
        this.coefficient = coefficient;
    }
    
    // Getters & Setters complets
    public int getIdEpreuve() { return idEpreuve; }
    public void setIdEpreuve(int idEpreuve) { this.idEpreuve = idEpreuve; }
    
    public String getTypeEpreuve() { return typeEpreuve; }
    public void setTypeEpreuve(String typeEpreuve) { this.typeEpreuve = typeEpreuve; }
    
    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }
    
    public LocalDate getDateEpreuve() { return dateEpreuve; }
    public void setDateEpreuve(LocalDate dateEpreuve) { this.dateEpreuve = dateEpreuve; }
    
    public double getCoefficient() { return coefficient; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }
    
    public int getIdMatiere() { return idMatiere; }
    public void setIdMatiere(int idMatiere) { this.idMatiere = idMatiere; }
    
    public int getIdEnseignant() { return idEnseignant; }
    public void setIdEnseignant(int idEnseignant) { this.idEnseignant = idEnseignant; }
    
    public int getIdAnnee() { return idAnnee; }
    public void setIdAnnee(int idAnnee) { this.idAnnee = idAnnee; }
    
    public String getMatiereName() { return matiereName; }
    public void setMatiereName(String matiereName) { this.matiereName = matiereName; }
    
    public String getEnseignantName() { return enseignantName; }
    public void setEnseignantName(String enseignantName) { this.enseignantName = enseignantName; }
    
    public String getAnneeScolaire() { return anneeScolaire; }
    public void setAnneeScolaire(String anneeScolaire) { this.anneeScolaire = anneeScolaire; }
    
    // Additional fields for compatibility
    private String description;
    private boolean active = true;
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    @Override
    public String toString() {
        return typeEpreuve + " - " + intitule;
    }
}
