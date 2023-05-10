package NES.UI.Debugger.PPUDebugger;

import NES.PPU.PPU;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PatternTablesPane extends JPanel {

    private final PPU ppu;

    public PatternTablesPane(PPU ppu) {
        this.ppu = ppu;

        setBorder(new TitledBorder("Pattern Tables"));

        add(createLeftPatternTable(true));
        add(createLeftPatternTable(false));
    }

    private JPanel createLeftPatternTable(boolean is_left_pattern_table) {
        // Currently selected tile index
        JPanel tile_index_panel = new JPanel();
        JLabel left_pattern_table_tile_index = new JLabel("Tile:");
        tile_index_panel.add(left_pattern_table_tile_index);

        // Pattern table canvas
        JPanel table_canvas = new JPanel();
        table_canvas.setLayout(new GridLayout(16, 16));
        for(byte row = 0; row < 16; row ++) {
            for (byte col = 0; col < 16; col++) {
                byte tile_index = (byte)(col + row * 16);
                table_canvas.add(new PatternTilePane(ppu, tile_index, is_left_pattern_table, left_pattern_table_tile_index));
            }
        }

        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        main.add(table_canvas, BorderLayout.CENTER);
        main.add(tile_index_panel, BorderLayout.PAGE_END);

        return main;
    }
}
