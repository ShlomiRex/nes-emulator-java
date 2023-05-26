package NES.UI.Debugger;

import NES.NES;
import NES.UI.Debugger.AssemblyDebugger.AssemnlyMainPane;
import NES.UI.Debugger.CPUDebugger.CPUMainPane;
import NES.UI.Debugger.PPUDebugger.PPUMainPane;

import javax.swing.*;
import java.awt.*;

public class DebuggerWindow extends JFrame {

    private final NES nes;

    public DebuggerWindow(NES nes) {
        this.nes = nes;

        setTitle("Java NES Emulator - Debugger");
        setSize(1600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set icon
        Image img_icon = new ImageIcon("resources/NES_icon.png").getImage();
        setIconImage(img_icon);

        JPanel main_pane = new JPanel();

        main_pane.setLayout(new BoxLayout(main_pane, BoxLayout.PAGE_AXIS));

        CPUMainPane main_cpu_debugging_pane = new CPUMainPane(nes.cpu, nes.cpu_memory);
        JPanel main_ppu_debugging_pane = new PPUMainPane(nes.ppu);
        JPanel assembly_main_pane = new AssemnlyMainPane(nes.cpu.registers, nes.cpu_memory);

        Runnable repaint_ppu_pane = main_ppu_debugging_pane::repaint;

        main_cpu_debugging_pane.setRepaintPpuPane(repaint_ppu_pane);

        main_pane.add(main_cpu_debugging_pane);
        main_pane.add(main_ppu_debugging_pane);

        add(assembly_main_pane, BorderLayout.LINE_START);
        add(main_pane, BorderLayout.CENTER);
        //pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
