package NES.UI.Debugger.CPUDebugger;

import NES.Bus.Bus;
import NES.NES;
import NES.CPU.CPU;
import NES.UI.Debugger.AssemblyDebugger.AssemblyTextPane;
import NES.UI.Debugger.AssemblyDebugger.AssemnlyMainPane;

import javax.swing.*;
import java.awt.*;

public class CPUMainPane extends JPanel {
    public final CPUButtonPane button_pane;

    public final JPanel stack_pane;

    public CPUMainPane(NES nes, AssemblyTextPane assembly_text_pane) {
        setBorder(BorderFactory.createTitledBorder("CPU"));

        CPU cpu = nes.cpu;
        Bus bus = nes.bus;

        JPanel reg_pane = new RegistersPanel(cpu.registers);
        stack_pane = new StackPanel(cpu, bus);
        button_pane = new CPUButtonPane(cpu, this, assembly_text_pane);
        JPanel cycles_pane = new CyclesPane(cpu);
        JPanel num_instr_pane = new NumInstructionsPane(cpu);

        JPanel box_pane = new JPanel();
        box_pane.setLayout(new BoxLayout(box_pane, BoxLayout.PAGE_AXIS));
        box_pane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        add(button_pane);
        add(reg_pane);
        add(stack_pane);

        box_pane.add(cycles_pane);
        box_pane.add(num_instr_pane);

        add(box_pane);
    }

    // Called when we need to update the PPU panel
    public void setRepaintPpuPane(Runnable repaint_ppu_pane_runnable) {
        button_pane.setRepaintPpuPane(repaint_ppu_pane_runnable);
    }
}
