/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.components;

import javax.swing.*;
import java.awt.*;
import javax.swing.JPanel;

public class NotificationToast {
    
    public static void show(JFrame parent, String message, Type type) {
        JDialog toast = new JDialog(parent);
        toast.setUndecorated(true);
        toast.setLayout(new BorderLayout());
        toast.setAlwaysOnTop(true);
        
        JPanel panel = new JPanel();
        panel.setBackground(type.getColor());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(type.getColor().darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        JLabel label = new JLabel(type.getIcon() + " " + message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label);
        
        toast.add(panel);
        toast.pack();
        
        // Position en bas à droite
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toast.setLocation(
            screenSize.width - toast.getWidth() - 20,
            screenSize.height - toast.getHeight() - 50
        );
        
        toast.setOpacity(0.95f);
        toast.setVisible(true);
        
        // Auto-fermeture après 3 secondes avec animation fade-out
        Timer timer = new Timer(3000, e -> {
            Timer fadeTimer = new Timer(50, null);
            fadeTimer.addActionListener(evt -> {
                float opacity = toast.getOpacity() - 0.1f;
                if (opacity <= 0) {
                    fadeTimer.stop();
                    toast.dispose();
                } else {
                    toast.setOpacity(opacity);
                }
            });
            fadeTimer.start();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    public enum Type {
        SUCCESS("✓", new Color(46, 204, 113)),
        ERROR("✗", new Color(231, 76, 60)),
        WARNING("⚠", new Color(230, 126, 34)),
        INFO("ℹ", new Color(52, 152, 219));
        
        private String icon;
        private Color color;
        
        Type(String icon, Color color) {
            this.icon = icon;
            this.color = color;
        }
        
        public String getIcon() { return icon; }
        public Color getColor() { return color; }
    }
}