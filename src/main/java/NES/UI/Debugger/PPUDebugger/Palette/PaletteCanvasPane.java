package NES.UI.Debugger.PPUDebugger.Palette;

import NES.PPU.PPU;
import NES.UI.Debugger.PPUDebugger.PatternTable.PatternTilePane;

import javax.swing.*;
import java.awt.*;

public class PaletteCanvasPane extends JPanel {

    private PatternTilePane[][] tiles = new PatternTilePane[16][16];

    public PaletteCanvasPane(PPU ppu, int palette_index, boolean is_left_pattern_table, JLabel lbl_tile_index) {
        super(new GridLayout(16, 16));

        for(byte row = 0; row < 16; row ++) {
            for (byte col = 0; col < 16; col++) {
                byte tile_index = (byte)(col + row * 16);

                PatternTilePane tile = new PatternTilePane(
                        ppu, tile_index, palette_index, is_left_pattern_table, lbl_tile_index);

                tiles[row][col] = tile;
                add(tile);
            }
        }
    }


    public void changePalette(int paletteIndex) {
        for (byte row = 0; row < 16; row++) {
            for (byte col = 0; col < 16; col++) {
                tiles[row][col].set_palette(paletteIndex);
            }
        }
    }
}
