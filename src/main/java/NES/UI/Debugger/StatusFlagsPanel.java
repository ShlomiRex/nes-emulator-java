package NES.UI.Debugger;

import NES.CPU.Registers.StatusFlags;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class StatusFlagsPanel extends JPanel {

    private final StatusFlags statusFlags;
    private final JCheckBox n,v,u,b,d,i,z,c;

    public StatusFlagsPanel(StatusFlags statusFlags) {
        this.statusFlags = statusFlags;

        setBorder(new TitledBorder("Status Flags"));

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

        c.setSelected(statusFlags.getCarry());
        z.setSelected(statusFlags.getZero());
        i.setSelected(statusFlags.getInterruptDisable());
        System.out.println(statusFlags.getInterruptDisable());
        d.setSelected(statusFlags.getDecimal());

        // Both bits are not used really
        b.setSelected(statusFlags.getB());
        u.setSelected(statusFlags.getUnused());

        v.setSelected(statusFlags.getOverflow());
        n.setSelected(statusFlags.getNegative());
    }
}
