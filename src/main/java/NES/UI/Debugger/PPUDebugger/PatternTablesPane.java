package NES.UI.Debugger.PPUDebugger;

import NES.PPU.PPU;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PatternTablesPane extends JPanel {

    private final PPU ppu;
    private final JPanel laft_pattern_table;
    private final JLabel left_pattern_table_tile_index;

    public PatternTablesPane(PPU ppu) {
        this.ppu = ppu;

        setBorder(new TitledBorder("Pattern Tables"));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // Shows the current selected tile
        JPanel left_info_pane = new JPanel();
        left_pattern_table_tile_index = new JLabel("Tile:");
        left_info_pane.add(left_pattern_table_tile_index);

        // The left pattern table canvas
        laft_pattern_table = new JPanel();
        laft_pattern_table.setLayout(new GridLayout(16, 16));
        for(byte row = 0; row < 16; row ++) {
            for (byte col = 0; col < 16; col++) {
                byte tile_index = (byte)(col + row * 16);
                laft_pattern_table.add(new PatternTilePane(ppu, tile_index, true, left_pattern_table_tile_index));
            }
        }

        add(laft_pattern_table);
        add(left_info_pane);
    }
}
