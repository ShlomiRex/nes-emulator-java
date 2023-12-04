package NES.UI.Debugger.PPUDebugger.PatternTable;

import NES.Common;
import NES.PPU.PPU;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PatternTilePane extends JPanel {
    private final PPU ppu;
    public byte tile_index;
    public int palette_index; // 0-3

    private final int panel_width, panel_height;

    private int[][] pattern = new int[8][8]; // Each pixel is 0-3 (which represents the color index in the palette)

    public PatternTilePane(PPU ppu,
                           int panel_width,
                           int panel_height,
                           byte tile_index,
                           int palette_index,
                           boolean is_left_pattern_table) {
        this.ppu = ppu;
        this.tile_index = tile_index;
        this.palette_index = palette_index;
        if (palette_index != 0)
            System.out.println(palette_index);

        this.panel_width = panel_width;
        this.panel_height = panel_height;

        setPreferredSize(new Dimension(panel_width, panel_height));

        ppu.set_pattern_tile(tile_index, is_left_pattern_table, pattern);
    }

    public PatternTilePane(PPU ppu, byte tile_index, int palette_index, boolean is_left_pattern_table) {
        this(ppu, 16, 16, tile_index, palette_index, is_left_pattern_table);
    }

    /**
     * Generic function to paint a tile, given the container width, height.
     * @param g
     * @param container_width
     * @param container_height
     */
    protected void paintTile(Graphics g, int container_width, int container_height) {
        int pixel_width = container_width / 8;
        int pixel_height = container_height / 8;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int pixelValue = pattern[row][col];
                Color c = ppu.get_palette(pixelValue + palette_index * 4).getB();
                g.setColor(c);
                g.fillRect(col * pixel_width, row * pixel_height, pixel_width, pixel_height);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
        paintTile(g, panel_width, panel_height);
    }

    public void change_tile_index(byte tile_index) {
        this.tile_index = tile_index;
        repaint();
    }

    public void set_palette(int paletteIndex) {
        this.palette_index = paletteIndex;
    }
}