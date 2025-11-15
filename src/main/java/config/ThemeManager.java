package config;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.Border;

/**
 * World-class design system for the Gestion Scolarité application
 * Implements modern UI/UX principles with professional aesthetics
 */
public class ThemeManager {
    
    // --- PRIMARY COLOR PALETTE ---
    public static final Color PRIMARY_BLUE = new Color(37, 99, 235);      // Modern blue
    public static final Color PRIMARY_BLUE_DARK = new Color(29, 78, 216); // Darker blue
    public static final Color PRIMARY_BLUE_LIGHT = new Color(59, 130, 246); // Lighter blue
    
    // --- SEMANTIC COLORS ---
    public static final Color SUCCESS_GREEN = new Color(16, 185, 129);
    public static final Color WARNING_ORANGE = new Color(245, 158, 11);
    public static final Color ERROR_RED = new Color(239, 68, 68);
    public static final Color INFO_CYAN = new Color(6, 182, 212);
    
    // --- NEUTRAL COLORS ---
    public static final Color GRAY_50 = new Color(249, 250, 251);
    public static final Color GRAY_100 = new Color(243, 244, 246);
    public static final Color GRAY_200 = new Color(229, 231, 235);
    public static final Color GRAY_300 = new Color(209, 213, 219);
    public static final Color GRAY_400 = new Color(156, 163, 175);
    public static final Color GRAY_500 = new Color(107, 114, 128);
    public static final Color GRAY_600 = new Color(75, 85, 99);
    public static final Color GRAY_700 = new Color(55, 65, 81);
    public static final Color GRAY_800 = new Color(31, 41, 55);
    public static final Color GRAY_900 = new Color(17, 24, 39);
    
    // --- BACKGROUND COLORS ---
    public static final Color SURFACE_WHITE = Color.WHITE;
    public static final Color SURFACE_LIGHT = GRAY_50;
    public static final Color SURFACE_CARD = new Color(255, 255, 255);
    
    // --- TYPOGRAPHY ---
    public static final Font FONT_HEADING_LARGE = new Font("Inter", Font.BOLD, 24);
    public static final Font FONT_HEADING_MEDIUM = new Font("Inter", Font.BOLD, 20);
    public static final Font FONT_HEADING_SMALL = new Font("Inter", Font.BOLD, 16);
    public static final Font FONT_BODY_LARGE = new Font("Inter", Font.PLAIN, 16);
    public static final Font FONT_BODY_MEDIUM = new Font("Inter", Font.PLAIN, 14);
    public static final Font FONT_BODY_SMALL = new Font("Inter", Font.PLAIN, 12);
    public static final Font FONT_CAPTION = new Font("Inter", Font.PLAIN, 11);
    public static final Font FONT_MONO = new Font("JetBrains Mono", Font.PLAIN, 12);
    
    // --- SPACING SYSTEM ---
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 16;
    public static final int SPACING_LG = 24;
    public static final int SPACING_XL = 32;
    public static final int SPACING_2XL = 48;
    public static final int SPACING_3XL = 64;
    
    // --- BORDER RADIUS ---
    public static final int RADIUS_SM = 6;
    public static final int RADIUS_MD = 8;
    public static final int RADIUS_LG = 12;
    public static final int RADIUS_XL = 16;
    
    // --- SHADOWS ---
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 8);
    public static final Color SHADOW_COLOR_MEDIUM = new Color(0, 0, 0, 15);
    public static final Color SHADOW_COLOR_STRONG = new Color(0, 0, 0, 25);
    
    /**
     * Initialize the application-wide look and feel
     */
    public static void initializeTheme() {
        try {
            // Use default Metal LAF for Java 23 compatibility
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            
            // Customize global UI properties
            UIManager.put("Button.font", FONT_BODY_MEDIUM);
            UIManager.put("Label.font", FONT_BODY_MEDIUM);
            UIManager.put("TextField.font", FONT_BODY_MEDIUM);
            UIManager.put("Table.font", FONT_BODY_MEDIUM);
            UIManager.put("TableHeader.font", FONT_BODY_MEDIUM);
            
            // Custom button styling
            UIManager.put("Button.background", PRIMARY_BLUE);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.select", PRIMARY_BLUE_DARK);
            
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
    }
    
    /**
     * Creates a modern card-style border with shadow effect
     */
    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(SPACING_MD, SPACING_MD, SPACING_MD, SPACING_MD)
        );
    }
    
    /**
     * Creates a subtle border for input fields
     */
    public static Border createInputBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1, true),
            BorderFactory.createEmptyBorder(SPACING_SM, SPACING_MD, SPACING_SM, SPACING_MD)
        );
    }
    
    /**
     * Creates a modern button with rounded corners
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                if (getModel().isPressed()) {
                    g2.setColor(PRIMARY_BLUE_DARK);
                } else if (getModel().isRollover()) {
                    g2.setColor(PRIMARY_BLUE_LIGHT);
                } else {
                    g2.setColor(PRIMARY_BLUE);
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RADIUS_MD, RADIUS_MD));
                
                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        button.setFont(FONT_BODY_MEDIUM);
        button.setBorder(BorderFactory.createEmptyBorder(SPACING_SM, SPACING_LG, SPACING_SM, SPACING_LG));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * Creates a secondary button with outline style
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                if (getModel().isPressed()) {
                    g2.setColor(GRAY_100);
                } else if (getModel().isRollover()) {
                    g2.setColor(GRAY_50);
                } else {
                    g2.setColor(Color.WHITE);
                }
                
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RADIUS_MD, RADIUS_MD));
                
                // Border
                g2.setColor(GRAY_300);
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, RADIUS_MD, RADIUS_MD));
                
                // Text
                g2.setColor(GRAY_700);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        button.setFont(FONT_BODY_MEDIUM);
        button.setBorder(BorderFactory.createEmptyBorder(SPACING_SM, SPACING_LG, SPACING_SM, SPACING_LG));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    /**
     * Creates a modern text field with proper styling
     */
    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RADIUS_MD, RADIUS_MD));
                
                // Border
                if (hasFocus()) {
                    g2.setColor(PRIMARY_BLUE);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(GRAY_300);
                    g2.setStroke(new BasicStroke(1));
                }
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, RADIUS_MD, RADIUS_MD));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        field.setFont(FONT_BODY_MEDIUM);
        field.setBorder(BorderFactory.createEmptyBorder(SPACING_SM, SPACING_MD, SPACING_SM, SPACING_MD));
        field.setBackground(Color.WHITE);
        field.setOpaque(false);
        
        // Placeholder functionality
        if (placeholder != null && !placeholder.isEmpty()) {
            field.setText(placeholder);
            field.setForeground(GRAY_400);
            field.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(GRAY_900);
                    }
                }
                
                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    if (field.getText().isEmpty()) {
                        field.setText(placeholder);
                        field.setForeground(GRAY_400);
                    }
                }
            });
        }
        
        return field;
    }
    
    /**
     * Creates a modern panel with card styling
     */
    public static JPanel createCard() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2.setColor(SHADOW_COLOR);
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth()-2, getHeight()-2, RADIUS_LG, RADIUS_LG));
                
                // Background
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-2, getHeight()-2, RADIUS_LG, RADIUS_LG));
                
                g2.dispose();
            }
        };
        
        panel.setBackground(SURFACE_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(SPACING_LG, SPACING_LG, SPACING_LG, SPACING_LG));
        panel.setOpaque(false);
        
        return panel;
    }
    
    /**
     * Get color based on grade/note
     */
    public static Color getGradeColor(double grade) {
        if (grade >= 16) return SUCCESS_GREEN;
        if (grade >= 14) return new Color(34, 197, 94);  // Light green
        if (grade >= 12) return PRIMARY_BLUE;
        if (grade >= 10) return WARNING_ORANGE;
        if (grade >= 8) return new Color(249, 115, 22);  // Orange-red
        return ERROR_RED;
    }
    
    /**
     * Get color based on status
     */
    public static Color getStatusColor(String status) {
        if (status == null) return GRAY_400;
        
        switch (status.toLowerCase()) {
            case "admis":
            case "validé":
            case "actif":
                return SUCCESS_GREEN;
            case "redoublant":
            case "en_attente":
            case "en_cours":
                return WARNING_ORANGE;
            case "exclu":
            case "inactif":
            case "refusé":
                return ERROR_RED;
            case "nouveau":
            case "information":
                return INFO_CYAN;
            default:
                return GRAY_400;
        }
    }
}
