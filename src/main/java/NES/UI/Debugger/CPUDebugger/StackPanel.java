package NES.UI.Debugger.CPUDebugger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class StackPanel extends JPanel {
    public StackPanel(Integer stack_pointer, Byte[] ) {
        setBorder(new TitledBorder("Stack"));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        String[] labels = {};
        JList my_list = new JList(labels);
        my_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        my_list.setVisibleRowCount(5);
        my_list.setFixedCellWidth(50);

        JScrollPane scrollPane = new JScrollPane(my_list);

        add(new JLabel("$1FF"));
        add(scrollPane);
        add(new JLabel("$100"));
    }
}
