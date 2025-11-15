/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.components;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class CustomTable extends JTable {
    
    public CustomTable(DefaultTableModel model) {
        super(model);
        setupStyle();
    }
    
    private void setupStyle() {
        // Police et taille
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setRowHeight(35);
        
        // Grille
        setShowGrid(true);
        setGridColor(new Color(230, 230, 230));
        
        // Sélection
        setSelectionBackground(new Color(52, 152, 219, 100));
        setSelectionForeground(Color.BLACK);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // En-tête
        JTableHeader header = getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 152, 219));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        
        // Alternance de couleurs de lignes
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                
                return c;
            }
        });
    }
    
    /**
     * Rend la table triable
     */
    public void makeSortable() {
        setAutoCreateRowSorter(true);
    }
    
    /**
     * Exporte les données en CSV
     */
    public String exportToCSV() {
        StringBuilder csv = new StringBuilder();
        
        // En-têtes
        for (int i = 0; i < getColumnCount(); i++) {
            csv.append(getColumnName(i));
            if (i < getColumnCount() - 1) csv.append(",");
        }
        csv.append("\n");
        
        // Données
        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColumnCount(); col++) {
                Object value = getValueAt(row, col);
                csv.append(value != null ? value.toString() : "");
                if (col < getColumnCount() - 1) csv.append(",");
            }
            csv.append("\n");
        }
        
        return csv.toString();
    }
}

