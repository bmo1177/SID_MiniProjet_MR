package views.components;

import config.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

/**
 * Modern statistics card component with professional design
 * Displays key metrics with real-time data from database
 */
public class ModernStatCard extends JPanel {
    private String title;
    private String value;
    private String trend;
    private Color accentColor;
    private String description;
    private JLabel valueLabel;
    private JLabel titleLabel;
    private JLabel trendLabel;
    private JLabel descriptionLabel;
    
    public ModernStatCard(String title, String value, Color accentColor) {
        this(title, value, accentColor, null, null);
    }
    
    public ModernStatCard(String title, String value, Color accentColor, String trend, String description) {
        this.title = title;
        this.value = value;
        this.accentColor = accentColor;
        this.trend = trend;
        this.description = description;
        
        initializeComponents();
        setupLayout();
        setupStyling();
    }
    
    private void initializeComponents() {
        valueLabel = new JLabel(value);
        titleLabel = new JLabel(title);
        trendLabel = new JLabel(trend != null ? trend : "");
        descriptionLabel = new JLabel(description != null ? description : "");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, ThemeManager.SPACING_MD));
        
        // Header with value and trend
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        valueLabel.setFont(ThemeManager.FONT_HEADING_LARGE);
        valueLabel.setForeground(ThemeManager.GRAY_900);
        headerPanel.add(valueLabel, BorderLayout.WEST);
        
        if (trend != null && !trend.isEmpty()) {
            trendLabel.setFont(ThemeManager.FONT_BODY_SMALL);
            trendLabel.setForeground(getTrendColor());
            trendLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            headerPanel.add(trendLabel, BorderLayout.EAST);
        }
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Footer with title and description
        JPanel footerPanel = new JPanel(new BorderLayout(0, ThemeManager.SPACING_XS));
        footerPanel.setOpaque(false);
        
        titleLabel.setFont(ThemeManager.FONT_BODY_MEDIUM);
        titleLabel.setForeground(ThemeManager.GRAY_600);
        footerPanel.add(titleLabel, BorderLayout.NORTH);
        
        if (description != null && !description.isEmpty()) {
            descriptionLabel.setFont(ThemeManager.FONT_CAPTION);
            descriptionLabel.setForeground(ThemeManager.GRAY_500);
            footerPanel.add(descriptionLabel, BorderLayout.SOUTH);
        }
        
        add(footerPanel, BorderLayout.SOUTH);
        
        // Accent indicator
        JPanel accentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), ThemeManager.RADIUS_SM, ThemeManager.RADIUS_SM);
                g2.dispose();
            }
        };
        accentPanel.setPreferredSize(new Dimension(4, 0));
        add(accentPanel, BorderLayout.WEST);
    }
    
    private void setupStyling() {
        setBackground(ThemeManager.SURFACE_CARD);
        setBorder(BorderFactory.createEmptyBorder(
            ThemeManager.SPACING_LG, 
            ThemeManager.SPACING_LG, 
            ThemeManager.SPACING_LG, 
            ThemeManager.SPACING_MD
        ));
        setPreferredSize(new Dimension(280, 120));
        
        // Add hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setBackground(ThemeManager.GRAY_50);
                repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setBackground(ThemeManager.SURFACE_CARD);
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Shadow
        g2.setColor(ThemeManager.SHADOW_COLOR);
        g2.fill(new RoundRectangle2D.Float(2, 2, getWidth()-2, getHeight()-2, 
                ThemeManager.RADIUS_LG, ThemeManager.RADIUS_LG));
        
        // Background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-2, getHeight()-2, 
                ThemeManager.RADIUS_LG, ThemeManager.RADIUS_LG));
        
        g2.dispose();
    }
    
    private Color getTrendColor() {
        if (trend == null || trend.isEmpty()) return ThemeManager.GRAY_500;
        
        if (trend.startsWith("+") || trend.contains("↑")) {
            return ThemeManager.SUCCESS_GREEN;
        } else if (trend.startsWith("-") || trend.contains("↓")) {
            return ThemeManager.ERROR_RED;
        } else {
            return ThemeManager.GRAY_500;
        }
    }
    
    // Update methods for real-time data
    public void updateValue(String newValue) {
        this.value = newValue;
        valueLabel.setText(newValue);
        repaint();
    }
    
    public void updateValue(int newValue) {
        updateValue(String.valueOf(newValue));
    }
    
    public void updateValue(double newValue, String format) {
        DecimalFormat df = new DecimalFormat(format);
        updateValue(df.format(newValue));
    }
    
    public void updateTrend(String newTrend) {
        this.trend = newTrend;
        trendLabel.setText(newTrend != null ? newTrend : "");
        trendLabel.setForeground(getTrendColor());
        repaint();
    }
    
    public void updateDescription(String newDescription) {
        this.description = newDescription;
        descriptionLabel.setText(newDescription != null ? newDescription : "");
        repaint();
    }
    
    public String getValue() {
        return value;
    }
    
    public String getTitle() {
        return title;
    }
}