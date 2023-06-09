package NES.UI.Debugger.PPUDebugger.Palette;

import NES.PPU.SystemPallete;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SystemPalettePane extends JPanel {

    public SystemPalettePane() {
        setBorder(new TitledBorder("System Palette"));
        setLayout(new GridLayout(4, 16));

        int table_width = 400;
        int table_height = 150;

        setMaximumSize(new Dimension(table_width, table_height));
        setPreferredSize(new Dimension(table_width, table_height));

        int tile_width = table_width / 16;
        int tile_height = table_height / 4;

        for (int row = 0; row < 4; row ++) {
            for (int col = 0; col < 16; col ++) {
                int tile_index = row*16 + col;
                JPanel palette_tile = new PaletteTilePane(tile_index, tile_width, tile_height);
                add(palette_tile);
            }
        }
    }
}
