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

        // Create main panels
        AssemnlyMainPane assembly_pane = new AssemnlyMainPane(nes);
        CPUMainPane cpu_pane = new CPUMainPane(nes, assembly_pane.assembly_text_area);
        PPUMainPane ppu_pane = new PPUMainPane(nes, cpu_pane, assembly_pane.assembly_text_area);


        JPanel left_panel = new JPanel();
        left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.Y_AXIS));

        MainControlsPane main_controls_pane = new MainControlsPane(nes,
                cpu_pane::repaint,
                ppu_pane::repaint,
                cpu_pane.button_pane,
                ppu_pane.button_pane,
                assembly_pane.assembly_text_area);

        assembly_pane.setBorder(new TitledBorder("Assembly"));

        left_panel.add(main_controls_pane);
        left_panel.add(assembly_pane);








        // Create scroll / split panes
        JScrollPane cpuScrollPane = new JScrollPane(cpu_pane);
        JScrollPane ppuScrollPane = new JScrollPane(ppu_pane);
        JSplitPane vert_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cpuScrollPane, ppuScrollPane);

        ppuScrollPane.getVerticalScrollBar().setUnitIncrement(8);



        cpu_pane.setRepaintPpuPane(ppu_pane::repaint);

        JPanel right_panel = new JPanel();
        right_panel.setLayout(new BoxLayout(right_panel, BoxLayout.Y_AXIS));
        right_panel.add(vert_split);

        cpuScrollPane.setMinimumSize(new Dimension(0, 160));












        JSplitPane hori_split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left_panel, right_panel);
        add(hori_split, BorderLayout.CENTER);


        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        setLocationRelativeTo(null);

    }

}
