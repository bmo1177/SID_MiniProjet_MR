/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Matiere {
    private int idMatiere;
    private String nom;
    private String objectif;
    private Integer semestre; // 1 ou 2
    
    public Matiere() {}
    
    public Matiere(String nom, String objectif, Integer semestre) {
        this.nom = nom;
        this.objectif = objectif;
        this.semestre = semestre;
    }
    
    // Getters & Setters
    public int getIdMatiere() { return idMatiere; }
    public void setIdMatiere(int idMatiere) { this.idMatiere = idMatiere; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }
    
    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }
    
    public String getSemestreLabel() {
        if (semestre == null) return "Non défini";
        return "Semestre " + semestre;
    }
    
    @Override
    public String toString() {
        return nom;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matiere matiere = (Matiere) o;
        return idMatiere == matiere.idMatiere;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idMatiere);
    }
}
