package NES.UI.Debugger.PPUDebugger;

import NES.PPU.PPU;
import NES.UI.Debugger.DebuggerUIEvents;
import NES.UI.Debugger.PPUDebugger.Palette.PaletteTablePane;
import NES.UI.Debugger.PPUDebugger.PatternTable.PatternTablesPane;
import NES.UI.Debugger.PPUDebugger.StatusInfo.CyclesPane;
import NES.UI.Debugger.PPUDebugger.StatusInfo.ScanlinePane;

import javax.swing.*;

public class PPUMainPane extends JPanel {
    public PPUMainPane(PPU ppu, DebuggerUIEvents ui_events) {
        setBorder(BorderFactory.createTitledBorder("PPU"));

        JPanel button_pane = new PPUButtonPane(ui_events, this);
        JPanel cycles_pane = new CyclesPane(ppu);
        JPanel scanline_pane = new ScanlinePane(ppu);
        JPanel pattern_tables_pane = new PatternTablesPane(ppu);
        JPanel palette_table_pane = new PaletteTablePane();

        // Containers
        JPanel box_pane = new JPanel();
        JPanel top_flow_pane = new JPanel();
        JPanel bottom_flow_pane = new JPanel();
        box_pane.setLayout(new BoxLayout(box_pane, BoxLayout.PAGE_AXIS));

        top_flow_pane.add(button_pane);
        top_flow_pane.add(cycles_pane);
        top_flow_pane.add(scanline_pane);

        bottom_flow_pane.add(pattern_tables_pane);
        bottom_flow_pane.add(palette_table_pane);

        box_pane.add(top_flow_pane);
        box_pane.add(bottom_flow_pane);

        add(box_pane);
    }
}
