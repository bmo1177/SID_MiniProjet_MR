/*
 * Panel avancé de statistiques pour l'administration
 * Affiche des graphiques et données statistiques complètes
 */
package views.admin;

import services.StatistiquesService;
import views.components.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StatistiquesPanel extends JPanel {
    private StatistiquesService statistiquesService;
    private JComboBox<String> cmbAnnee;
    private JPanel chartPanel;
    private JTable tableStats;
    private DefaultTableModel tableModel;
    
    // Composants de statistiques
    private StatCard cardTotalEtudiants;
    private StatCard cardTauxReussite;
    private StatCard cardMoyenneGlobale;
    private StatCard cardNbProgrammes;

    public StatistiquesPanel() {
        this.statistiquesService = new StatistiquesService();
        initComponents();
        loadStatistiques();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // En-tête avec sélecteur d'année
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel principal avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        tabbedPane.addTab("📊 Vue d'ensemble", createOverviewPanel());
        tabbedPane.addTab("📈 Programmes", createProgrammeStatsPanel());
        tabbedPane.addTab("🏆 Top Étudiants", createTopEtudiantsPanel());
        tabbedPane.addTab("📋 Rapports", createReportsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Titre
        JLabel lblTitle = new JLabel("📊 Statistiques et Rapports");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(44, 62, 80));
        
        // Sélecteur d'année
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        selectionPanel.setOpaque(false);
        
        JLabel lblAnnee = new JLabel("Année universitaire :");
        lblAnnee.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        cmbAnnee = new JComboBox<>(new String[]{"2024/2025", "2025/2026", "2026/2027"});
        cmbAnnee.setSelectedItem("2025/2026");
        cmbAnnee.setPreferredSize(new Dimension(150, 30));
        cmbAnnee.addActionListener(e -> loadStatistiques());
        
        JButton btnRefresh = new JButton("🔄");
        btnRefresh.setToolTipText("Actualiser");
        btnRefresh.setPreferredSize(new Dimension(40, 30));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadStatistiques());
        
        selectionPanel.add(lblAnnee);
        selectionPanel.add(cmbAnnee);
        selectionPanel.add(btnRefresh);
        
        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(selectionPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Cards de statistiques principales
        JPanel cardsPanel = createStatsCardsPanel();
        panel.add(cardsPanel, BorderLayout.NORTH);
        
        // Graphiques
        JPanel chartsPanel = createChartsPanel();
        panel.add(chartsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatsCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 15));
        panel.setOpaque(false);
        
        cardTotalEtudiants = new StatCard("Total Étudiants", "0", new Color(52, 152, 219));
        cardTauxReussite = new StatCard("Taux de Réussite", "0%", new Color(46, 204, 113));
        cardMoyenneGlobale = new StatCard("Moyenne Globale", "0.00", new Color(155, 89, 182));
        cardNbProgrammes = new StatCard("Programmes Actifs", "0", new Color(230, 126, 34));
        
        panel.add(cardTotalEtudiants);
        panel.add(cardTauxReussite);
        panel.add(cardMoyenneGlobale);
        panel.add(cardNbProgrammes);
        
        return panel;
    }
    
    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setOpaque(false);
        
        // Graphique répartition par statut
        JPanel statutChart = createStatutChart();
        panel.add(statutChart);
        
        // Graphique par programme
        JPanel programmeChart = createProgrammeChart();
        panel.add(programmeChart);
        
        return panel;
    }
    
    private JPanel createStatutChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Répartition par Statut"));
        
        // Simulation d'un graphique en secteurs
        JPanel chartArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int centerX = width / 2;
                int centerY = height / 2;
                int radius = Math.min(width, height) / 3;
                
                // Données simulées (à remplacer par de vraies données)
                int[] values = {65, 25, 10}; // Admis, Redoublant, Exclu
                Color[] colors = {
                    new Color(46, 204, 113),   // Vert pour admis
                    new Color(230, 126, 34),   // Orange pour redoublant
                    new Color(231, 76, 60)     // Rouge pour exclu
                };
                String[] labels = {"Admis", "Redoublants", "Exclus"};
                
                int startAngle = 0;
                for (int i = 0; i < values.length; i++) {
                    int arcAngle = (int) (values[i] * 360.0 / 100.0);
                    g2.setColor(colors[i]);
                    g2.fillArc(centerX - radius, centerY - radius, 
                              radius * 2, radius * 2, startAngle, arcAngle);
                    startAngle += arcAngle;
                }
                
                // Légende
                int legendY = height - 80;
                for (int i = 0; i < labels.length; i++) {
                    g2.setColor(colors[i]);
                    g2.fillRect(20, legendY + i * 20, 15, 15);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2.drawString(labels[i] + " (" + values[i] + "%)", 
                                 40, legendY + i * 20 + 12);
                }
            }
        };
        
        panel.add(chartArea, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createProgrammeChart() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Performances par Programme"));
        
        // Graphique en barres simulé
        JPanel chartArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                String[] programmes = {"L1 Info", "L2 Info", "L3 Info", "M1 GL", "M2 GL"};
                double[] moyennes = {12.5, 13.2, 14.1, 15.8, 16.2};
                
                int width = getWidth();
                int height = getHeight();
                int barWidth = width / programmes.length - 20;
                int maxHeight = height - 100;
                
                g2.setColor(new Color(52, 152, 219));
                for (int i = 0; i < programmes.length; i++) {
                    int barHeight = (int) (moyennes[i] * maxHeight / 20.0);
                    int x = i * (barWidth + 15) + 10;
                    int y = height - barHeight - 60;
                    
                    g2.fillRect(x, y, barWidth, barHeight);
                    
                    // Valeur au-dessus de la barre
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    g2.drawString(String.format("%.1f", moyennes[i]), 
                                 x + barWidth/2 - 10, y - 5);
                    
                    // Nom du programme
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    g2.drawString(programmes[i], x, height - 40);
                    
                    g2.setColor(new Color(52, 152, 219));
                }
            }
        };
        
        panel.add(chartArea, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createProgrammeStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Table des statistiques par programme
        String[] columns = {"Programme", "Nb Étudiants", "Moyenne", "Taux Réussite", "Meilleure Note", "Note Min"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Statistiques Détaillées par Programme"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel d'actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        
        JButton btnExportExcel = new JButton("📊 Exporter Excel");
        btnExportExcel.setBackground(new Color(46, 204, 113));
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.setFocusPainted(false);
        btnExportExcel.addActionListener(e -> exporterVersExcel());
        
        actionPanel.add(btnExportExcel);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTopEtudiantsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Table top étudiants
        String[] columns = {"Rang", "Nom", "Prénom", "Programme", "Moyenne", "Statut"};
        DefaultTableModel topModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Données simulées pour le top 10
        String[][] topData = {
            {"1", "BENALI", "Sara", "M2 Génie Logiciel", "18.75", "Admis"},
            {"2", "HAMDI", "Mohamed", "M2 IA", "18.45", "Admis"},
            {"3", "CHERIF", "Amina", "L3 Informatique", "18.20", "Admis"},
            {"4", "MANSOURI", "Youssef", "M1 Réseaux", "17.95", "Admis"},
            {"5", "BOUAZIZ", "Fatima", "L3 SI", "17.80", "Admis"},
            {"6", "SAID", "Ali", "M2 Sécurité", "17.65", "Admis"},
            {"7", "KADI", "Nour", "L2 Informatique", "17.50", "Admis"},
            {"8", "BRAHIM", "Omar", "M1 GL", "17.35", "Admis"},
            {"9", "GHARBI", "Leila", "L3 Informatique", "17.20", "Admis"},
            {"10", "MOURAD", "Karim", "M2 IA", "17.05", "Admis"}
        };
        
        for (String[] row : topData) {
            topModel.addRow(row);
        }
        
        JTable topTable = new JTable(topModel);
        topTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        topTable.setRowHeight(30);
        topTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        topTable.getTableHeader().setBackground(new Color(230, 126, 34));
        topTable.getTableHeader().setForeground(Color.WHITE);
        
        // Colorer les 3 premiers rangs
        topTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row == 0) { // 1er
                        c.setBackground(new Color(255, 215, 0, 50)); // Or
                    } else if (row == 1) { // 2ème
                        c.setBackground(new Color(192, 192, 192, 50)); // Argent
                    } else if (row == 2) { // 3ème
                        c.setBackground(new Color(205, 127, 50, 50)); // Bronze
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane topScrollPane = new JScrollPane(topTable);
        topScrollPane.setBorder(BorderFactory.createTitledBorder("🏆 Top 10 des Meilleurs Étudiants"));
        
        panel.add(topScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel des rapports rapides
        JPanel quickReportsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        quickReportsPanel.setOpaque(false);
        
        quickReportsPanel.add(createReportCard("📊 Rapport Global", 
            "Statistiques complètes de l'année", () -> genererRapportGlobal()));
        quickReportsPanel.add(createReportCard("📈 Évolution Notes", 
            "Évolution des moyennes par semestre", () -> genererRapportEvolution()));
        quickReportsPanel.add(createReportCard("👥 Rapport Programmes", 
            "Analyse détaillée par programme", () -> genererRapportProgrammes()));
        quickReportsPanel.add(createReportCard("🎯 Taux de Réussite", 
            "Analyse des taux de réussite", () -> genererRapportReussite()));
        quickReportsPanel.add(createReportCard("📋 Bulletin Global", 
            "Bulletin consolidé institutionnel", () -> genererBulletinInstitutionnel()));
        quickReportsPanel.add(createReportCard("⚠️ Étudiants à Risque", 
            "Liste des étudiants en difficulté", () -> genererRapportRisque()));
        
        panel.add(quickReportsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReportCard(String title, String description, Runnable action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDesc = new JLabel("<html><center>" + description + "</center></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(127, 140, 141));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton btnGenerate = new JButton("Générer");
        btnGenerate.setBackground(new Color(52, 152, 219));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);
        btnGenerate.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGenerate.addActionListener(e -> action.run());
        
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(15));
        card.add(btnGenerate);
        
        return card;
    }
    
    private void loadStatistiques() {
        // Charger les données statistiques depuis la base
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Simuler le chargement des statistiques
                    Thread.sleep(500); // Simulation
                    
                    SwingUtilities.invokeLater(() -> {
                        // Mettre à jour les cards
                        cardTotalEtudiants.setValue("1,247");
                        cardTauxReussite.setValue("74.2%");
                        cardMoyenneGlobale.setValue("13.45");
                        cardNbProgrammes.setValue("12");
                        
                        // Mettre à jour le tableau
                        updateProgrammeTable();
                    });
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(StatistiquesPanel.this,
                            "Erreur lors du chargement des statistiques: " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void updateProgrammeTable() {
        tableModel.setRowCount(0);
        
        // Données simulées pour les programmes
        Object[][] data = {
            {"L1 Informatique", "245", "12.8", "68%", "19.5", "4.2"},
            {"L2 Informatique", "198", "13.2", "72%", "18.8", "5.1"},
            {"L3 Informatique", "165", "14.1", "78%", "19.2", "6.3"},
            {"M1 Génie Logiciel", "87", "15.2", "85%", "19.8", "8.7"},
            {"M2 Génie Logiciel", "62", "16.1", "89%", "19.9", "10.2"},
            {"M1 Réseaux", "45", "14.8", "82%", "18.9", "7.8"},
            {"M2 Sécurité", "38", "15.9", "92%", "19.7", "11.4"}
        };
        
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }
    
    private void exporterVersExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter les statistiques");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers Excel", "xlsx"));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
        fileChooser.setSelectedFile(new File("statistiques_" + sdf.format(new Date()) + ".xlsx"));
        
        int userChoice = fileChooser.showSaveDialog(this);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }
            
            final String finalFilePath = filePath; // Make it effectively final
            
            // Export en arrière-plan
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        statistiquesService.exporterStatistiquesExcel(2, finalFilePath); // Année 2
                        
                        SwingUtilities.invokeLater(() -> {
                            NotificationToast.show(
                                (JFrame) SwingUtilities.getWindowAncestor(StatistiquesPanel.this),
                                "Export Excel terminé avec succès !",
                                NotificationToast.Type.SUCCESS
                            );
                        });
                        
                    } catch (Exception e) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(StatistiquesPanel.this,
                                "Erreur lors de l'export: " + e.getMessage(),
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                    return null;
                }
            };
            worker.execute();
        }
    }
    
    // Méthodes pour générer les rapports
    private void genererRapportGlobal() {
        NotificationToast.show(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Génération du rapport global en cours...",
            NotificationToast.Type.INFO
        );
    }
    
    private void genererRapportEvolution() {
        NotificationToast.show(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Génération du rapport d'évolution en cours...",
            NotificationToast.Type.INFO
        );
    }
    
    private void genererRapportProgrammes() {
        NotificationToast.show(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Génération du rapport par programmes en cours...",
            NotificationToast.Type.INFO
        );
    }
    
    private void genererRapportReussite() {
        NotificationToast.show(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Analyse des taux de réussite en cours...",
            NotificationToast.Type.INFO
        );
    }
    
    private void genererBulletinInstitutionnel() {
        NotificationToast.show(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Génération du bulletin institutionnel en cours...",
            NotificationToast.Type.INFO
        );
    }
    
    private void genererRapportRisque() {
        NotificationToast.show(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Identification des étudiants à risque en cours...",
            NotificationToast.Type.INFO
        );
    }
}