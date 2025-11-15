/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Modèle représentant un étudiant
 */
public class Etudiant {
    private int idEtudiant;
    private String nom;
    private String prenom;
    private String origineScolaire; // DUT, CPI, CPGE
    private LocalDate dateNaissance;
    private String email;
    private String telephone;
    private String adresse;
    private LocalDate dateInscription;
    
    // Constructeurs
    public Etudiant() {
        this.dateInscription = LocalDate.now();
    }
    
    public Etudiant(String nom, String prenom, String origineScolaire) {
        this();
        this.nom = nom;
        this.prenom = prenom;
        this.origineScolaire = origineScolaire;
    }
    
    // Getters et Setters
    public int getIdEtudiant() {
        return idEtudiant;
    }
    
    public void setIdEtudiant(int idEtudiant) {
        this.idEtudiant = idEtudiant;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getOrigineScolaire() {
        return origineScolaire;
    }
    
    public void setOrigineScolaire(String origineScolaire) {
        this.origineScolaire = origineScolaire;
    }
    
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }
    
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public LocalDate getDateInscription() {
        return dateInscription;
    }
    
    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }
    
    // Méthodes utilitaires
    public String getNomComplet() {
        return nom + " " + prenom;
    }
    
    public int getAge() {
        if (dateNaissance == null) return 0;
        return LocalDate.now().getYear() - dateNaissance.getYear();
    }
    
    @Override
    public String toString() {
        return String.format("%s %s (%s)", nom, prenom, origineScolaire);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Etudiant etudiant = (Etudiant) o;
        return idEtudiant == etudiant.idEtudiant;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idEtudiant);
    }
}