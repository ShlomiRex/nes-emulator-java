package NES.UI.Debugger;

import NES.NES;
import NES.UI.Debugger.AssemblyDebugger.AssemblyTextPane;
import NES.UI.Debugger.AssemblyDebugger.AssemnlyMainPane;
import NES.UI.Debugger.CPUDebugger.CPUMainPane;
import NES.UI.Debugger.PPUDebugger.PPUMainPane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class DebuggerWindow extends JFrame {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");

    public DebuggerWindow(NES nes) {
        setTitle("Java NES Emulator - Debugger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set icon
        Image img_icon = new ImageIcon("resources/NES_icon.png").getImage();
        setIconImage(img_icon);


        AssemnlyMainPane assembly_main_pane = new AssemnlyMainPane(nes);
        JPanel left_panel = createLeftPane(assembly_main_pane);
        JPanel right_panel = createRightPane(nes, assembly_main_pane.assembly_text_area);

        JSplitPane hori_split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left_panel, right_panel);
        add(hori_split, BorderLayout.CENTER);


        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setLocationRelativeTo(null);

    }

    private JPanel createLeftPane(AssemnlyMainPane assembly_main_pane) {
        JPanel left_panel = new JPanel();
        left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.Y_AXIS));

        MainControlsPane main_controls_pane = new MainControlsPane();

        assembly_main_pane.setBorder(new TitledBorder("Assembly"));

        left_panel.add(main_controls_pane);
        left_panel.add(assembly_main_pane);

        return left_panel;
    }

    private JPanel createRightPane(NES nes, AssemblyTextPane text_pane) {
        CPUMainPane pane_cpu = new CPUMainPane(nes, text_pane);
        PPUMainPane pane_ppu = new PPUMainPane(nes, pane_cpu, text_pane);

        JScrollPane cpuScrollPane = new JScrollPane(pane_cpu);
        JScrollPane ppuScrollPane = new JScrollPane(pane_ppu);

        ppuScrollPane.getVerticalScrollBar().setUnitIncrement(8);

        JSplitPane vert_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cpuScrollPane, ppuScrollPane);

        pane_cpu.setRepaintPpuPane(pane_ppu::repaint);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(vert_split);

        cpuScrollPane.setMinimumSize(new Dimension(0, 160));

        return wrapper;
    }
}
