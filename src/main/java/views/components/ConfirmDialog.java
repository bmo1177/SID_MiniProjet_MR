/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.components;

import javax.swing.*;
import java.awt.*;
import javax.swing.JPanel;

public class ConfirmDialog {
    
    public static boolean show(Component parent, String title, String message) {
        return show(parent, title, message, "Confirmer", "Annuler");
    }
    
    public static boolean show(Component parent, String title, String message, 
                              String confirmText, String cancelText) {
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), 
                                    title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parent);
        
        // Message
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messagePanel.setBackground(Color.WHITE);
        
        JLabel lblIcon = new JLabel("⚠", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblIcon.setForeground(new Color(230, 126, 34));
        
        JLabel lblMessage = new JLabel("<html><center>" + message + "</center></html>");
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        
        messagePanel.add(lblIcon, BorderLayout.WEST);
        messagePanel.add(lblMessage, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(248, 249, 250));
        
        final boolean[] result = {false};
        
        JButton btnConfirm = new JButton(confirmText);
        btnConfirm.setBackground(new Color(231, 76, 60));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });
        
        JButton btnCancel = new JButton(cancelText);
        btnCancel.setBackground(new Color(189, 195, 199));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnConfirm);
        
        dialog.add(messagePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
        
        return result[0];
    }
}
