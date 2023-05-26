package NES.UI.Debugger.CPUDebugger;

import NES.CPU.CPU;

import javax.swing.*;
import java.awt.*;

public class CPUMainPane extends JPanel {
    private final CPUButtonPane button_pane;
    public CPUMainPane(CPU cpu, byte[] cpu_memory) {
        setBorder(BorderFactory.createTitledBorder("CPU"));

        JPanel reg_pane = new RegistersPanel(cpu.registers);
        JPanel stack_pane = new StackPanel();
        button_pane = new CPUButtonPane(cpu, this);
        JPanel cycles_pane = new CyclesPane(cpu);
        JPanel num_instr_pane = new NumInstructionsPane(cpu);
        JPanel instr_pane = new InstructionsPane(cpu, cpu_memory);

        JPanel box_pane = new JPanel();
        box_pane.setLayout(new BoxLayout(box_pane, BoxLayout.PAGE_AXIS));
        box_pane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        add(button_pane);
        add(reg_pane);
        add(stack_pane);

        box_pane.add(cycles_pane);
        box_pane.add(num_instr_pane);
        add(box_pane);

        add(instr_pane);
    }

    // Called when we need to update the PPU panel
    public void setRepaintPpuPane(Runnable repaint_ppu_pane_runnable) {
        button_pane.setRepaintPpuPane(repaint_ppu_pane_runnable);
    }
}
