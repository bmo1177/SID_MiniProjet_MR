package views.admin;

import config.ThemeManager;
import models.*;
import dao.*;
import services.*;
import views.components.*;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

/**
 * Data loading and management methods for AdminDashboard
 * This is a continuation of the AdminDashboard class methods
 */
public class AdminDashboard_DataMethods {
    
    /**
     * Methods to add to AdminDashboard class:
     */
    
    /*
    // Load initial data when dashboard opens
    private void loadInitialData() {
        SwingUtilities.invokeLater(() -> {
            loadStatisticsData();
            loadUsersData();
        });
    }
    
    // Load real statistics from database
    private void loadStatisticsData() {
        try {
            // Total users count
            List<Utilisateur> allUsers = utilisateurDAO.findAll();
            totalUsersCard.updateValue(String.valueOf(allUsers.size()));
            
            // Active students count
            List<Etudiant> activeStudents = etudiantDAO.findAll();
            int activeCount = activeStudents.size();
            activeStudentsCard.updateValue(String.valueOf(activeCount));
            
            // Calculate trend (mock calculation)
            String studentTrend = activeCount > 50 ? "+12%" : "-3%";
            activeStudentsCard.updateTrend(studentTrend);
            
            // Total teachers
            List<Enseignant> teachers = enseignantDAO.findAll();
            totalTeachersCard.updateValue(String.valueOf(teachers.size()));
            
            // System health (based on data integrity)
            double healthScore = calculateSystemHealth();
            systemHealthCard.updateValue(String.format("%.1f%%", healthScore));
            if (healthScore > 90) {
                systemHealthCard.updateTrend("Excellent");
            } else if (healthScore > 70) {
                systemHealthCard.updateTrend("Bon");
            } else {
                systemHealthCard.updateTrend("Attention");
            }
            
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors du chargement des statistiques", e);
        }
    }
    
    // Load users data into table
    private void loadUsersData() {
        try {
            List<Utilisateur> users = utilisateurDAO.findAll();
            List<Object[]> tableData = new ArrayList<>();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (Utilisateur user : users) {
                Object[] row = new Object[7];
                row[0] = user.getIdUtilisateur();
                row[1] = user.getLogin();
                row[2] = user.getRole();
                
                // Get full name based on role
                String fullName = getFullNameForUser(user);
                row[3] = fullName;
                
                // Get email
                String email = getEmailForUser(user);
                row[4] = email;
                
                // Status
                row[5] = user.isActif() ? "Actif" : "Inactif";
                
                // Last connection
                if (user.getDerniereConnexion() != null) {
                    row[6] = dateFormat.format(user.getDerniereConnexion());
                } else {
                    row[6] = "Jamais";
                }
                
                tableData.add(row);
            }
            
            usersTable.updateData(tableData);
            
        } catch (SQLException e) {
            handleDatabaseError("Erreur lors du chargement des utilisateurs", e);
        }
    }
    
    // Get full name for user based on their role
    private String getFullNameForUser(Utilisateur user) {
        try {
            switch (user.getRole()) {
                case "ETUDIANT":
                    if (user.getIdEtudiant() != null) {
                        Etudiant etudiant = etudiantDAO.findById(user.getIdEtudiant());
                        if (etudiant != null) {
                            return etudiant.getNom() + " " + etudiant.getPrenom();
                        }
                    }
                    break;
                case "ENSEIGNANT":
                    if (user.getIdEnseignant() != null) {
                        Enseignant enseignant = enseignantDAO.findById(user.getIdEnseignant());
                        if (enseignant != null) {
                            return enseignant.getNom() + " " + enseignant.getPrenom();
                        }
                    }
                    break;
                default:
                    return user.getLogin(); // For admin, scolarite, direction
            }
        } catch (SQLException e) {
            System.err.println("Error getting full name for user " + user.getLogin() + ": " + e.getMessage());
        }
        return user.getLogin();
    }
    
    // Get email for user based on their role
    private String getEmailForUser(Utilisateur user) {
        try {
            switch (user.getRole()) {
                case "ETUDIANT":
                    if (user.getIdEtudiant() != null) {
                        Etudiant etudiant = etudiantDAO.findById(user.getIdEtudiant());
                        if (etudiant != null) {
                            return etudiant.getEmail();
                        }
                    }
                    break;
                case "ENSEIGNANT":
                    if (user.getIdEnseignant() != null) {
                        Enseignant enseignant = enseignantDAO.findById(user.getIdEnseignant());
                        if (enseignant != null) {
                            return enseignant.getEmail();
                        }
                    }
                    break;
                default:
                    return user.getLogin() + "@univ-tiaret.dz"; // Default email pattern
            }
        } catch (SQLException e) {
            System.err.println("Error getting email for user " + user.getLogin() + ": " + e.getMessage());
        }
        return "";
    }
    
    // Calculate system health score
    private double calculateSystemHealth() {
        double score = 100.0;
        
        try {
            // Check for data integrity issues
            List<Utilisateur> users = utilisateurDAO.findAll();
            List<Etudiant> students = etudiantDAO.findAll();
            List<Enseignant> teachers = enseignantDAO.findAll();
            
            // Check for orphaned users (users without associated student/teacher)
            int orphanedUsers = 0;
            for (Utilisateur user : users) {
                if ("ETUDIANT".equals(user.getRole()) && user.getIdEtudiant() == null) {
                    orphanedUsers++;
                } else if ("ENSEIGNANT".equals(user.getRole()) && user.getIdEnseignant() == null) {
                    orphanedUsers++;
                }
            }
            
            // Deduct points for issues
            if (orphanedUsers > 0) {
                score -= orphanedUsers * 5; // 5 points per orphaned user
            }
            
            // Check for inactive users
            long inactiveUsers = users.stream()
                .filter(u -> !u.isActif())
                .count();
            
            if (inactiveUsers > users.size() * 0.1) { // More than 10% inactive
                score -= 10;
            }
            
            // Ensure score doesn't go below 0
            score = Math.max(0, score);
            
        } catch (SQLException e) {
            score = 0; // Database connectivity issues
        }
        
        return score;
    }
    
    // Filter users by search term
    private void filterUsers(String searchTerm) {
        if (usersTable != null) {
            usersTable.filterData(searchTerm);
        }
    }
    
    // Filter users by role
    private void filterUsersByRole(String role) {
        // Implementation for role filtering
        if ("Tous les rôles".equals(role)) {
            loadUsersData(); // Reload all data
        } else {
            try {
                List<Utilisateur> users = utilisateurDAO.findAll();
                List<Object[]> filteredData = new ArrayList<>();
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                
                for (Utilisateur user : users) {
                    if (role.equals(user.getRole())) {
                        Object[] row = new Object[7];
                        row[0] = user.getIdUtilisateur();
                        row[1] = user.getLogin();
                        row[2] = user.getRole();
                        row[3] = getFullNameForUser(user);
                        row[4] = getEmailForUser(user);
                        row[5] = user.isActif() ? "Actif" : "Inactif";
                        row[6] = user.getDerniereConnexion() != null ? 
                               dateFormat.format(user.getDerniereConnexion()) : "Jamais";
                        filteredData.add(row);
                    }
                }
                
                usersTable.updateData(filteredData);
                
            } catch (SQLException e) {
                handleDatabaseError("Erreur lors du filtrage des utilisateurs", e);
            }
        }
    }
    
    // Create detailed stat card
    private JPanel createDetailedStatCard(String title, String description, Color color) {
        JPanel card = ThemeManager.createCard();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 150));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.FONT_HEADING_SMALL);
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(ThemeManager.FONT_BODY_SMALL);
        descLabel.setForeground(ThemeManager.GRAY_600);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add some mock data - in real implementation, this would come from database
        JLabel valueLabel = new JLabel("Loading...");
        valueLabel.setFont(ThemeManager.FONT_HEADING_LARGE);
        valueLabel.setForeground(ThemeManager.GRAY_900);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel content = new JPanel(new GridLayout(3, 1, 0, ThemeManager.SPACING_SM));
        content.setOpaque(false);
        content.add(titleLabel);
        content.add(valueLabel);
        content.add(descLabel);
        
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    // Action methods
    private void refreshData() {
        loadStatisticsData();
        loadUsersData();
        showNotification("Données actualisées", "success");
    }
    
    private void exportData() {
        // Implementation for data export
        String csvData = usersTable.exportToCsv();
        // Save to file or show save dialog
        showNotification("Export réalisé", "success");
    }
    
    private void openSettings() {
        // Implementation for settings dialog
        showNotification("Paramètres à venir", "info");
    }
    
    private void openAddUserDialog() {
        // Implementation for add user dialog
        showNotification("Fonctionnalité à venir", "info");
    }
    
    private void editSelectedUser() {
        Object[] selectedData = usersTable.getSelectedRowData();
        if (selectedData != null) {
            showNotification("Modification de: " + selectedData[1], "info");
        } else {
            showNotification("Veuillez sélectionner un utilisateur", "warning");
        }
    }
    
    private void deleteSelectedUser() {
        Object[] selectedData = usersTable.getSelectedRowData();
        if (selectedData != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer l'utilisateur " + selectedData[1] + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int userId = (Integer) selectedData[0];
                    utilisateurDAO.delete(userId);
                    loadUsersData(); // Reload data
                    showNotification("Utilisateur supprimé", "success");
                } catch (SQLException e) {
                    handleDatabaseError("Erreur lors de la suppression", e);
                }
            }
        } else {
            showNotification("Veuillez sélectionner un utilisateur", "warning");
        }
    }
    
    private void resetUserPassword() {
        Object[] selectedData = usersTable.getSelectedRowData();
        if (selectedData != null) {
            showNotification("Reset mot de passe pour: " + selectedData[1], "info");
        } else {
            showNotification("Veuillez sélectionner un utilisateur", "warning");
        }
    }
    
    // Utility methods
    private void showNotification(String message, String type) {
        // Implementation for showing notifications
        JOptionPane.showMessageDialog(this, message);
    }
    
    private void handleDatabaseError(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        showNotification(message + ": " + e.getMessage(), "error");
    }
    */
}