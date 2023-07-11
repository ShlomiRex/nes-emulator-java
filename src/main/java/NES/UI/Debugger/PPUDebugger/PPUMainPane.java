package NES.UI.Debugger.PPUDebugger;

import NES.PPU.PPU;
import NES.UI.Debugger.AssemblyDebugger.AssemblyTextPane;
import NES.UI.Debugger.CPUDebugger.CPUMainPane;
import NES.UI.Debugger.PPUDebugger.Palette.PaletteMemoryPane;
import NES.UI.Debugger.PPUDebugger.Palette.SystemPalettePane;
import NES.UI.Debugger.PPUDebugger.PatternTable.PatternTablesPane;
import NES.UI.Debugger.PPUDebugger.StatusInfo.CyclesPane;
import NES.UI.Debugger.PPUDebugger.StatusInfo.FramePane;
import NES.UI.Debugger.PPUDebugger.StatusInfo.ScanlinePane;
import NES.UI.Debugger.PPUDebugger.StatusInfo.VBlankPane;

import javax.swing.*;
import java.awt.*;

public class PPUMainPane extends JPanel {
    public PPUMainPane(PPU ppu, CPUMainPane cpu_main_pane, AssemblyTextPane assemblyTextPane) {
        setBorder(BorderFactory.createTitledBorder("PPU"));

        // Containers
        JPanel box_pane = new JPanel();
        JPanel top_flow_pane = new JPanel();
        JPanel bottom_flow_pane = new JPanel();
        JPanel status_info_pane = new JPanel();
        JPanel palette_pane = new JPanel();

        // Init containers
        box_pane.setLayout(new BoxLayout(box_pane, BoxLayout.PAGE_AXIS));
        status_info_pane.setLayout(new GridLayout(3, 1));
        status_info_pane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        palette_pane.setLayout(new BoxLayout(palette_pane, BoxLayout.PAGE_AXIS));

        // Main panels
        JPanel button_pane = new PPUButtonPane(ppu, top_flow_pane, cpu_main_pane, assemblyTextPane); // We don't need to update unrelated panels
        JPanel cycles_pane = new CyclesPane(ppu);
        JPanel scanline_pane = new ScanlinePane(ppu);
        JPanel frame_pane = new FramePane(ppu);
        JPanel registers_pane = new PPURegistersPan(ppu.registers);
        JPanel vblank_pane = new VBlankPane(ppu);
        JPanel pattern_tables_pane = new PatternTablesPane(ppu);
        JPanel system_palette_pane = new SystemPalettePane();
        JPanel palette_memory_pane = new PaletteMemoryPane(ppu);

        // Add panels
        status_info_pane.add(cycles_pane);
        status_info_pane.add(scanline_pane);
        status_info_pane.add(frame_pane);

        top_flow_pane.add(button_pane);
        top_flow_pane.add(status_info_pane);
        top_flow_pane.add(registers_pane);
        top_flow_pane.add(vblank_pane);

        palette_pane.add(system_palette_pane);
        palette_pane.add(palette_memory_pane);

        bottom_flow_pane.add(pattern_tables_pane);
        bottom_flow_pane.add(palette_pane);

        box_pane.add(top_flow_pane);
        box_pane.add(bottom_flow_pane);

        add(box_pane);
    }
}
