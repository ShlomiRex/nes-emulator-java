package NES.UI.Debugger.CPUDebugger;

import NES.CPU.CPU;
import NES.UI.Debugger.DebuggerWindow;

import javax.swing.*;
import java.awt.*;

public class NumInstructionsPane extends JPanel {
    private final CPU cpu;
    private final JLabel num_instructions;

    public NumInstructionsPane(CPU cpu) {
        this.cpu = cpu;
        this.num_instructions = new JLabel("0");

        add(new JLabel("Instructions: "));
        add(num_instructions);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        num_instructions.setText(DebuggerWindow.DECIMAL_FORMAT.format(cpu.instructions));
    }
}
