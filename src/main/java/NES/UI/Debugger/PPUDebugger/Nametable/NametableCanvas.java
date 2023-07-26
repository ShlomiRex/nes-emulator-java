package NES.UI.Debugger.PPUDebugger.Nametable;

import NES.PPU.PPU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.atomic.AtomicInteger;

public class NametableCanvas extends JPanel {

    private final Logger logger = LoggerFactory.getLogger(NametableCanvas.class);
    private static final int ROWS = 30;
    private static final int COLUMNS = 32;

    public static final int SCALE = 2;

    protected int tile_hover = -1;
    protected int tile_selected = -1;
    public final int table_index;
    private final PPU ppu;

    public NametableCanvas(PPU ppu, int table_index) {
        this.ppu = ppu;
        this.table_index = table_index;
        setPreferredSize(new Dimension(256 * SCALE, 240 * SCALE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        ppu.draw_frame(g, getWidth(), getHeight());

        // Draw the grid
        g.setColor(Color.WHITE);
        for (int y = 0; y < ROWS; y++) {
            g.drawLine(0, y * 8 * SCALE, getWidth(), y * 8 * SCALE);
        }
        for (int x = 0; x < COLUMNS; x++) {
            g.drawLine(x * 8 * SCALE, 0, x * 8 * SCALE, getHeight());
        }


        // Draw hover tile
        if (tile_hover != -1) {
            draw_tile(g, true);
            draw_tile_block(g, true);
        }

        // Draw the selected tile
        if (tile_selected != -1) {
            draw_tile(g, false);
            draw_tile_block(g, false);
        }
    }

    /**
     *
     * @param g
     * @param is_hover_or_selected True if the tile is hovered. False if the tile is selected.
     */
    private void draw_tile(Graphics g, boolean is_hover_or_selected) {
        int col, row;
        Color color;

        if (is_hover_or_selected) {
            col = tile_hover % COLUMNS;
            row = tile_hover / COLUMNS;
            color = Color.BLUE;
        } else {
            col = tile_selected % COLUMNS;
            row = tile_selected / COLUMNS;
            color = Color.RED;
        }

        // Top-left graphics coordinates of the tile
        int canvas_x = col * 8 * SCALE;
        int canvas_y = row * 8 * SCALE;

        g.setColor(color);
        g.drawRect(canvas_x, canvas_y, 8 * SCALE, 8 * SCALE);
    }

    /**
     *
     * @param g
     * @param is_hover_or_selected True if the tile is hovered. False if the tile is selected.
     */
    private void draw_tile_block(Graphics g, boolean is_hover_or_selected) {
        int col, row;
        Color color;
        if (is_hover_or_selected) {
            col = tile_hover % COLUMNS;
            row = tile_hover / COLUMNS;
            color = Color.GREEN;
        } else {
            col = tile_selected % COLUMNS;
            row = tile_selected / COLUMNS;
            color = Color.MAGENTA;
        }

        // Top-left graphics coordinates of the tile
        int canvas_x = col * 8 * SCALE;
        int canvas_y = row * 8 * SCALE;

        int x_offset = 0;
        int y_offset = 0;

        if (col % 2 == 1)
            x_offset = - 8 * SCALE;

        if (row % 2 == 1)
            y_offset = - 8 * SCALE;

        // Draw the hovered tile block
        g.setColor(color);
        g.drawRect(canvas_x + x_offset, canvas_y + y_offset, 8 * SCALE * 2, 8 * SCALE * 2);
    }

}
