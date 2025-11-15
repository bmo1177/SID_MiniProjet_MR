/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.components;

import javax.swing.*;
import java.awt.*;
import javax.swing.JPanel;

public class Breadcrumb extends JPanel {
    
    public Breadcrumb(String... items) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setOpaque(false);
        
        for (int i = 0; i < items.length; i++) {
            JLabel lblItem = new JLabel(items[i]);
            lblItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            if (i == items.length - 1) {
                // Dernier élément (actuel)
                lblItem.setForeground(new Color(52, 152, 219));
                lblItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else {
                lblItem.setForeground(new Color(127, 140, 141));
            }
            
            add(lblItem);
            
            if (i < items.length - 1) {
                JLabel separator = new JLabel(">");
                separator.setForeground(new Color(189, 195, 199));
                separator.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                add(separator);
            }
        }
    }
}