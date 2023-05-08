package NES.UI.Window;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class DebuggerWin extends JFrame {
    public DebuggerWin() {
        setTitle("6502 Debugger");
        //setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel reg_pane = init_registers_pane();
        JPanel stack_pane = init_stack_pane();

        JPanel main_pane = new JPanel();
        main_pane.add(reg_pane);
        main_pane.add(stack_pane);
        add(main_pane);

        pack();
        setVisible(true);
    }

    private JPanel init_registers_pane() {
        JPanel reg_panel = new JPanel();

        // A
        reg_panel.add(new JLabel("A:"));
        JTextField a = new JTextField("00");
        a.setEditable(false);
        a.setColumns(2);
        reg_panel.add(a);

        // X
        reg_panel.add(new JLabel("X:"));
        JTextField x = new JTextField("00");
        x.setEditable(false);
        x.setColumns(2);
        reg_panel.add(x);

        // Y
        reg_panel.add(new JLabel("Y:"));
        JTextField y = new JTextField("00");
        y.setEditable(false);
        y.setColumns(2);
        reg_panel.add(y);

        // P
        JPanel status_flags_panel = new JPanel();
        status_flags_panel.setBorder(new TitledBorder("Status Flags"));

        JCheckBox p_n = new JCheckBox("N");
        p_n.setEnabled(false);

        JCheckBox p_v = new JCheckBox("V");
        p_v.setEnabled(false);

        JCheckBox p_u = new JCheckBox("U");
        p_u.setEnabled(false);

        JCheckBox p_b = new JCheckBox("B");
        p_b.setEnabled(false);

        JCheckBox p_d = new JCheckBox("D");
        p_d.setEnabled(false);

        JCheckBox p_i = new JCheckBox("I");
        p_i.setEnabled(false);

        JCheckBox p_z = new JCheckBox("Z");
        p_z.setEnabled(false);

        JCheckBox p_c = new JCheckBox("C");
        p_c.setEnabled(false);

        status_flags_panel.add(p_n);
        status_flags_panel.add(p_v);
        status_flags_panel.add(p_u);
        status_flags_panel.add(p_b);
        status_flags_panel.add(p_d);
        status_flags_panel.add(p_i);
        status_flags_panel.add(p_z);
        status_flags_panel.add(p_c);

        reg_panel.add(status_flags_panel);
        return reg_panel;
    }

    private JPanel init_stack_pane() {
        JPanel stack_pane = new JPanel();
        stack_pane.setBorder(new TitledBorder("Stack"));

        DefaultListModel defaultListModel = new DefaultListModel();
        String elements[] = {"FF", "FF"};
        JList stack_elements = new JList(elements);

        stack_pane.add(stack_elements);
        stack_elements.setFixedCellWidth(50);

        return stack_pane;
    }
}
