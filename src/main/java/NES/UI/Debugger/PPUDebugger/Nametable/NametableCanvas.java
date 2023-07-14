package NES.UI.Debugger.PPUDebugger.Nametable;

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
    protected AtomicInteger tile_selected = new AtomicInteger(-1);
    private final NametableInfoPane info_pane;
    public final int table_index;

    public NametableCanvas(int table_index, NametableInfoPane info_pane) {
        this.table_index = table_index;
        this.info_pane = info_pane;

        setPreferredSize(new Dimension(256 * SCALE, 240 * SCALE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

//        // Draw the grid
//        g.setColor(Color.WHITE);
//        for (int y = 0; y < ROWS; y++) {
//            g.drawLine(0, y * 8 * SCALE, getWidth(), y * 8 * SCALE);
//        }
//        for (int x = 0; x < COLUMNS; x++) {
//            g.drawLine(x * 8 * SCALE, 0, x * 8 * SCALE, getHeight());
//        }

        int tile_selected = this.tile_selected.get();

        // Draw hover tile
        if (tile_hover != -1) {
            int col = tile_hover % COLUMNS;
            int row = tile_hover / COLUMNS;

            // Top-left graphics coordinates of the tile
            int canvas_x = col * 8 * SCALE;
            int canvas_y = row * 8 * SCALE;

            // Draw the hovered tile
            g.setColor(Color.RED);
            g.drawRect(canvas_x, canvas_y, 8 * SCALE, 8 * SCALE);


            int x_offset = 0;
            int y_offset = 0;

            if (col % 2 == 1)
                x_offset = - 8 * SCALE;

            if (row % 2 == 1)
                y_offset = - 8 * SCALE;

            // Draw the hovered tile block
            g.setColor(Color.GREEN);
            g.drawRect(canvas_x + x_offset, canvas_y + y_offset, 8 * SCALE * 2, 8 * SCALE * 2);
        }

        // Draw the selected tile
        if (tile_selected != -1) {
            int x = tile_selected % COLUMNS;
            int y = tile_selected / COLUMNS;
            g.setColor(Color.RED);
            g.drawRect(x * 8 * SCALE, y * 8 * SCALE, 8 * SCALE, 8 * SCALE);
        }
    }

}
