package NES.UI.Debugger.CPUDebugger;

import NES.CPU.Registers.CPURegisters;
import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RegistersPanel extends JPanel {
    private final CPURegisters registers;
    private final JTextField a,x,y,s,pc;

    public RegistersPanel(CPURegisters registers) {
        this.registers = registers;

        JPanel flow_pane1 = new JPanel();
        JPanel flow_pane2 = new JPanel();
        JPanel box_pane = new JPanel();

        box_pane.setLayout(new BoxLayout(box_pane, BoxLayout.PAGE_AXIS));
        box_pane.setBorder(BorderFactory.createTitledBorder("Registers"));

        a = new JTextField("00");
        x = new JTextField("00");
        y = new JTextField("00");
        s = new JTextField("00");
        pc = new JTextField("00");

        flow_pane1.add(new JLabel("A:"));
        flow_pane1.add(a);
        flow_pane1.add(new JLabel("X:"));
        flow_pane1.add(x);
        flow_pane1.add(new JLabel("Y:"));
        flow_pane1.add(y);
        flow_pane2.add(new JLabel("S:"));
        flow_pane2.add(s);
        flow_pane2.add(new JLabel("PC:"));
        flow_pane2.add(pc);

        box_pane.add(flow_pane1);
        box_pane.add(flow_pane2);
        add(box_pane);

        // A
        a.setEditable(false);
        a.setColumns(2);

        // X
        x.setEditable(false);
        x.setColumns(2);

        // Y
        y.setEditable(false);
        y.setColumns(2);

        // S
        s.setEditable(false);
        s.setColumns(2);

        // PC
        pc.setEditable(false);
        pc.setColumns(4);

        // P
        JPanel statusFlagsPanel = new StatusFlagsPanel(registers.getP());
        add(statusFlagsPanel);

        // Start with initial values
        a.setText(Common.byteToHex(registers.getA(), false));
        x.setText(Common.byteToHex(registers.getX(), false));
        y.setText(Common.byteToHex(registers.getY(), false));
        s.setText(Common.byteToHex(registers.getS(), false));
        pc.setText(Common.shortToHex(registers.getPC(), false));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        a.setText(Common.byteToHex(registers.getA(), false));
        x.setText(Common.byteToHex(registers.getX(), false));
        y.setText(Common.byteToHex(registers.getY(), false));
        s.setText(Common.byteToHex(registers.getS(), false));
        pc.setText(Common.shortToHex(registers.getPC(), false));
    }
}
