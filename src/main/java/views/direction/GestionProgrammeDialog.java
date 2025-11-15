/*
 * Dialog pour la gestion des programmes d'études
 * Utilisé par la direction pour créer/modifier/supprimer des programmes
 */
package views.direction;

import models.*;
import dao.*;
import views.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class GestionProgrammeDialog extends JDialog {
    private ProgrammeDAO programmeDAO;
    private MatiereDAO matiereDAO;
    private DefaultTableModel programmeModel;
    private DefaultTableModel matiereModel;
    private JTable tableProgrammes;
    private JTable tableMatieres;
    
    private JTextField txtNomProgramme;
    private JTextField txtDescription;
    private JComboBox<String> cmbNiveau;
    private JComboBox<String> cmbSpecialite;
    private JSpinner spinDuree;
    
    private Programme programmeSelectionne;

    public GestionProgrammeDialog(Frame parent) {
        super(parent, "Gestion des Programmes", true);
        this.programmeDAO = new ProgrammeDAO();
        this.matiereDAO = new MatiereDAO();
        
        initComponents();
        loadProgrammes();
        setupEvents();
        
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel principal avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📚 Programmes", createProgrammePanel());
        tabbedPane.addTab("📝 Matières", createMatierePanel());
        tabbedPane.addTab("⚖️ Pondérations", createPonderationPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createProgrammePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Formulaire de création/modification
        JPanel formPanel = createProgrammeForm();
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Table des programmes
        JPanel tablePanel = createProgrammeTable();
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProgrammeForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Informations du Programme"));
        
        // Première ligne
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.add(new JLabel("Nom du programme :"));
        txtNomProgramme = new JTextField(20);
        row1.add(txtNomProgramme);
        
        row1.add(new JLabel("Niveau :"));
        cmbNiveau = new JComboBox<>(new String[]{"L1", "L2", "L3", "M1", "M2", "Doctorat"});
        row1.add(cmbNiveau);
        
        panel.add(row1);
        
        // Deuxième ligne
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.add(new JLabel("Spécialité :"));
        cmbSpecialite = new JComboBox<>(new String[]{
            "Informatique Générale", "Génie Logiciel", "Réseaux et Télécoms", 
            "Intelligence Artificielle", "Sécurité Informatique", "Systèmes d'Information"
        });
        row2.add(cmbSpecialite);
        
        row2.add(new JLabel("Durée (semestres) :"));
        spinDuree = new JSpinner(new SpinnerNumberModel(6, 2, 12, 1));
        row2.add(spinDuree);
        
        panel.add(row2);
        
        // Description
        JPanel row3 = new JPanel(new BorderLayout(10, 5));
        row3.add(new JLabel("Description :"), BorderLayout.WEST);
        txtDescription = new JTextField();
        txtDescription.setPreferredSize(new Dimension(400, 25));
        row3.add(txtDescription, BorderLayout.CENTER);
        
        JButton btnAjouter = new JButton("➕ Ajouter");
        btnAjouter.setBackground(new Color(46, 204, 113));
        btnAjouter.setForeground(Color.WHITE);
        btnAjouter.setFocusPainted(false);
        btnAjouter.addActionListener(e -> ajouterProgramme());
        row3.add(btnAjouter, BorderLayout.EAST);
        
        panel.add(row3);
        
        return panel;
    }
    
    private JPanel createProgrammeTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Programmes Existants"));
        
        // Table
        String[] columns = {"ID", "Nom", "Niveau", "Spécialité", "Durée", "Description", "Actif"};
        programmeModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableProgrammes = new JTable(programmeModel);
        tableProgrammes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableProgrammes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableProgrammes.getSelectedRow();
                if (selectedRow >= 0) {
                    chargerProgrammeSelectionne(selectedRow);
                }
            }
        });
        
        // Masquer la colonne ID
        tableProgrammes.getColumnModel().getColumn(0).setMinWidth(0);
        tableProgrammes.getColumnModel().getColumn(0).setMaxWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(tableProgrammes);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Boutons d'action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnModifier = new JButton("✏️ Modifier");
        btnModifier.setBackground(new Color(230, 126, 34));
        btnModifier.setForeground(Color.WHITE);
        btnModifier.setFocusPainted(false);
        btnModifier.addActionListener(e -> modifierProgramme());
        
        JButton btnSupprimer = new JButton("🗑️ Supprimer");
        btnSupprimer.setBackground(new Color(231, 76, 60));
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.setFocusPainted(false);
        btnSupprimer.addActionListener(e -> supprimerProgramme());
        
        JButton btnGererMatieres = new JButton("📝 Gérer les Matières");
        btnGererMatieres.setBackground(new Color(52, 152, 219));
        btnGererMatieres.setForeground(Color.WHITE);
        btnGererMatieres.setFocusPainted(false);
        btnGererMatieres.addActionListener(e -> gererMatieres());
        
        actionPanel.add(btnGererMatieres);
        actionPanel.add(btnModifier);
        actionPanel.add(btnSupprimer);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createMatierePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblInfo = new JLabel("Sélectionnez un programme pour gérer ses matières", 
            SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblInfo.setForeground(new Color(127, 140, 141));
        panel.add(lblInfo, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPonderationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblInfo = new JLabel("Définir les coefficients des matières par programme", 
            SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblInfo.setForeground(new Color(127, 140, 141));
        panel.add(lblInfo, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton btnFermer = new JButton("Fermer");
        btnFermer.setPreferredSize(new Dimension(100, 35));
        btnFermer.addActionListener(e -> dispose());
        
        panel.add(btnFermer);
        
        return panel;
    }
    
    private void setupEvents() {
        // Événements déjà configurés dans les méthodes de création
    }
    
    private void loadProgrammes() {
        try {
            programmeModel.setRowCount(0);
            List<Programme> programmes = programmeDAO.findAll();
            
            for (Programme programme : programmes) {
                programmeModel.addRow(new Object[]{
                    programme.getIdProgramme(),
                    programme.getNom(),
                    programme.getNiveau(),
                    programme.getSpecialite(),
                    programme.getDureeEnSemestres(),
                    programme.getDescription(),
                    programme.isActif() ? "Oui" : "Non"
                });
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des programmes: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void ajouterProgramme() {
        try {
            // Validation
            if (txtNomProgramme.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Le nom du programme est obligatoire");
            }
            
            // Créer le programme
            Programme programme = new Programme();
            programme.setNom(txtNomProgramme.getText().trim());
            programme.setNiveau((String) cmbNiveau.getSelectedItem());
            programme.setSpecialite((String) cmbSpecialite.getSelectedItem());
            programme.setDureeEnSemestres((Integer) spinDuree.getValue());
            programme.setDescription(txtDescription.getText().trim());
            programme.setActif(true);
            
            // Sauvegarder
            programmeDAO.insert(programme);
            
            // Rafraîchir la liste
            loadProgrammes();
            clearForm();
            
            NotificationToast.show((JFrame) getParent(),
                "Programme créé avec succès", NotificationToast.Type.SUCCESS);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la création du programme: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void modifierProgramme() {
        int selectedRow = tableProgrammes.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un programme à modifier",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int idProgramme = (Integer) programmeModel.getValueAt(selectedRow, 0);
            Programme programme = programmeDAO.findById(idProgramme);
            
            if (programme != null) {
                // Remplir le formulaire
                txtNomProgramme.setText(programme.getNom());
                cmbNiveau.setSelectedItem(programme.getNiveau());
                cmbSpecialite.setSelectedItem(programme.getSpecialite());
                spinDuree.setValue(programme.getDureeEnSemestres());
                txtDescription.setText(programme.getDescription());
                
                // Mettre à jour le programme
                programme.setNom(txtNomProgramme.getText().trim());
                programme.setNiveau((String) cmbNiveau.getSelectedItem());
                programme.setSpecialite((String) cmbSpecialite.getSelectedItem());
                programme.setDureeEnSemestres((Integer) spinDuree.getValue());
                programme.setDescription(txtDescription.getText().trim());
                
                programmeDAO.update(programme);
                loadProgrammes();
                clearForm();
                
                NotificationToast.show((JFrame) getParent(),
                    "Programme modifié avec succès", NotificationToast.Type.SUCCESS);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la modification: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerProgramme() {
        int selectedRow = tableProgrammes.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un programme à supprimer",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce programme ?\n" +
            "Cette action ne peut pas être annulée.",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int idProgramme = (Integer) programmeModel.getValueAt(selectedRow, 0);
                programmeDAO.delete(idProgramme);
                loadProgrammes();
                clearForm();
                
                NotificationToast.show((JFrame) getParent(),
                    "Programme supprimé avec succès", NotificationToast.Type.SUCCESS);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void gererMatieres() {
        if (programmeSelectionne == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un programme",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Ouvrir dialog de gestion des matières
        MatiereGestionDialog dialog = new MatiereGestionDialog(this, programmeSelectionne);
        dialog.setVisible(true);
    }
    
    private void chargerProgrammeSelectionne(int row) {
        try {
            int idProgramme = (Integer) programmeModel.getValueAt(row, 0);
            programmeSelectionne = programmeDAO.findById(idProgramme);
        } catch (Exception e) {
            programmeSelectionne = null;
        }
    }
    
    private void clearForm() {
        txtNomProgramme.setText("");
        txtDescription.setText("");
        cmbNiveau.setSelectedIndex(0);
        cmbSpecialite.setSelectedIndex(0);
        spinDuree.setValue(6);
        programmeSelectionne = null;
    }
    
    // Classe interne pour gérer les matières d'un programme
    private static class MatiereGestionDialog extends JDialog {
        // Implementation simplifiée pour la gestion des matières
        public MatiereGestionDialog(Dialog parent, Programme programme) {
            super(parent, "Matières - " + programme.getNom(), true);
            setSize(600, 400);
            setLocationRelativeTo(parent);
            
            JLabel lblInfo = new JLabel("Gestion des matières pour: " + programme.getNom(),
                SwingConstants.CENTER);
            add(lblInfo);
        }
    }
}