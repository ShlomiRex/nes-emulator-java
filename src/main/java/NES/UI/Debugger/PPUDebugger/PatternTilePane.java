package NES.UI.Debugger.PPUDebugger;

import NES.PPU.PPU;

import javax.swing.*;
import java.awt.*;

public class PatternTilePane extends JPanel {
    private final PPU ppu;
    private final byte tile_index;
    private final boolean is_left_pattern_table;

    private final int panel_width, panel_height;


    public PatternTilePane(PPU ppu, byte tile_index, boolean is_left_pattern_table) {
        this.ppu = ppu;
        this.tile_index = tile_index;
        this.is_left_pattern_table = is_left_pattern_table;

        this.panel_width = 36;
        this.panel_height = 36;

        setPreferredSize(new Dimension(panel_width, panel_height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        byte[] tile = ppu.get_pattern_tile(tile_index, is_left_pattern_table);
        //TODO: Paint the tile, should be: |-  (like | pattern and then - pattern)

        byte[][] pixels = ppu.convert_pattern_tile_to_pixel_pattern(tile);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, panel_width, panel_height);

        int pixel_width = panel_width / 8;
        int pixel_height = panel_height / 8;

        g.setColor(Color.WHITE);
        for(int row = 0; row < 8; row ++) {
            for (int col = 0; col < 8; col ++) {
                byte pixel = pixels[row][col];
                if (pixel != 0) {
                    g.fillRect(col * pixel_width, row * pixel_height, pixel_width, pixel_height);
                }
            }
        }
    }
}
