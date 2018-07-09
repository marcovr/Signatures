package ch.unifr.marcovr.ResultsAnalyzer.UI;

import ch.unifr.marcovr.ResultsAnalyzer.EER;
import ch.unifr.marcovr.ResultsAnalyzer.RowNumberTable;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TableGUI {
    private JTable dataTable;
    private JPanel contentPanel;
    private JScrollPane tableScrollPane;

    private ResultsGUI gui;

    public TableGUI(ResultsGUI gui) {
        this.gui = gui;

        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.getTableHeader().setReorderingAllowed(false);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dataTable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
            @Override
            public void columnAdded(TableColumnModelEvent e) {}

            @Override
            public void columnRemoved(TableColumnModelEvent e) {}

            @Override
            public void columnMoved(TableColumnModelEvent e) {}

            @Override
            public void columnMarginChanged(ChangeEvent e) {}

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    SelectionChanged();
                }
            }
        });

        dataTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SelectionChanged();
            }
        });

        JTable rowTable = new RowNumberTable(dataTable);
        tableScrollPane.setRowHeaderView(rowTable);
        tableScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());

        dataTable.setDefaultRenderer(Object.class, new StatusColumnCellRenderer());
    }

    private void SelectionChanged() {
        int user = dataTable.getSelectedRow();
        int column = dataTable.getSelectedColumn();

        switch (column) {
            case 0:
                gui.selectNone(user);
                break;
            case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8:
                gui.select(user, 1, true, column);
                break;
            case 9: case 10: case 11: case 12: case 13: case 14: case 15: case 16:
                gui.select(user, 1, false, column - 8);
                break;
            case 17: case 18: case 19:
                gui.select(user, 2, true, column - 16);
                break;
            case 20: case 21: case 22:
                gui.select(user, 2, false, column - 19);
                break;
            case 23: case 24: case 25:
                gui.select(user, 3, true, column - 22);
                break;
            default:
                gui.select(user, 3, false, column - 25);
        }
    }

    public void setData(Object[][] data, String[] header) {
        dataTable.setModel(new NonEditableModel(data, header));
    }

    public void show() {
        JFrame frame = new JFrame("EER Data Table");
        frame.setContentPane(contentPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 200));
        frame.pack();
        frame.setVisible(true);
    }

    private class NonEditableModel extends DefaultTableModel {
        NonEditableModel(Object[][] data, String[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
        private final Color[] colors = new Color[] {Color.GREEN, Color.WHITE, Color.ORANGE};

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            if (isSelected) {
                Font f = label.getFont();
                label.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
            }

            if (hasFocus) {
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }

            label.setForeground(Color.BLACK);
            label.setBackground(colors[((EER) value).status + 1]);
            return label;
        }
    }
}
