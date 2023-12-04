package NES.UI.Debugger.PPUDebugger.Palette;

import NES.Common;
import NES.PPU.PPU;
import NES.UI.Debugger.PPUDebugger.PatternTable.PatternTilePane;
import NES.UI.Debugger.PPUDebugger.PatternTable.PatternTileTooltip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PaletteCanvasPane extends JPanel {

    private CustomPatternTilePane[][] tiles = new CustomPatternTilePane[16][16];

    public PaletteCanvasPane(PPU ppu, int palette_index, boolean is_left_pattern_table, JLabel lbl_tile_index) {
        super(new GridLayout(16, 16));

        for(byte row = 0; row < 16; row ++) {
            for (byte col = 0; col < 16; col++) {
                byte tile_index = (byte)(col + row * 16);

                CustomPatternTilePane tile = new CustomPatternTilePane(
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

    class CustomPatternTilePane extends PatternTilePane {
        public CustomPatternTilePane(PPU ppu, byte tile_index, int palette_index, boolean is_left_pattern_table, JLabel selected_tile_label) {
            super(ppu, tile_index, palette_index, is_left_pattern_table);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                    selected_tile_label.setText("Tile: $" + Common.byteToHex(tile_index, false));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBorder(BorderFactory.createEmptyBorder());
                    selected_tile_label.setText("Tile:");
                }
            });

            setToolTipText("");
        }


        @Override
        public JToolTip createToolTip() {
            return new PatternTileTooltip(this);
        }
    }
}
