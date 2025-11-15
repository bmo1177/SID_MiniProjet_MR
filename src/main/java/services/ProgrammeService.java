/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.*;
import models.*;
import java.sql.SQLException;
import java.util.List;

public class ProgrammeService {
    private ProgrammeDAO programmeDAO;
    
    public ProgrammeService() {
        this.programmeDAO = new ProgrammeDAO();
    }
    
    /**
     * Récupère tous les programmes
     */
    public List<Programme> getTousProgrammes() throws SQLException {
        return programmeDAO.findAll();
    }
    
    /**
     * Récupère les programmes par année d'étude
     */
    public List<Programme> getProgrammesParAnnee(int anneeEtude) throws SQLException {
        return programmeDAO.findByAnneeEtude(anneeEtude);
    }
    
    /**
     * Crée un nouveau programme
     */
    public int creerProgramme(Programme programme) throws SQLException {
        // Vérifier que le code n'existe pas déjà
        Programme existing = programmeDAO.findByCode(programme.getCode());
        if (existing != null) {
            throw new IllegalArgumentException("Un programme avec ce code existe déjà");
        }
        return programmeDAO.insert(programme);
    }
    
    /**
     * Modifie un programme existant
     */
    public boolean modifierProgramme(Programme programme) throws SQLException {
        return programmeDAO.update(programme);
    }
    
    /**
     * Supprime un programme
     */
    public boolean supprimerProgramme(int idProgramme) throws SQLException {
        return programmeDAO.delete(idProgramme);
    }
    
    /**
     * Récupère les matières d'un programme
     */
    public List<Matiere> getMatieresParProgramme(int idProgramme) throws SQLException {
        return programmeDAO.getMatieresByProgramme(idProgramme);
    }
    
    /**
     * Associe une matière à un programme
     */
    public boolean associerMatiere(int idProgramme, int idMatiere) throws SQLException {
        String sql = "INSERT INTO PROGRAMME_MATIERE (id_programme, id_matiere) VALUES (?, ?)";
        return programmeDAO.executeUpdate(sql, idProgramme, idMatiere) > 0;
    }
    
    /**
     * Dissocie une matière d'un programme
     */
    public boolean dissocierMatiere(int idProgramme, int idMatiere) throws SQLException {
        String sql = "DELETE FROM PROGRAMME_MATIERE WHERE id_programme = ? AND id_matiere = ?";
        return programmeDAO.executeUpdate(sql, idProgramme, idMatiere) > 0;
    }
    
    /**
     * Définit un prérequis entre programmes
     */
    public boolean definirPrerequis(int idProgrammeRequis, int idProgrammeCible) 
            throws SQLException {
        String sql = "INSERT INTO PREREQUIS (id_programme_requis, id_programme_cible) " +
                     "VALUES (?, ?)";
        return programmeDAO.executeUpdate(sql, idProgrammeRequis, idProgrammeCible) > 0;
    }
    
    /**
     * Définit la pondération d'une matière
     */
    public boolean definirPonderation(int idProgramme, int idMatiere, int idAnnee, 
                                     double coefficient) throws SQLException {
        String sql = "INSERT INTO PONDERATION_MATIERE " +
                     "(id_programme, id_matiere, id_annee, coefficient) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE coefficient = ?";
        return programmeDAO.executeUpdate(sql, idProgramme, idMatiere, idAnnee, 
                                         coefficient, coefficient) > 0;
    }
}

