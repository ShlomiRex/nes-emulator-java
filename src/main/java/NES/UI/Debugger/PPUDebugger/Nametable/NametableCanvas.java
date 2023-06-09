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

public class NametableCanvas extends JPanel implements MouseListener, MouseMotionListener {

    private final Logger logger = LoggerFactory.getLogger(NametableCanvas.class);
    private static final int ROWS = 30;
    private static final int COLUMNS = 32;

    private static final int SCALE = 1;

    protected AtomicInteger tile_hover = new AtomicInteger(-1);
    protected AtomicInteger tile_selected = new AtomicInteger(-1);
    private final NametableInfoPane info_pane;
    public final int table_index;

    public NametableCanvas(int table_index, NametableInfoPane info_pane) {
        this.table_index = table_index;
        this.info_pane = info_pane;

        setPreferredSize(new Dimension(256 * SCALE, 240 * SCALE));

        addMouseListener(this);
        addMouseMotionListener(this);
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

        int tile_hover = this.tile_hover.get();
        int tile_selected = this.tile_selected.get();

        if (tile_hover != -1) {
            int x = tile_hover % COLUMNS;
            int y = tile_hover / COLUMNS;
            g.setColor(Color.BLUE);
            g.drawRect(x * 8 * SCALE, y * 8 * SCALE, 8 * SCALE, 8 * SCALE);
        }

        if (tile_selected != -1) {
            int x = tile_selected % COLUMNS;
            int y = tile_selected / COLUMNS;
            g.setColor(Color.RED);
            g.drawRect(x * 8 * SCALE, y * 8 * SCALE, 8 * SCALE, 8 * SCALE);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX() / (8 * SCALE);
        int y = e.getY() / (8 * SCALE);

        tile_hover.set(y * COLUMNS + x);
        repaint();

        info_pane.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        logger.debug("Canvas " + table_index + ": Mouse exited");
        tile_hover.set(-1);
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        tile_selected.set(tile_hover.get());

        repaint();
        info_pane.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }
}
