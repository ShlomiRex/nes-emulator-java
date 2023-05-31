package NES.UI.Debugger.CPUDebugger;

import NES.CPU.Registers.CPURegisters;
import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RegistersPanel extends JPanel implements PropertyChangeListener {

    private final Logger logger = LoggerFactory.getLogger(RegistersPanel.class);
    private final CPURegisters registers;
    private final JTextField a,x,y,s,pc;
    private final JPanel statusFlagsPanel;

    public RegistersPanel(CPURegisters registers) {
        this.registers = registers;
        this.registers.addChangeListener(this);

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
        statusFlagsPanel = new StatusFlagsPanel(registers.getP());
        add(statusFlagsPanel);

        // Start with initial values
        a.setText(Common.byteToHex(registers.getA(), false));
        x.setText(Common.byteToHex(registers.getX(), false));
        y.setText(Common.byteToHex(registers.getY(), false));
        s.setText(Common.byteToHex(registers.getS(), false));
        pc.setText(Common.shortToHex(registers.getPC(), false));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        logger.debug("Property change: " + evt.getPropertyName());
        if (evt.getPropertyName().equals("A")) {
            a.setText(Common.byteToHex((byte) evt.getNewValue(), false));
        } else if (evt.getPropertyName().equals("X")) {
            x.setText(Common.byteToHex((byte) evt.getNewValue(), false));
        } else if (evt.getPropertyName().equals("Y")) {
            y.setText(Common.byteToHex((byte) evt.getNewValue(), false));
        } else if (evt.getPropertyName().equals("S")) {
            s.setText(Common.byteToHex((byte) evt.getNewValue(), false));
        } else if (evt.getPropertyName().equals("PC")) {
            pc.setText(Common.shortToHex((short) evt.getNewValue(), false));
        } else {
            throw new RuntimeException("Unknown property: " + evt.getPropertyName());
        }
//        else if (evt.getPropertyName().equals("P")) {
//            statusFlagsPanel.repaint();
//        }
    }
}
