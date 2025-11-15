/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Enseignant {
    private int idEnseignant;
    private String nom;
    private String prenom;
    private String grade;
    private String email;
    private String telephone;
    private String specialite;
    
    public Enseignant() {}
    
    public Enseignant(String nom, String prenom, String grade) {
        this.nom = nom;
        this.prenom = prenom;
        this.grade = grade;
    }
    
    // Getters & Setters
    public int getIdEnseignant() { return idEnseignant; }
    public void setIdEnseignant(int idEnseignant) { this.idEnseignant = idEnseignant; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    
    public String getNomComplet() {
        return nom + " " + prenom;
    }
    
    public String getNomAvecGrade() {
        return grade + " " + nom + " " + prenom;
    }
    
    @Override
    public String toString() {
        return getNomComplet();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enseignant that = (Enseignant) o;
        return idEnseignant == that.idEnseignant;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idEnseignant);
    }
}
