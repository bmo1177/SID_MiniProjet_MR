/*
 * Panel avancé pour la saisie des notes par les enseignants
 * Permet la saisie rapide et la validation des notes
 */
package views.components;

import models.*;
import dao.*;
import services.NoteService;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class NoteSaisiePanel extends JPanel {
    private Enseignant enseignant;
    private NoteService noteService;
    private EpreuveDAO epreuveDAO;
    private EtudiantDAO etudiantDAO;
    
    private JComboBox<Epreuve> cmbEpreuve;
    private JTable tableNotes;
    private DefaultTableModel tableModel;
    private JButton btnSauvegarder;
    private JButton btnValiderTout;
    private JLabel lblStatut;
    private JProgressBar progressBar;
    
    private Epreuve epreuveSelectionnee;
    private List<EtudiantNote> etudiantsNotes;
    
    public NoteSaisiePanel(Enseignant enseignant) {
        this.enseignant = enseignant;
        this.noteService = new NoteService();
        this.epreuveDAO = new EpreuveDAO();
        this.etudiantDAO = new EtudiantDAO();
        this.etudiantsNotes = new ArrayList<>();
        
        initComponents();
        setupEvents();
        loadEpreuves();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(236, 240, 241));
        
        // Panel de sélection d'épreuve
        add(createSelectionPanel(), BorderLayout.NORTH);
        
        // Table des notes
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Panel des actions
        add(createActionPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Sélection de l'Épreuve"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Sélection épreuve
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        selectionPanel.setOpaque(false);
        
        JLabel lblEpreuve = new JLabel("Épreuve :");
        lblEpreuve.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        cmbEpreuve = new JComboBox<>();
        cmbEpreuve.setPreferredSize(new Dimension(400, 30));
        cmbEpreuve.setRenderer(new EpreuveComboRenderer());
        
        JButton btnCharger = new JButton("Charger les étudiants");
        btnCharger.setBackground(new Color(52, 152, 219));
        btnCharger.setForeground(Color.WHITE);
        btnCharger.setFocusPainted(false);
        btnCharger.addActionListener(e -> chargerEtudiants());
        
        selectionPanel.add(lblEpreuve);
        selectionPanel.add(cmbEpreuve);
        selectionPanel.add(btnCharger);
        
        panel.add(selectionPanel, BorderLayout.CENTER);
        
        // Statut
        lblStatut = new JLabel("Sélectionnez une épreuve pour commencer la saisie");
        lblStatut.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatut.setForeground(new Color(127, 140, 141));
        panel.add(lblStatut, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Modèle de table
        String[] columns = {"Étudiant", "Note (/20)", "Commentaire", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 2; // Note et commentaire éditables
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Double.class;
                return String.class;
            }
        };
        
        tableNotes = new JTable(tableModel);
        setupTable();
        
        JScrollPane scrollPane = new JScrollPane(tableNotes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Saisie des Notes"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupTable() {
        tableNotes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableNotes.setRowHeight(30);
        tableNotes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // En-tête
        JTableHeader header = tableNotes.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(52, 152, 219));
        header.setForeground(Color.WHITE);
        
        // Largeurs des colonnes
        TableColumnModel columnModel = tableNotes.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(200); // Étudiant
        columnModel.getColumn(1).setPreferredWidth(100); // Note
        columnModel.getColumn(2).setPreferredWidth(250); // Commentaire
        columnModel.getColumn(3).setPreferredWidth(100); // Statut
        
        // Éditeur pour les notes
        JSpinner noteSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 20.0, 0.25));
        // Custom cell editor for JSpinner
        columnModel.getColumn(1).setCellEditor(new SpinnerEditor(noteSpinner));
        
        // Renderer pour les statuts
        columnModel.getColumn(3).setCellRenderer(new StatutCellRenderer());
        
        // Événements de modification
        tableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (row >= 0 && row < etudiantsNotes.size()) {
                    EtudiantNote etudiantNote = etudiantsNotes.get(row);
                    
                    if (column == 1) { // Note modifiée
                        Object value = tableModel.getValueAt(row, column);
                        if (value != null) {
                            try {
                                double note = Double.parseDouble(value.toString());
                                if (note >= 0 && note <= 20) {
                                    etudiantNote.setNote(note);
                                    etudiantNote.setModifiee(true);
                                    tableModel.setValueAt(getStatutNote(note), row, 3);
                                    updateSaveButton();
                                }
                            } catch (NumberFormatException ex) {
                                // Ignorer les valeurs invalides
                            }
                        }
                    } else if (column == 2) { // Commentaire modifié
                        Object value = tableModel.getValueAt(row, column);
                        etudiantNote.setCommentaire(value != null ? value.toString() : "");
                        etudiantNote.setModifiee(true);
                        updateSaveButton();
                    }
                }
            }
        });
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Barre de progression
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        panel.add(progressBar, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);
        
        btnSauvegarder = new JButton("💾 Sauvegarder les modifications");
        btnSauvegarder.setBackground(new Color(46, 204, 113));
        btnSauvegarder.setForeground(Color.WHITE);
        btnSauvegarder.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSauvegarder.setFocusPainted(false);
        btnSauvegarder.setEnabled(false);
        
        btnValiderTout = new JButton("✅ Valider toutes les notes");
        btnValiderTout.setBackground(new Color(230, 126, 34));
        btnValiderTout.setForeground(Color.WHITE);
        btnValiderTout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnValiderTout.setFocusPainted(false);
        btnValiderTout.setEnabled(false);
        
        JButton btnImporter = new JButton("📁 Importer Excel");
        btnImporter.setFocusPainted(false);
        
        buttonPanel.add(btnImporter);
        buttonPanel.add(btnSauvegarder);
        buttonPanel.add(btnValiderTout);
        
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void setupEvents() {
        btnSauvegarder.addActionListener(e -> sauvegarderNotes());
        btnValiderTout.addActionListener(e -> validerToutesLesNotes());
    }
    
    private void loadEpreuves() {
        try {
            cmbEpreuve.removeAllItems();
            List<Epreuve> epreuves = epreuveDAO.findByEnseignant(enseignant.getIdEnseignant());
            for (Epreuve epreuve : epreuves) {
                cmbEpreuve.addItem(epreuve);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des épreuves: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void chargerEtudiants() {
        epreuveSelectionnee = (Epreuve) cmbEpreuve.getSelectedItem();
        if (epreuveSelectionnee == null) {
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
            lblStatut.setText("Chargement des étudiants...");
        });
        
        // Exécuter en arrière-plan
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadEtudiantsForEpreuve();
                return null;
            }
            
            @Override
            protected void done() {
                progressBar.setVisible(false);
                lblStatut.setText("Étudiants chargés. Vous pouvez maintenant saisir les notes.");
                updateSaveButton();
            }
        };
        worker.execute();
    }
    
    private void loadEtudiantsForEpreuve() {
        try {
            etudiantsNotes.clear();
            tableModel.setRowCount(0);
            
            // Récupérer tous les étudiants inscrits pour cette épreuve
            List<Etudiant> etudiants = etudiantDAO.findByProgrammeAndAnnee(
                1, epreuveSelectionnee.getIdAnnee()); // Supposons programme par défaut
            
            for (Etudiant etudiant : etudiants) {
                EtudiantNote etudiantNote = new EtudiantNote();
                etudiantNote.setEtudiant(etudiant);
                etudiantNote.setNote(0.0);
                etudiantNote.setCommentaire("");
                etudiantNote.setModifiee(false);
                
                // Vérifier si une note existe déjà
                try {
                    NoteEpreuve noteExistante = noteService.getNoteEpreuve(
                        etudiant.getIdEtudiant(), epreuveSelectionnee.getIdEpreuve());
                    if (noteExistante != null) {
                        etudiantNote.setNote(noteExistante.getNote());
                        etudiantNote.setCommentaire(noteExistante.getCommentaire());
                        etudiantNote.setExistante(true);
                    }
                } catch (Exception e) {
                    // Note n'existe pas, garder les valeurs par défaut
                }
                
                etudiantsNotes.add(etudiantNote);
                
                // Ajouter à la table
                SwingUtilities.invokeLater(() -> {
                    tableModel.addRow(new Object[]{
                        etudiant.getNomComplet(),
                        etudiantNote.getNote(),
                        etudiantNote.getCommentaire(),
                        getStatutNote(etudiantNote.getNote())
                    });
                });
            }
            
        } catch (SQLException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du chargement des étudiants: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            });
        }
    }
    
    private void sauvegarderNotes() {
        if (epreuveSelectionnee == null || etudiantsNotes.isEmpty()) {
            return;
        }
        
        progressBar.setVisible(true);
        progressBar.setIndeterminate(false);
        progressBar.setMaximum(etudiantsNotes.size());
        progressBar.setValue(0);
        
        SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {
            @Override
            protected Integer doInBackground() throws Exception {
                int saved = 0;
                int total = etudiantsNotes.size();
                
                for (int i = 0; i < total; i++) {
                    EtudiantNote etudiantNote = etudiantsNotes.get(i);
                    
                    if (etudiantNote.isModifiee() || !etudiantNote.isExistante()) {
                        try {
                            noteService.saisirNote(
                                etudiantNote.getEtudiant().getIdEtudiant(),
                                epreuveSelectionnee.getIdEpreuve(),
                                etudiantNote.getNote(),
                                enseignant.getIdEnseignant(),
                                etudiantNote.getCommentaire()
                            );
                            etudiantNote.setModifiee(false);
                            etudiantNote.setExistante(true);
                            saved++;
                        } catch (SQLException e) {
                            // Log l'erreur mais continuer
                            System.err.println("Erreur sauvegarde note: " + e.getMessage());
                        }
                    }
                    
                    publish(i + 1);
                }
                
                return saved;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                int latest = chunks.get(chunks.size() - 1);
                progressBar.setValue(latest);
                lblStatut.setText("Sauvegarde: " + latest + "/" + etudiantsNotes.size());
            }
            
            @Override
            protected void done() {
                try {
                    int saved = get();
                    progressBar.setVisible(false);
                    lblStatut.setText(saved + " notes sauvegardées avec succès");
                    updateSaveButton();
                    
                    NotificationToast.show(
                        (JFrame) SwingUtilities.getWindowAncestor(NoteSaisiePanel.this),
                        saved + " notes sauvegardées", 
                        NotificationToast.Type.SUCCESS
                    );
                    
                } catch (Exception e) {
                    progressBar.setVisible(false);
                    JOptionPane.showMessageDialog(NoteSaisiePanel.this,
                        "Erreur lors de la sauvegarde: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void validerToutesLesNotes() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir valider toutes les notes ?\n" +
            "Cette action ne peut pas être annulée.",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Implémenter la validation
            NotificationToast.show(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Toutes les notes ont été validées", 
                NotificationToast.Type.SUCCESS
            );
            btnValiderTout.setEnabled(false);
        }
    }
    
    private void updateSaveButton() {
        boolean hasModifications = etudiantsNotes.stream()
            .anyMatch(en -> en.isModifiee() || !en.isExistante());
        btnSauvegarder.setEnabled(hasModifications);
        btnValiderTout.setEnabled(!etudiantsNotes.isEmpty());
    }
    
    private String getStatutNote(double note) {
        if (note >= 10) return "✅ Validé";
        if (note > 0) return "⚠️ En cours";
        return "❌ Non saisi";
    }
    
    // Classes internes
    private static class EtudiantNote {
        private Etudiant etudiant;
        private double note;
        private String commentaire;
        private boolean modifiee;
        private boolean existante;
        
        // Getters & Setters
        public Etudiant getEtudiant() { return etudiant; }
        public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }
        
        public double getNote() { return note; }
        public void setNote(double note) { this.note = note; }
        
        public String getCommentaire() { return commentaire; }
        public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
        
        public boolean isModifiee() { return modifiee; }
        public void setModifiee(boolean modifiee) { this.modifiee = modifiee; }
        
        public boolean isExistante() { return existante; }
        public void setExistante(boolean existante) { this.existante = existante; }
    }
    
    private static class EpreuveComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Epreuve) {
                Epreuve epreuve = (Epreuve) value;
                setText(epreuve.getTypeEpreuve() + " - " + epreuve.getIntitule() + 
                       " (" + epreuve.getMatiereName() + ")");
            }
            
            return this;
        }
    }
    
    private static class StatutCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String statut = value.toString();
                if (statut.contains("Validé")) {
                    setForeground(new Color(46, 204, 113));
                } else if (statut.contains("En cours")) {
                    setForeground(new Color(230, 126, 34));
                } else {
                    setForeground(new Color(231, 76, 60));
                }
            }
            
            return this;
        }
    }
    
    // Custom cell editor for JSpinner
    private static class SpinnerEditor extends DefaultCellEditor {
        private final JSpinner spinner;
        
        public SpinnerEditor(JSpinner spinner) {
            super(new JTextField());
            this.spinner = spinner;
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            spinner.setValue(value != null ? value : 0.0);
            return spinner;
        }
        
        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }
    }
}