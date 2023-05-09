package NES.UI.Debugger;

import NES.NES;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DebuggerWindow extends JFrame {

    private final NES nes;

    public DebuggerWindow(NES nes, AtomicBoolean next_tick_event) {
        this.nes = nes;

        setTitle("6502 Debugger");
        //setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main_pane = new JPanel();

        JPanel reg_pane = new RegistersPanel(nes.cpu.registers);
        JPanel stack_pane = new StackPanel();
        JPanel button_pane = new ButtonPane(next_tick_event, main_pane);
        JPanel cycles_pane = new CyclesPane(nes.cpu);
        JPanel instr_pane = new InstructionsPane(nes.cpu, nes.cpu_memory);

        main_pane.add(button_pane);
        main_pane.add(reg_pane);
        main_pane.add(stack_pane);
        main_pane.add(cycles_pane);
        main_pane.add(instr_pane);

        add(main_pane);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
