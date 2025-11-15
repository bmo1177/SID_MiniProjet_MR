/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.components;

import javax.swing.*;
import java.awt.*;
import javax.swing.JPanel;

public class LoadingDialog extends JDialog {
    
    public LoadingDialog(Frame parent, String message) {
        super(parent, "Chargement", false);
        setUndecorated(true);
        setLayout(new BorderLayout(10, 10));
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        panel.setBackground(Color.WHITE);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(200, 20));
        
        JLabel lblMessage = new JLabel(message, SwingConstants.CENTER);
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        panel.add(lblMessage, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        
        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }
    
    public void close() {
        setVisible(false);
        dispose();
    }
}
