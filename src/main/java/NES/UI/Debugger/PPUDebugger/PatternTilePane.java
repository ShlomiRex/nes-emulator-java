package NES.UI.Debugger.PPUDebugger;

import NES.Common;
import NES.PPU.PPU;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PatternTilePane extends JPanel {
    private final PPU ppu;
    private final byte tile_index;
    private final boolean is_left_pattern_table;

    public PatternTilePane(PPU ppu, byte tile_index, boolean is_left_pattern_table) {
        this.ppu = ppu;
        this.tile_index = tile_index;
        this.is_left_pattern_table = is_left_pattern_table;

        setPreferredSize(new Dimension(300, 300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        byte[] tile = ppu.get_pattern_tile(tile_index, is_left_pattern_table);
        //TODO: Paint the tile, should be: |-  (like | pattern and then - pattern)

        byte[][] pixels = ppu.convert_pattern_tile_to_pixel_pattern(tile);

        int scale = 10;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 8 * scale, 8 * scale);

        g.setColor(Color.GREEN);
        for(int row = 0; row < 8; row ++) {
            for (int col = 0; col < 8; col ++) {
                byte pixel = pixels[row][col];
                if (pixel != 0) {
                    g.fillRect(col * scale, row * scale, scale, scale);
                }
            }
        }
    }
}
