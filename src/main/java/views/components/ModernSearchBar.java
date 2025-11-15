/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views.components;

import config.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Modern search bar component with real-time filtering
 * Features: Icon integration, real-time search, modern styling
 */
public class ModernSearchBar extends JPanel {
    private JTextField searchField;
    private JLabel searchIcon;
    private JButton clearButton;
    private String placeholder;
    private SearchListener searchListener;
    private Timer searchTimer;
    
    public interface SearchListener {
        void onSearchChanged(String searchTerm);
        void onSearchCleared();
    }
    
    public ModernSearchBar(String placeholder) {
        this.placeholder = placeholder;
        initializeComponents();
        setupLayout();
        setupStyling();
        setupBehavior();
    }
    
    private void initializeComponents() {
        searchField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                // Don't paint the default background
                if (!isOpaque()) {
                    super.paintComponent(g);
                    return;
                }
                super.paintComponent(g);
            }
        };
        
        // Search icon using text (can be replaced with actual icon)
        searchIcon = new JLabel("Search") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.GRAY_400);
                g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
                
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("⚲")) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2.drawString("⚲", x, y);
                
                g2.dispose();
            }
        };
        
        clearButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background on hover
                if (getModel().isRollover()) {
                    g2.setColor(ThemeManager.GRAY_100);
                    g2.fillOval(2, 2, getWidth()-4, getHeight()-4);
                }
                
                // X icon
                g2.setColor(ThemeManager.GRAY_400);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int size = 8;
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                g2.drawLine(centerX - size/2, centerY - size/2, centerX + size/2, centerY + size/2);
                g2.drawLine(centerX + size/2, centerY - size/2, centerX - size/2, centerY + size/2);
                
                g2.dispose();
            }
        };
        
        // Search timer for real-time search with delay
        searchTimer = new Timer(300, e -> {
            if (searchListener != null) {
                String text = getSearchText();
                if (text.isEmpty()) {
                    searchListener.onSearchCleared();
                } else {
                    searchListener.onSearchChanged(text);
                }
            }
        });
        searchTimer.setRepeats(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(ThemeManager.SPACING_SM, 0));
        
        // Left side - search icon
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.add(searchIcon, BorderLayout.CENTER);
        iconPanel.setPreferredSize(new Dimension(32, 0));
        add(iconPanel, BorderLayout.WEST);
        
        // Center - text field
        add(searchField, BorderLayout.CENTER);
        
        // Right side - clear button
        JPanel clearPanel = new JPanel(new BorderLayout());
        clearPanel.setOpaque(false);
        clearPanel.add(clearButton, BorderLayout.CENTER);
        clearPanel.setPreferredSize(new Dimension(32, 0));
        clearButton.setVisible(false);
        add(clearPanel, BorderLayout.EAST);
    }
    
    private void setupStyling() {
        // Main panel styling
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(300, 40));
        setOpaque(false);
        
        // Text field styling
        searchField.setFont(ThemeManager.FONT_BODY_MEDIUM);
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(ThemeManager.GRAY_700);
        searchField.setText(placeholder);
        searchField.setCaretColor(ThemeManager.PRIMARY_BLUE);
        
        // Clear button styling
        clearButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        clearButton.setContentAreaFilled(false);
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setPreferredSize(new Dimension(24, 24));
    }
    
    private void setupBehavior() {
        // Placeholder behavior
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(ThemeManager.GRAY_900);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(ThemeManager.GRAY_400);
                    clearButton.setVisible(false);
                }
            }
        });
        
        // Real-time search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { scheduleSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { scheduleSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { scheduleSearch(); }
            
            private void scheduleSearch() {
                String text = searchField.getText();
                boolean showClear = !text.isEmpty() && !text.equals(placeholder);
                clearButton.setVisible(showClear);
                
                searchTimer.restart();
            }
        });
        
        // Clear button action
        clearButton.addActionListener(e -> {
            searchField.setText(placeholder);
            searchField.setForeground(ThemeManager.GRAY_400);
            clearButton.setVisible(false);
            searchField.requestFocus();
            
            if (searchListener != null) {
                searchListener.onSearchCleared();
            }
        });
        
        // Set initial state
        searchField.setForeground(ThemeManager.GRAY_400);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 
                ThemeManager.RADIUS_MD, ThemeManager.RADIUS_MD));
        
        // Border
        if (searchField.hasFocus()) {
            g2.setColor(ThemeManager.PRIMARY_BLUE);
            g2.setStroke(new BasicStroke(2));
        } else {
            g2.setColor(ThemeManager.GRAY_300);
            g2.setStroke(new BasicStroke(1));
        }
        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 
                ThemeManager.RADIUS_MD, ThemeManager.RADIUS_MD));
        
        g2.dispose();
    }
    
    // Public API methods
    public String getSearchText() {
        String text = searchField.getText().trim();
        return text.equals(placeholder) ? "" : text;
    }
    
    public void setSearchListener(SearchListener listener) {
        this.searchListener = listener;
    }
    
    public void clear() {
        searchField.setText(placeholder);
        searchField.setForeground(ThemeManager.GRAY_400);
        clearButton.setVisible(false);
        
        if (searchListener != null) {
            searchListener.onSearchCleared();
        }
    }
    
    public void setPlaceholder(String newPlaceholder) {
        boolean wasPlaceholder = searchField.getText().equals(this.placeholder);
        this.placeholder = newPlaceholder;
        if (wasPlaceholder) {
            searchField.setText(newPlaceholder);
        }
    }
    
    public void focus() {
        searchField.requestFocus();
    }
}
