/*
 * Dialog pour créer ou modifier une épreuve
 * Utilisé par les enseignants
 */
package views.components;

import models.*;
import dao.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EpreuveFormDialog extends JDialog {
    private EpreuveDAO epreuveDAO;
    private MatiereDAO matiereDAO;
    private Enseignant enseignant;
    private Epreuve epreuve; // null pour création, objet existant pour modification
    
    private JComboBox<String> cmbType;
    private JTextField txtIntitule;
    private JComboBox<Matiere> cmbMatiere;
    private JTextField txtDate;
    private JSpinner spinCoefficient;
    private JTextArea txtDescription;
    private JCheckBox chkActive;
    
    private boolean confirmed = false;

    public EpreuveFormDialog(Frame parent, Enseignant enseignant, Epreuve epreuve) {
        super(parent, epreuve == null ? "Nouvelle Épreuve" : "Modifier l'Épreuve", true);
        this.enseignant = enseignant;
        this.epreuve = epreuve;
        this.epreuveDAO = new EpreuveDAO();
        this.matiereDAO = new MatiereDAO();
        
        initComponents();
        loadData();
        setupEvents();
        
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel principal du formulaire
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        // Type d'épreuve
        panel.add(createFieldPanel("Type d'épreuve :", 
            cmbType = new JComboBox<>(new String[]{"Contrôle", "Examen", "TP", "Projet", "Exposé", "Devoir"})));
        
        // Intitulé
        panel.add(createFieldPanel("Intitulé :", 
            txtIntitule = new JTextField()));
        
        // Matière
        panel.add(createFieldPanel("Matière :", 
            cmbMatiere = new JComboBox<>()));
        
        // Date
        panel.add(createFieldPanel("Date (jj/mm/aaaa) :", 
            txtDate = new JTextField()));
        txtDate.setToolTipText("Format: jj/mm/aaaa (ex: 15/12/2025)");
        
        // Coefficient
        panel.add(createFieldPanel("Coefficient :", 
            spinCoefficient = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 10.0, 0.1))));
        
        // Description
        JLabel lblDesc = new JLabel("Description :");
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblDesc);
        
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollDesc);
        
        panel.add(Box.createVerticalStrut(10));
        
        // Épreuve active
        chkActive = new JCheckBox("Épreuve active");
        chkActive.setSelected(true);
        chkActive.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(chkActive);
        
        return panel;
    }
    
    private JPanel createFieldPanel(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(120, 25));
        panel.add(lbl, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton btnSave = new JButton(epreuve == null ? "Créer" : "Modifier");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setPreferredSize(new Dimension(100, 35));
        
        JButton btnCancel = new JButton("Annuler");
        btnCancel.setPreferredSize(new Dimension(100, 35));
        
        panel.add(btnSave);
        panel.add(btnCancel);
        
        return panel;
    }
    
    private void loadData() {
        // Charger les matières
        try {
            List<Matiere> matieres = matiereDAO.findAll();
            for (Matiere matiere : matieres) {
                cmbMatiere.addItem(matiere);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des matières: " + e.getMessage());
        }
        
        // Si modification, remplir les champs
        if (epreuve != null) {
            cmbType.setSelectedItem(epreuve.getTypeEpreuve());
            txtIntitule.setText(epreuve.getIntitule());
            
            // Sélectionner la matière
            for (int i = 0; i < cmbMatiere.getItemCount(); i++) {
                Matiere m = cmbMatiere.getItemAt(i);
                if (m.getIdMatiere() == epreuve.getIdMatiere()) {
                    cmbMatiere.setSelectedIndex(i);
                    break;
                }
            }
            
            if (epreuve.getDateEpreuve() != null) {
                txtDate.setText(epreuve.getDateEpreuve().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            
            spinCoefficient.setValue(epreuve.getCoefficient());
            txtDescription.setText(epreuve.getDescription());
            chkActive.setSelected(epreuve.isActive());
        }
    }
    
    private void setupEvents() {
        // Bouton Sauvegarder
        JButton btnSave = (JButton) ((JPanel) getContentPane().getComponent(1))
                          .getComponent(0);
        btnSave.addActionListener(e -> saveEpreuve());
        
        // Bouton Annuler
        JButton btnCancel = (JButton) ((JPanel) getContentPane().getComponent(1))
                           .getComponent(1);
        btnCancel.addActionListener(e -> dispose());
    }
    
    private void saveEpreuve() {
        try {
            // Validation
            if (txtIntitule.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("L'intitulé est obligatoire");
            }
            
            if (cmbMatiere.getSelectedItem() == null) {
                throw new IllegalArgumentException("Veuillez sélectionner une matière");
            }
            
            LocalDate dateEpreuve = null;
            if (!txtDate.getText().trim().isEmpty()) {
                try {
                    dateEpreuve = LocalDate.parse(txtDate.getText(), 
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Date invalide. Format requis: jj/mm/aaaa");
                }
            }
            
            // Créer ou mettre à jour l'épreuve
            if (epreuve == null) {
                epreuve = new Epreuve();
                epreuve.setIdEnseignant(enseignant.getIdEnseignant());
            }
            
            epreuve.setTypeEpreuve((String) cmbType.getSelectedItem());
            epreuve.setIntitule(txtIntitule.getText().trim());
            epreuve.setIdMatiere(((Matiere) cmbMatiere.getSelectedItem()).getIdMatiere());
            epreuve.setDateEpreuve(dateEpreuve);
            epreuve.setCoefficient((Double) spinCoefficient.getValue());
            epreuve.setDescription(txtDescription.getText().trim());
            epreuve.setActive(chkActive.isSelected());
            epreuve.setIdAnnee(2); // Année scolaire courante
            
            // Sauvegarder
            if (epreuve.getIdEpreuve() == 0) {
                epreuveDAO.insert(epreuve);
                NotificationToast.show((JFrame) getParent(), 
                    "Épreuve créée avec succès", NotificationToast.Type.SUCCESS);
            } else {
                epreuveDAO.update(epreuve);
                NotificationToast.show((JFrame) getParent(), 
                    "Épreuve modifiée avec succès", NotificationToast.Type.SUCCESS);
            }
            
            confirmed = true;
            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + ex.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Epreuve getEpreuve() {
        return epreuve;
    }
}