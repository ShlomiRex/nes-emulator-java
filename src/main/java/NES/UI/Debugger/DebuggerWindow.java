package NES.UI.Debugger;

import NES.NES;
import NES.UI.Debugger.AssemblyDebugger.AssemnlyMainPane;
import NES.UI.Debugger.CPUDebugger.CPUMainPane;
import NES.UI.Debugger.PPUDebugger.PPUMainPane;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class DebuggerWindow extends JFrame {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");

    public DebuggerWindow(NES nes) {
        setTitle("Java NES Emulator - Debugger");
        setPreferredSize(new Dimension(1200, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set icon
        Image img_icon = new ImageIcon("resources/NES_icon.png").getImage();
        setIconImage(img_icon);

        AssemnlyMainPane assembly_main_pane = new AssemnlyMainPane(nes);
        JSplitPane main_pane = createMainPane(nes, assembly_main_pane); // right to assembly pane

        add(main_pane, BorderLayout.CENTER);

        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private JSplitPane createMainPane(NES nes, AssemnlyMainPane assembly_main_pane) {
        CPUMainPane pane_cpu = new CPUMainPane(nes, assembly_main_pane.assembly_text_area);
        PPUMainPane pane_ppu = new PPUMainPane(nes, pane_cpu, assembly_main_pane.assembly_text_area);

        JScrollPane scrollPane = new JScrollPane(pane_ppu);

        JSplitPane vert_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pane_cpu, scrollPane);
        JSplitPane hori_split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, assembly_main_pane, vert_split);

        pane_cpu.setRepaintPpuPane(pane_ppu::repaint);

        return hori_split;
    }
}
