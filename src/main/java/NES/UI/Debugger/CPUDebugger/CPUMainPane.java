package NES.UI.Debugger.CPUDebugger;

import NES.CPU.CPU;

import javax.swing.*;

public class CPUMainPane extends JPanel {
    public CPUMainPane(CPU cpu, byte[] cpu_memory) {
        setBorder(BorderFactory.createTitledBorder("CPU"));

        JPanel reg_pane = new RegistersPanel(cpu.registers);
        JPanel stack_pane = new StackPanel();
        JPanel button_pane = new CPUButtonPane(cpu, this);
        JPanel cycles_pane = new CyclesPane(cpu);
        JPanel instr_pane = new InstructionsPane(cpu, cpu_memory);

        add(button_pane);
        add(reg_pane);
        add(stack_pane);
        add(cycles_pane);
        add(instr_pane);
    }
}