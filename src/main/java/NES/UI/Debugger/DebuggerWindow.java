package NES.UI.Debugger;

import NES.NES;
import NES.UI.Debugger.AssemblyDebugger.AssemnlyMainPane;
import NES.UI.Debugger.CPUDebugger.CPUMainPane;
import NES.UI.Debugger.PPUDebugger.PPUMainPane;

import javax.swing.*;
import java.awt.*;

public class DebuggerWindow extends JFrame {

    public DebuggerWindow(NES nes) {
        setTitle("Java NES Emulator - Debugger");
        setPreferredSize(new Dimension(1200, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set icon
        Image img_icon = new ImageIcon("resources/NES_icon.png").getImage();
        setIconImage(img_icon);

        JPanel main_pane = new JPanel();

        main_pane.setLayout(new BoxLayout(main_pane, BoxLayout.PAGE_AXIS));

        AssemnlyMainPane assembly_main_pane = new AssemnlyMainPane(nes.cpu, nes.cpu_memory);
        CPUMainPane main_cpu_debugging_pane = new CPUMainPane(nes.cpu, assembly_main_pane.assembly_text_area);
        JPanel main_ppu_debugging_pane = new PPUMainPane(nes, main_cpu_debugging_pane, assembly_main_pane.assembly_text_area);

        JScrollPane cpu_scroll_pane = new JScrollPane(main_cpu_debugging_pane);
        JScrollPane ppu_scroll_pane = new JScrollPane(main_ppu_debugging_pane);

        Runnable repaint_ppu_pane = main_ppu_debugging_pane::repaint;

        main_cpu_debugging_pane.setRepaintPpuPane(repaint_ppu_pane);

        main_pane.add(cpu_scroll_pane);
        main_pane.add(ppu_scroll_pane);

        add(assembly_main_pane, BorderLayout.LINE_START);
        add(main_pane, BorderLayout.CENTER);

        //pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);


        setVisible(true);
        setLocationRelativeTo(null);
    }

    public Runnable getUpdateRunnable() {
        return this::repaint;
    }
}
