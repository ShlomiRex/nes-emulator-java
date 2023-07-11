package NES.UI.Debugger.CPUDebugger;

import NES.CPU.Registers.CPURegisters;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class StatusFlagsPanel extends JPanel {

    private final CPURegisters cpuRegisters;
    private final JCheckBox n,v,u,b,d,i,z,c;

    public StatusFlagsPanel(CPURegisters cpuRegisters) {
        this.cpuRegisters = cpuRegisters;

        setBorder(new TitledBorder("Status Flags"));
        setLayout(new GridLayout(2, 4));

        c = new JCheckBox("C");
        c.setEnabled(false);

        z = new JCheckBox("Z");
        z.setEnabled(false);

        i = new JCheckBox("I");
        i.setEnabled(false);

        d = new JCheckBox("D");
        d.setEnabled(false);

        b = new JCheckBox("B");
        b.setEnabled(false);

        u = new JCheckBox("U");
        u.setEnabled(false);

        v = new JCheckBox("V");
        v.setEnabled(false);

        n = new JCheckBox("N");
        n.setEnabled(false);

        // Swap order so its like binary right to left
        add(n);
        add(v);
        add(u);
        add(b);
        add(d);
        add(i);
        add(z);
        add(c);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        c.setSelected(cpuRegisters.getCarry());
        z.setSelected(cpuRegisters.getZero());
        i.setSelected(cpuRegisters.getInterruptDisable());
        d.setSelected(cpuRegisters.getDecimal());

        // Both bits are not used really
        b.setSelected(cpuRegisters.getBreak());
        u.setSelected(cpuRegisters.getUnused());

        v.setSelected(cpuRegisters.getOverflow());
        n.setSelected(cpuRegisters.getNegative());
    }
}
