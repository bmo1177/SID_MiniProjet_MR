/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;


public class StatCard extends JPanel {
    
    public StatCard(String title, String value, Color color) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(color);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setPreferredSize(new Dimension(150, 100));
        
        // Valeur
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(Color.WHITE);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Titre
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(255, 255, 255, 200));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(Box.createVerticalGlue());
        add(lblValue);
        add(Box.createVerticalStrut(5));
        add(lblTitle);
        add(Box.createVerticalGlue());
    }
    
    public StatCard(String title, String value, String icon, Color color) {
        this(title, value, color);
        
        // Ajouter l'icône en haut
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(lblIcon, 0);
    }
    
    public void setValue(String value) {
        // Update the value label
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getFont().getSize() == 24) { // This is the value label
                    label.setText(value);
                    break;
                }
            }
        }
        repaint();
    }
}
