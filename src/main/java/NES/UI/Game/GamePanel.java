package NES.UI.Game;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private static final int TILE_SIZE = 32;
    private static final int TILE_COLUMNS = 32;
    private static final int TILE_ROWS = 30;

    private static final int PIXEL_SIZE = 4;
    private static final int PIXEL_COLUMNS = 256;
    private static final int PIXEL_ROWS = 240;

    public GamePanel() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int tileWidth = getWidth() / TILE_COLUMNS;
        int tileHeight = getHeight() / TILE_ROWS;
        int pixelSize = Math.min(getWidth() / PIXEL_COLUMNS, getHeight() / PIXEL_ROWS);

        // Draw tile grid
        g.setColor(Color.BLUE);
        for (int row = 0; row < TILE_ROWS; row++) {
            for (int col = 0; col < TILE_COLUMNS; col++) {
                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;
                g.drawRect(x, y, TILE_SIZE, TILE_SIZE);
            }
        }

        // Draw pixel grid
//        g.setColor(Color.BLACK);
//        for (int row = 0; row < PIXEL_ROWS; row++) {
//            for (int col = 0; col < PIXEL_COLUMNS; col++) {
//                int x = col * PIXEL_SIZE;
//                int y = row * PIXEL_SIZE;
//                g.drawRect(x, y, PIXEL_SIZE, PIXEL_SIZE);
//            }
//        }
    }
}
