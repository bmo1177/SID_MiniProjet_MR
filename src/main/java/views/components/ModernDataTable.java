package views.components;

import config.ThemeManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.ArrayList;

/**
 * Modern data table component with professional styling and real-time data
 * Features: Sorting, filtering, pagination, responsive design
 */
public class ModernDataTable extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;
    private JPanel headerPanel;
    private JPanel footerPanel;
    private JLabel rowCountLabel;
    private String[] columnNames;
    private Class<?>[] columnTypes;
    
    public ModernDataTable(String[] columns, Class<?>[] types) {
        this.columnNames = columns;
        this.columnTypes = types;
        initializeComponents();
        setupLayout();
        setupStyling();
    }
    
    private void initializeComponents() {
        // Create table model
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnTypes != null && columnIndex < columnTypes.length) {
                    return columnTypes[columnIndex];
                }
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only by default
            }
        };
        
        // Create table
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                
                // Alternating row colors
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : ThemeManager.GRAY_50);
                } else {
                    comp.setBackground(ThemeManager.PRIMARY_BLUE_LIGHT);
                    comp.setForeground(Color.WHITE);
                }
                
                // Status column coloring
                if (getColumnClass(column) == String.class && getValueAt(row, column) != null) {
                    String value = getValueAt(row, column).toString();
                    if (isStatusColumn(getColumnName(column))) {
                        comp.setForeground(ThemeManager.getStatusColor(value));
                        if (isRowSelected(row)) {
                            comp.setForeground(Color.WHITE);
                        }
                    }
                }
                
                // Number formatting and coloring
                if (getColumnClass(column) == Double.class || getColumnClass(column) == Float.class) {
                    Object val = getValueAt(row, column);
                    if (val instanceof Number) {
                        double numValue = ((Number) val).doubleValue();
                        if (!isRowSelected(row)) {
                            comp.setForeground(ThemeManager.getGradeColor(numValue));
                        }
                    }
                }
                
                return comp;
            }
        };
        
        // Create scroll pane
        scrollPane = new JScrollPane(table) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 
                        ThemeManager.RADIUS_LG, ThemeManager.RADIUS_LG));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        // Header panel
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Footer panel with row count
        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        rowCountLabel = new JLabel();
        rowCountLabel.setFont(ThemeManager.FONT_CAPTION);
        rowCountLabel.setForeground(ThemeManager.GRAY_500);
        footerPanel.add(rowCountLabel, BorderLayout.WEST);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, ThemeManager.SPACING_MD));
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private void setupStyling() {
        setOpaque(false);
        
        // Table styling
        table.setFont(ThemeManager.FONT_BODY_MEDIUM);
        table.setRowHeight(48);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new ModernHeaderRenderer());
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        
        // Scroll pane styling
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        
        // Enable sorting
        table.setAutoCreateRowSorter(true);
        
        updateRowCount();
    }
    
    /**
     * Add a header component (like search bar or filters)
     */
    public void addHeaderComponent(JComponent component) {
        headerPanel.add(component, BorderLayout.CENTER);
        revalidate();
    }
    
    /**
     * Add a footer component (like pagination)
     */
    public void addFooterComponent(JComponent component) {
        footerPanel.add(component, BorderLayout.EAST);
        revalidate();
    }
    
    /**
     * Add a single row of data
     */
    public void addRow(Object[] rowData) {
        model.addRow(rowData);
        updateRowCount();
    }
    
    /**
     * Add multiple rows of data
     */
    public void addRows(List<Object[]> rowsData) {
        for (Object[] row : rowsData) {
            model.addRow(row);
        }
        updateRowCount();
    }
    
    /**
     * Clear all data
     */
    public void clearData() {
        model.setRowCount(0);
        updateRowCount();
    }
    
    /**
     * Update data with new dataset
     */
    public void updateData(List<Object[]> newData) {
        clearData();
        addRows(newData);
    }
    
    /**
     * Get selected row index
     */
    public int getSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            return table.convertRowIndexToModel(selectedRow);
        }
        return -1;
    }
    
    /**
     * Get selected row data
     */
    public Object[] getSelectedRowData() {
        int selectedRow = getSelectedRow();
        if (selectedRow != -1) {
            Object[] rowData = new Object[model.getColumnCount()];
            for (int i = 0; i < model.getColumnCount(); i++) {
                rowData[i] = model.getValueAt(selectedRow, i);
            }
            return rowData;
        }
        return null;
    }
    
    /**
     * Filter table data based on search term
     */
    public void filterData(String searchTerm) {
        TableRowSorter<DefaultTableModel> sorter = 
            (TableRowSorter<DefaultTableModel>) table.getRowSorter();
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm));
        }
        updateRowCount();
    }
    
    /**
     * Export table data to CSV
     */
    public String exportToCsv() {
        StringBuilder csv = new StringBuilder();
        
        // Headers
        for (int i = 0; i < model.getColumnCount(); i++) {
            csv.append("\"").append(model.getColumnName(i)).append("\"");
            if (i < model.getColumnCount() - 1) csv.append(",");
        }
        csv.append("\n");
        
        // Data
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                Object value = table.getValueAt(row, col);
                csv.append("\"").append(value != null ? value.toString() : "").append("\"");
                if (col < table.getColumnCount() - 1) csv.append(",");
            }
            csv.append("\n");
        }
        
        return csv.toString();
    }
    
    private void updateRowCount() {
        int totalRows = model.getRowCount();
        int visibleRows = table.getRowCount();
        
        if (totalRows == visibleRows) {
            rowCountLabel.setText(String.format("%d enregistrements", totalRows));
        } else {
            rowCountLabel.setText(String.format("%d sur %d enregistrements", visibleRows, totalRows));
        }
    }
    
    private boolean isStatusColumn(String columnName) {
        String lower = columnName.toLowerCase();
        return lower.contains("statut") || lower.contains("état") || 
               lower.contains("status") || lower.contains("etat");
    }
    
    public JTable getTable() {
        return table;
    }
    
    public DefaultTableModel getModel() {
        return model;
    }
    
    /**
     * Modern table header renderer
     */
    private static class ModernHeaderRenderer extends DefaultTableCellRenderer {
        public ModernHeaderRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            setBackground(ThemeManager.GRAY_50);
            setForeground(ThemeManager.GRAY_700);
            setFont(ThemeManager.FONT_BODY_MEDIUM.deriveFont(Font.BOLD));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.GRAY_200),
                BorderFactory.createEmptyBorder(ThemeManager.SPACING_MD, ThemeManager.SPACING_MD, 
                                              ThemeManager.SPACING_MD, ThemeManager.SPACING_MD)
            ));
            
            return this;
        }
    }
}