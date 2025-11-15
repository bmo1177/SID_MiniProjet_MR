/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class AnneeScolaire {
    private int idAnnee;
    private String libelle;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;
    
    public AnneeScolaire() {}
    
    public AnneeScolaire(String libelle, LocalDate dateDebut, LocalDate dateFin) {
        this.libelle = libelle;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
    
    // Getters & Setters
    public int getIdAnnee() { return idAnnee; }
    public void setIdAnnee(int idAnnee) { this.idAnnee = idAnnee; }
    
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isEnCours() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(dateDebut) && !today.isAfter(dateFin);
    }
    
    @Override
    public String toString() {
        return libelle + (active ? " (active)" : "");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnneeScolaire that = (AnneeScolaire) o;
        return idAnnee == that.idAnnee;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idAnnee);
    }
}
