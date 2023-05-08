package NES.UI;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {

    private final Window window;

    private final int HORIZONTAL_TILES = 32;
    private final int VERTICAL_TILES = 30;
    private final int HORIZONTAL_PIXELS = 256;
    private final int VERTICAL_PIXELS = 240;

    public Canvas(Window window) {
        this.window = window;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int win_width = window.getWidth();
        int win_height = window.getHeight();
        int canvas_width = getWidth();
        int canvas_height = getHeight();

        int tile_width = win_width / HORIZONTAL_TILES;
        int tile_height = win_height / VERTICAL_TILES;
        int pixel_width = win_width / HORIZONTAL_PIXELS;
        int pixel_height = win_height / VERTICAL_PIXELS;

        // Tiles
        g.setColor(Color.BLUE);
        for (int x = 0; x < HORIZONTAL_TILES; x++) {
            int tile_x = x * tile_width;
            g.drawLine(tile_x, 0, tile_x, canvas_height);
        }
        for (int y = 0; y < VERTICAL_TILES; y++) {
            int tile_y = y * tile_height;
            g.drawLine(0, tile_y, canvas_width, tile_y);
        }

        // Pixels
        g.setColor(Color.BLACK);
        for (int x = 0; x < HORIZONTAL_PIXELS; x++) {
            int pixel_x = x * pixel_width;
            g.drawLine(pixel_x, 0, pixel_x, canvas_height);
        }
//        for (int y = 0; y < VERTICAL_PIXELS; y++) {
//            int tile_y = y * tile_height;
//            g.drawLine(0, tile_y, canvas_width, tile_y);
//        }
    }
}
