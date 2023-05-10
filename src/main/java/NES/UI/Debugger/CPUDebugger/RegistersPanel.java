package NES.UI.Debugger.CPUDebugger;

import NES.CPU.Registers.CPURegisters;
import NES.Common;

import javax.swing.*;
import java.awt.*;

public class RegistersPanel extends JPanel {

    private final CPURegisters registers;
    private final JTextField a,x,y,s,pc;
    private final JPanel statusFlagsPanel;

    public RegistersPanel(CPURegisters registers) {
        this.registers = registers;

        // A
        add(new JLabel("A:"));
        a = new JTextField("00");
        a.setEditable(false);
        a.setColumns(2);
        add(a);

        // X
        add(new JLabel("X:"));
        x = new JTextField("00");
        x.setEditable(false);
        x.setColumns(2);
        add(x);

        // Y
        add(new JLabel("Y:"));
        y = new JTextField("00");
        y.setEditable(false);
        y.setColumns(2);
        add(y);

        add(new JLabel("S:"));
        s = new JTextField("00");
        s.setEditable(false);
        s.setColumns(2);
        add(s);

        // PC
        add(new JLabel("PC:"));
        pc = new JTextField("00");
        pc.setEditable(false);
        pc.setColumns(4);
        add(pc);

        // P
        statusFlagsPanel = new StatusFlagsPanel(registers.P);
        add(statusFlagsPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        a.setText(Common.byteToHexString(registers.A, false));
        x.setText(Common.byteToHexString(registers.X, false));
        y.setText(Common.byteToHexString(registers.Y, false));
        s.setText(Common.byteToHexString(registers.S, false));
        pc.setText(Common.shortToHexString(registers.PC, false));
    }
}
