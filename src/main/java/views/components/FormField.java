/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.components;

import javax.swing.*;
import java.awt.*;
import javax.swing.JPanel;

public class FormField extends JPanel {
    private JLabel label;
    private JTextField textField;
    private JLabel errorLabel;
    
    public FormField(String labelText, boolean required) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        
        // Label
        label = new JLabel(labelText + (required ? " *" : ""));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(52, 73, 94));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Champ de texte
        textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Label d'erreur
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setVisible(false);
        
        add(label);
        add(Box.createVerticalStrut(5));
        add(textField);
        add(Box.createVerticalStrut(2));
        add(errorLabel);
        add(Box.createVerticalStrut(10));
    }
    
    public String getText() {
        return textField.getText();
    }
    
    public void setText(String text) {
        textField.setText(text);
    }
    
    public void setError(String error) {
        if (error == null || error.isEmpty()) {
            errorLabel.setVisible(false);
            textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } else {
            errorLabel.setText(error);
            errorLabel.setVisible(true);
            textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        }
    }
    
    public void clearError() {
        setError(null);
    }
    
    public JTextField getTextField() {
        return textField;
    }
}

