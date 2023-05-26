package NES.UI.Debugger;

import NES.NES;
import NES.UI.Debugger.CPUDebugger.CPUMainPane;
import NES.UI.Debugger.PPUDebugger.PPUMainPane;

import javax.swing.*;

public class DebuggerWindow extends JFrame {

    private final NES nes;

    public DebuggerWindow(NES nes) {
        this.nes = nes;

        setTitle("Java NES Emulator - Debugger");
        setSize(1600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main_pane = new JPanel();

        main_pane.setLayout(new BoxLayout(main_pane, BoxLayout.PAGE_AXIS));

        JPanel main_cpu_debugging_pane = new CPUMainPane(nes.cpu, nes.cpu_memory);
        JPanel main_ppu_debugging_pane = new PPUMainPane(nes.ppu);

        main_pane.add(main_cpu_debugging_pane);
        main_pane.add(main_ppu_debugging_pane);

        add(main_pane);
        //pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
