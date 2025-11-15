/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.scolarite;

import javax.swing.*;
import java.awt.*;
import models.Utilisateur;


import models.*;
import services.*;
import views.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ScolariteDashboard extends JPanel {
    private Utilisateur currentUser;
    private EtudiantService etudiantService;
    
    public ScolariteDashboard(Utilisateur user) {
        this.currentUser = user;
        this.etudiantService = new EtudiantService();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("🏠 Accueil", createAccueilPanel());
        tabbedPane.addTab("👨‍🎓 Étudiants", createEtudiantsPanel());
        tabbedPane.addTab("📝 Inscriptions", createInscriptionsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createAccueilPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(createQuickCard("Nouvel Étudiant", "➕", new Color(52, 152, 219)));
        panel.add(createQuickCard("Nouvelle Inscription", "📝", new Color(46, 204, 113)));
        panel.add(createQuickCard("Rechercher", "🔍", new Color(230, 126, 34)));
        panel.add(createQuickCard("Statistiques", "📊", new Color(155, 89, 182)));
        
        return panel;
    }
    
    private JPanel createQuickCard(String title, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        lblIcon.setForeground(Color.WHITE);
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        
        card.add(lblIcon, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createEtudiantsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // En-tête
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        views.components.ModernSearchBar searchBar = new views.components.ModernSearchBar("Rechercher un étudiant...");
        topPanel.add(searchBar, BorderLayout.CENTER);
        
        JButton btnNouveau = new JButton("➕ Nouvel Étudiant");
        btnNouveau.setBackground(new Color(52, 152, 219));
        btnNouveau.setForeground(Color.WHITE);
        btnNouveau.setFocusPainted(false);
        topPanel.add(btnNouveau, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Tableau
        String[] columns = {"Nom", "Prénom", "Origine", "Email", "Programme", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        CustomTable table = new CustomTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInscriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        
        JLabel lblTitle = new JLabel("Gestion des Inscriptions", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.CENTER);
        
        return panel;
    }
}