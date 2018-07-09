package ch.unifr.marcovr.ResultsAnalyzer.UI;

import ch.unifr.marcovr.ResultsAnalyzer.Signature;
import ch.unifr.marcovr.ResultsAnalyzer.User;

import javax.swing.*;
import java.awt.*;

public class ResultsGUI {
    private JList userList;
    private JList sigList;
    private JPanel contentPanel;
    private GraphPanel graphPanel;
    private JPanel transformPanel;
    private JList transformList;
    private JCheckBox keepEdgesCheckBox;
    private JSpinner kSpinner;
    private JScrollPane transformScrollPane;

    private DefaultListModel<User> userListModel;
    private DefaultListModel<Signature> sigListModel;

    public ResultsGUI() {
        contentPanel.setPreferredSize(new Dimension(600, 400));
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (userList.getSelectedIndex() != -1) {
                    setSigList(userListModel.elementAt(userList.getSelectedIndex()));
                }
            }
        });
        sigList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (sigList.getSelectedIndex() != -1) {
                    Signature item = sigListModel.elementAt(sigList.getSelectedIndex());
                    graphPanel.setGxl(item.gxl);
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
    }

    public void show() {
        JFrame frame = new JFrame("GXL-D25 Transformation Visualizer");
        frame.setContentPane(contentPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 200));
        frame.pack();
        frame.setVisible(true);
    }

    public void addUser(User user) {
        userListModel.addElement(user);
        if (userListModel.size() == 1) {
            userList.setSelectedIndex(0);
        }
    }

    public void selectNone(int user) {
        userList.setSelectedIndex(user);
        transformList.setSelectedIndex(0);
    }

    public void select(int user, int transform, boolean keepEdges, int k) {
        userList.setSelectedIndex(user);
        transformList.setSelectedIndex(transform);
        keepEdgesCheckBox.setSelected(keepEdges);
        kSpinner.setValue(k);
    }

    private void setSigList(User user) {
        int i = sigList.getSelectedIndex();
        sigListModel.removeAllElements();
        user.signatures.forEach(s -> sigListModel.addElement(s));
        sigList.setSelectedIndex(i == -1 ? 0 : i);
    }

    private void createUIComponents() {
        userList = new JList<>(userListModel = new DefaultListModel<>());
        sigList = new JList<>(sigListModel = new DefaultListModel<>());
        kSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
    }
}
