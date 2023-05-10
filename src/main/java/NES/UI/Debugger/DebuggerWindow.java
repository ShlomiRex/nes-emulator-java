package NES.UI.Debugger;

import NES.NES;
import NES.UI.Debugger.CPUDebugger.*;
import NES.UI.Debugger.PPUDebugger.PatternTablesPane;
import NES.UI.Debugger.PPUDebugger.PatternTilePane;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DebuggerWindow extends JFrame {

    private final NES nes;

    public DebuggerWindow(NES nes, AtomicBoolean next_tick_event) {
        this.nes = nes;

        setTitle("Java NES Emulator - Debugger");
        setSize(1600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main_pane = new JPanel();
        main_pane.setLayout(new BoxLayout(main_pane, BoxLayout.PAGE_AXIS));
        JPanel main_cpu_debugging_pane = new JPanel();
        JPanel main_ppu_debugging_pane = new JPanel();

        JPanel reg_pane = new RegistersPanel(nes.cpu.registers);
        JPanel stack_pane = new StackPanel();
        JPanel button_pane = new ButtonPane(next_tick_event, main_pane);
        JPanel cycles_pane = new CyclesPane(nes.cpu);
        JPanel instr_pane = new InstructionsPane(nes.cpu, nes.cpu_memory);
        JPanel pattern_tables_pane = new PatternTablesPane(nes.ppu);

        // CPU debugging
        main_cpu_debugging_pane.add(button_pane);
        main_cpu_debugging_pane.add(reg_pane);
        main_cpu_debugging_pane.add(stack_pane);
        main_cpu_debugging_pane.add(cycles_pane);
        main_cpu_debugging_pane.add(instr_pane);

        // PPU debugging
        main_ppu_debugging_pane.add(pattern_tables_pane);

        // Main pane
        main_pane.add(main_cpu_debugging_pane);
        main_pane.add(main_ppu_debugging_pane);

        add(main_pane);
        //pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
