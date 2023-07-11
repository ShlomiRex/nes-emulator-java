package NES.UI.Debugger.CPUDebugger;

import NES.CPU.Registers.CPURegisters;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import static NES.CPU.Registers.Flags.*;

public class StatusFlagsPanel extends JPanel {

    private final CPURegisters cpuRegisters;
    private final JCheckBox n,v,u,b,d,i,z,c;

    public StatusFlagsPanel(CPURegisters cpuRegisters) {
        this.cpuRegisters = cpuRegisters;

        setBorder(new TitledBorder("Status Flags"));
        setLayout(new GridLayout(2, 4));

        c = new JCheckBox("C");
        c.setEnabled(false);
        c.setToolTipText("Carry bit flag");

        z = new JCheckBox("Z");
        z.setEnabled(false);
        z.setToolTipText("Zero bit flag");

        i = new JCheckBox("I");
        i.setEnabled(false);
        i.setToolTipText("Interrupt disable bit flag");

        d = new JCheckBox("D");
        d.setEnabled(false);
        d.setToolTipText("Decimal mode bit flag");

        b = new JCheckBox("B");
        b.setEnabled(false);
        b.setToolTipText("Break bit flag");

        u = new JCheckBox("U");
        u.setEnabled(false);
        u.setToolTipText("Unused bit flag");

        v = new JCheckBox("V");
        v.setEnabled(false);
        v.setToolTipText("Overflow bit flag");

        n = new JCheckBox("N");
        n.setEnabled(false);
        n.setToolTipText("Negative bit flag");

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

        c.setSelected(cpuRegisters.getFlag(CARRY));
        z.setSelected(cpuRegisters.getFlag(ZERO));
        i.setSelected(cpuRegisters.getFlag(INTERRUPT));
        d.setSelected(cpuRegisters.getFlag(DECIMAL));

        // Both bits are not used really
        b.setSelected(cpuRegisters.getFlag(BREAK));
        u.setSelected(cpuRegisters.getFlag(UNUSED));

        v.setSelected(cpuRegisters.getFlag(OVERFLOW));
        n.setSelected(cpuRegisters.getFlag(NEGATIVE));
    }
}
