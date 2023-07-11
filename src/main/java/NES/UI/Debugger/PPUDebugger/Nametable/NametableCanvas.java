package NES.UI.Debugger.PPUDebugger.Nametable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class NametableCanvas extends JPanel {

    private final Logger logger = LoggerFactory.getLogger(NametableCanvas.class);
    private static final int ROWS = 30;
    private static final int COLUMNS = 32;

    private static final int SCALE = 2;

    private int tile_hover = -1;

    public NametableCanvas() {
        setPreferredSize(new Dimension(256 * SCALE, 240 * SCALE));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                int x = e.getX() / (8 * SCALE);
                int y = e.getY() / (8 * SCALE);

                tile_hover = y * COLUMNS + x;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);

                tile_hover = -1;
                logger.debug("Mouse exited");
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the grid
        g.setColor(Color.WHITE);
        for (int y = 0; y < ROWS; y++) {
            g.drawLine(0, y * 8 * SCALE, getWidth(), y * 8 * SCALE);
        }
        for (int x = 0; x < COLUMNS; x++) {
            g.drawLine(x * 8 * SCALE, 0, x * 8 * SCALE, getHeight());
        }

        // Draw the tile hover
        if (tile_hover != -1) {
            int x = tile_hover % COLUMNS;
            int y = tile_hover / COLUMNS;

            g.setColor(Color.BLUE);
            g.drawRect(x * 8 * SCALE, y * 8 * SCALE, 8 * SCALE, 8 * SCALE);
//            g.fillRect(0, 0, 100, 100);
        }
    }
}
