package NES.UI.Game;

import NES.PPU.PPU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class GamePanel extends JPanel {

    // Scale the game by this factor, including screen size
    public static final int INITIAL_SCALE = 4;

    public static float SCALE_X;
    public static float SCALE_Y;

    private final PPU ppu;

    private static final Logger logger = LoggerFactory.getLogger(GamePanel.class);

    private int mouse_x;
    private int mouse_y;

    public GamePanel(PPU ppu) {
        this.ppu = ppu;
        setPreferredSize(new Dimension(256 * INITIAL_SCALE, 240 * INITIAL_SCALE));

        // Listen to mouse movement
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouse_x = e.getX();
                mouse_y = e.getY();
                StatusBar.instance.setMousePos((int) (mouse_x / SCALE_X), (int) (mouse_y / SCALE_Y));
                StatusBar.instance.setNametablePos((int) (mouse_x / (8 * SCALE_X)), (int) (mouse_y / (8 * SCALE_Y)));
            }
        });

        // Update scale when the window is resized
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                SCALE_X = (float) getWidth() / 256;
                SCALE_Y = (float) getHeight() / 240;
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //ppu.draw_frame(g, getWidth(), getHeight());
        ppu.flush_graphics(g, getWidth(), getHeight());

        if (MenuBarStatePreferences.instance.isNametableGridlines()) {
            draw_nametable_gridlines(g);
        }
        if (MenuBarStatePreferences.instance.isNametableHover()) {
            draw_nametable_hover(g);
        }
        if (MenuBarStatePreferences.instance.isPixelHover()) {
            draw_pixel_hover(g);
        }
//        draw_gridline((124 + 5) * SCALE, (109 + 8) * SCALE, g);
    }

    private void draw_nametable_gridlines(Graphics g) {
        g.setColor(Color.GRAY);
        for (int row = 0; row < 30; row++) {
            g.drawLine(0, (int) (row * 8 * SCALE_Y), (int) (256 * SCALE_X), (int) (row * 8 * SCALE_Y));
        }
        for (int col = 0; col < 32; col++) {
            g.drawLine((int) (col * 8 * SCALE_X), 0, (int) (col * 8 * SCALE_X), (int) (240 * SCALE_Y));
        }
    }

    private void draw_pixel_hover(Graphics g) {
        int x = (int) (mouse_x / SCALE_X);
        int y = (int) (mouse_y / SCALE_Y);

        g.setColor(Color.BLUE);
        g.fillRect(x * INITIAL_SCALE, y * INITIAL_SCALE, INITIAL_SCALE, INITIAL_SCALE);
    }

    private void draw_nametable_hover(Graphics g) {
        int nametable_x = (int) (mouse_x / (8 * SCALE_X));
        int nametable_y = (int) (mouse_y / (8 * SCALE_Y));

        g.setColor(Color.RED);
        g.drawRect((int) (nametable_x * 8 * SCALE_X), (int) (nametable_y * 8 * SCALE_Y), (int) (8 * SCALE_X), (int) (8 * SCALE_Y));
    }
}
