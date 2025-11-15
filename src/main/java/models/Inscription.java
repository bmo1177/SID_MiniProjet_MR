/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Inscription {
    private int idInscription;
    private int idEtudiant;
    private int idProgramme;
    private int idAnnee;
    private Double moyenneGenerale;
    private String statutFinAnnee;
    private LocalDate dateInscription;
    
    // Champs pour jointures
    private String etudiantName;
    private String programmeName;
    private String anneeScolaire;
    
    public Inscription() {}
    
    // Getters & Setters
    public int getIdInscription() { return idInscription; }
    public void setIdInscription(int idInscription) { this.idInscription = idInscription; }
    
    public int getIdEtudiant() { return idEtudiant; }
    public void setIdEtudiant(int idEtudiant) { this.idEtudiant = idEtudiant; }
    
    public int getIdProgramme() { return idProgramme; }
    public void setIdProgramme(int idProgramme) { this.idProgramme = idProgramme; }
    
    public int getIdAnnee() { return idAnnee; }
    public void setIdAnnee(int idAnnee) { this.idAnnee = idAnnee; }
    
    public Double getMoyenneGenerale() { return moyenneGenerale; }
    public void setMoyenneGenerale(Double moyenneGenerale) { this.moyenneGenerale = moyenneGenerale; }
    
    public String getStatutFinAnnee() { return statutFinAnnee; }
    public void setStatutFinAnnee(String statutFinAnnee) { this.statutFinAnnee = statutFinAnnee; }
    
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    
    // Jointures
    public String getEtudiantName() { return etudiantName; }
    public void setEtudiantName(String etudiantName) { this.etudiantName = etudiantName; }
    
    public String getProgrammeName() { return programmeName; }
    public void setProgrammeName(String programmeName) { this.programmeName = programmeName; }
    
    public String getAnneeScolaire() { return anneeScolaire; }
    public void setAnneeScolaire(String anneeScolaire) { this.anneeScolaire = anneeScolaire; }
    
    public String getStatutAvecIcone() {
        if (statutFinAnnee == null) return "⚪ En cours";
        switch (statutFinAnnee.toLowerCase()) {
            case "admis": return "🟢 Admis";
            case "redoublant": return "🟠 Redoublant";
            case "exclu": return "🔴 Exclu";
            default: return "⚪ " + statutFinAnnee;
        }
    }
    
    public String getMoyenneFormatee() {
        return moyenneGenerale != null ? String.format("%.2f/20", moyenneGenerale) : "N/A";
    }
}
