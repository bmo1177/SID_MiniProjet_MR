/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Utilisateur {
    private int idUtilisateur;
    private String login;
    private String passwordHash;
    private String role;
    private Integer idEtudiant;
    private Integer idEnseignant;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;
    
    public Utilisateur() {}
    
    // Getters & Setters
    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Integer getIdEtudiant() { return idEtudiant; }
    public void setIdEtudiant(Integer idEtudiant) { this.idEtudiant = idEtudiant; }
    
    public Integer getIdEnseignant() { return idEnseignant; }
    public void setIdEnseignant(Integer idEnseignant) { this.idEnseignant = idEnseignant; }
    
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getDerniereConnexion() { return derniereConnexion; }
    public void setDerniereConnexion(LocalDateTime derniereConnexion) { 
        this.derniereConnexion = derniereConnexion; 
    }
    
    public String getRoleLabel() {
        if (role == null) return "Inconnu";
        switch (role.toLowerCase()) {
            case "etudiant": return "Étudiant";
            case "enseignant": return "Enseignant";
            case "scolarite": return "Service Scolarité";
            case "direction": return "Direction";
            case "admin": return "Administrateur";
            default: return role;
        }
    }
    
    public String getRoleIcon() {
        if (role == null) return "👤";
        switch (role.toLowerCase()) {
            case "etudiant": return "👨‍🎓";
            case "enseignant": return "👨‍🏫";
            case "scolarite": return "📋";
            case "direction": return "👔";
            case "admin": return "⚙️";
            default: return "👤";
        }
    }
}