package NES.UI.Debugger.CPUDebugger;

import NES.CPU.CPU;

import javax.swing.*;
import java.awt.*;

public class CyclesPane extends JPanel {

    private final CPU cpu;
    private final JLabel cpu_cycles;

    public CyclesPane(CPU cpu) {
        this.cpu = cpu;
        this.cpu_cycles = new JLabel("0");

        add(new JLabel("Cycles: "));
        add(cpu_cycles);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        cpu_cycles.setText(""+cpu.cycles);
    }
}
