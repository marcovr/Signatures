package ch.unifr.marcovr.ResultsAnalyzer.UI;

import ch.unifr.marcovr.ResultsAnalyzer.EER;
import ch.unifr.marcovr.ResultsAnalyzer.RowNumberTable;
import ch.unifr.marcovr.ResultsAnalyzer.Signature;
import ch.unifr.marcovr.ResultsAnalyzer.User;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

public class ResultsGUI {
    private JList userList;
    private JList sigList;
    private JPanel contentPanel;
    private GraphPanel graphPanel;
    private JList transformList;
    private JCheckBox keepEdgesCheckBox;
    private JSpinner kSpinner;
    private JPanel transformPanel;
    private JScrollPane transformScrollPane;
    private JScrollPane tableScrollPane;
    private JTable dataTable;
    private JProgressBar progressBar;
    private JSplitPane splitPane;
    private JComboBox layoutComboBox;
    private JScrollPane userScrollPane;

    private DefaultListModel<User> userListModel;
    private DefaultListModel<Signature> sigListModel;
    private int pendingSelection = 0;
    private ArrayList<NonEditableModel> dataModels = new ArrayList<>();

    public ResultsGUI() {
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (userList.getSelectedIndex() != -1) {
                    setSigList(userListModel.elementAt(userList.getSelectedIndex()));
                }
                else {
                    setSigList(null);
                }
            }
        });
        sigList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (sigList.getSelectedIndex() != -1) {
                    Signature item = sigListModel.elementAt(sigList.getSelectedIndex());
                    graphPanel.setGxl(item.gxl);
                }
                else {
                    graphPanel.setGxl(null);
                }
            }
        });
        transformList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (transformList.getSelectedIndex() != -1) {
                    graphPanel.setTransform((String)transformList.getSelectedValue());
                }
            }
        });
        keepEdgesCheckBox.addChangeListener(e -> graphPanel.setKeepEdges(keepEdgesCheckBox.isSelected()));
        kSpinner.addChangeListener(e -> graphPanel.setK((int)kSpinner.getValue()));

        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.getTableHeader().setReorderingAllowed(false);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableSelectionListener t = new TableSelectionListener();
        dataTable.getColumnModel().addColumnModelListener(t);
        dataTable.getSelectionModel().addListSelectionListener(t);

        JTable rowTable = new RowNumberTable(dataTable);
        tableScrollPane.setRowHeaderView(rowTable);
        tableScrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());

        dataTable.setDefaultRenderer(Object.class, new StatusColumnCellRenderer());

        layoutComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setMode(layoutComboBox.getSelectedIndex());
            }
        });

        setMode(0);
    }

    public void show() {
        JFrame frame = new JFrame("GXL-D25 Transformation Results Visualizer");
        frame.setContentPane(contentPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 200));
        frame.pack();
        frame.setVisible(true);
    }

    public void setProgress(int value) {
        progressBar.setValue(value);
        if (value == 100) {
            progressBar.setVisible(false);
        }
    }

    public void addUser(User user) {
        userListModel.addElement(user);
        if (pendingSelection >= 0 && userListModel.size() > pendingSelection) {
            userList.setSelectedIndex(pendingSelection);
            pendingSelection = -1;
        }
    }

    public void addData(Object[][] data) {

        String[] header = new String[data[0].length];
        for (int i = 0; i < header.length; i++) {
            switch (i) {
                case 0:
                    header[i] = "original";
                    break;
                case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8:
                    header[i] = "nk " + i;
                    break;
                case 9: case 10: case 11: case 12: case 13: case 14: case 15: case 16:
                    header[i] = "nr " + (i - 8);
                    break;
                case 17: case 18: case 19:
                    header[i] = "sak " + (i - 16);
                    break;
                case 20: case 21: case 22:
                    header[i] = "sar " + (i - 19);
                    break;
                case 23: case 24: case 25:
                    header[i] = "smk " + (i - 22);
                    break;
                default:
                    header[i] = "smr " + (i - 25);
            }
        }

        dataModels.add(new NonEditableModel(data, header));
    }

    private void setMode(int mode) {
        if (mode < 1) {
            tableScrollPane.setVisible(false);
            userScrollPane.setVisible(true);
            splitPane.setEnabled(false);
            splitPane.setResizeWeight(0);
            splitPane.setDividerLocation(splitPane.getLeftComponent().getMinimumSize().width);

            transformList.setEnabled(true);
            keepEdgesCheckBox.setEnabled(true);
            kSpinner.setEnabled(true);
        }
        else {
            tableScrollPane.setVisible(true);
            userScrollPane.setVisible(false);
            splitPane.setEnabled(true);
            splitPane.setResizeWeight(0.5);
            splitPane.setDividerLocation(contentPanel.getWidth() / 2);

            transformList.setEnabled(false);
            keepEdgesCheckBox.setEnabled(false);
            kSpinner.setEnabled(false);

            dataTable.setModel(dataModels.get(mode - 1));
        }
        splitPane.revalidate();
    }

    private void selectNone(int user) {
        if (user < userListModel.size()) {
            userList.setSelectedIndex(user);
            pendingSelection = -1;
        }
        else {
            userList.clearSelection();
            pendingSelection = user;
        }
        transformList.setSelectedIndex(0);
    }

    private void select(int user, int transform, boolean keepEdges, int k) {
        if (user < userListModel.size()) {
            userList.setSelectedIndex(user);
            pendingSelection = -1;
        }
        else {
            userList.clearSelection();
            pendingSelection = user;
        }
        transformList.setSelectedIndex(transform);
        keepEdgesCheckBox.setSelected(keepEdges);
        kSpinner.setValue(k);
    }

    private void setSigList(User user) {
        int i = sigList.getSelectedIndex();
        sigListModel.removeAllElements();
        if (user != null) {
            user.signatures.forEach(s -> sigListModel.addElement(s));
            sigList.setSelectedIndex(i == -1 ? 0 : i);
        }
        else {
            sigList.clearSelection();
        }
        sigList.repaint();
    }

    private void createUIComponents() {
        userList = new JList<>(userListModel = new DefaultListModel<>());
        sigList = new JList<>(sigListModel = new DefaultListModel<>());
        kSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
    }

    private class TableSelectionListener implements ListSelectionListener, TableColumnModelListener {
        private int prevUser = -1;
        private int prevCol = -1;

        private void SelectionChanged() {
            int user = dataTable.getSelectedRow();
            int column = dataTable.getSelectedColumn();

            if (user == prevUser && column == prevCol) {
                return;
            }
            prevUser = user;
            prevCol = column;

            switch (column) {
                case 0:
                    selectNone(user);
                    break;
                case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8:
                    select(user, 1, true, column);
                    break;
                case 9: case 10: case 11: case 12: case 13: case 14: case 15: case 16:
                    select(user, 1, false, column - 8);
                    break;
                case 17: case 18: case 19:
                    select(user, 2, true, column - 16);
                    break;
                case 20: case 21: case 22:
                    select(user, 2, false, column - 19);
                    break;
                case 23: case 24: case 25:
                    select(user, 3, true, column - 22);
                    break;
                default:
                    select(user, 3, false, column - 25);
            }
        }

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

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                SelectionChanged();
            }
        }
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

    private class StatusColumnCellRenderer extends DefaultTableCellRenderer {
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
