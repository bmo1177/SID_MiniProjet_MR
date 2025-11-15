/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Programme {
    private int idProgramme;
    private String code;
    private String nom;
    private int anneeEtude;
    private String description;
    
    public Programme() {}
    
    public Programme(String code, String nom, int anneeEtude) {
        this.code = code;
        this.nom = nom;
        this.anneeEtude = anneeEtude;
    }
    
    // Getters & Setters
    public int getIdProgramme() { return idProgramme; }
    public void setIdProgramme(int idProgramme) { this.idProgramme = idProgramme; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public int getAnneeEtude() { return anneeEtude; }
    public void setAnneeEtude(int anneeEtude) { this.anneeEtude = anneeEtude; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    // Additional fields for compatibility
    private String niveau;
    private String specialite;
    private Integer dureeEnSemestres;
    private boolean actif = true;
    
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    
    public Integer getDureeEnSemestres() { return dureeEnSemestres; }
    public void setDureeEnSemestres(Integer dureeEnSemestres) { this.dureeEnSemestres = dureeEnSemestres; }
    
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    
    @Override
    public String toString() {
        return code + " - " + nom;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Programme programme = (Programme) o;
        return idProgramme == programme.idProgramme;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idProgramme);
    }
}
