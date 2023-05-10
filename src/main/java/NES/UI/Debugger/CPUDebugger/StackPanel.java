package NES.UI.Debugger.CPUDebugger;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class StackPanel extends JPanel {
    public StackPanel() {
        setBorder(new TitledBorder("Stack"));

        DefaultListModel<String> defaultListModel = new DefaultListModel<String>();

        defaultListModel.addElement("FF");
        defaultListModel.addElement("FF");

        JList<String> stack_elements = new JList<String>(defaultListModel);

        stack_elements.setFixedCellWidth(50);
        stack_elements.setVisibleRowCount(2);

        add(stack_elements);
    }
}
