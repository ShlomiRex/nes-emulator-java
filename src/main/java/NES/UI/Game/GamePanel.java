package NES.UI.Game;

import NES.PPU.PPU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(GamePanel.class);

    private static final int TILE_SIZE = 32;
    private static final int TILE_COLUMNS = 32;
    private static final int TILE_ROWS = 30;

    private static final int PIXEL_SIZE = 4;
    private static final int PIXEL_COLUMNS = 256;
    private static final int PIXEL_ROWS = 240;

    private byte[] frameBuffer;

    public GamePanel() {
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        logger.debug("Painting component");

        if (frameBuffer == null)
            return;

        logger.debug("Frame buffer is null");

        int tileWidth = getWidth() / TILE_COLUMNS;
        int tileHeight = getHeight() / TILE_ROWS;
        int pixelSize = Math.min(getWidth() / PIXEL_COLUMNS, getHeight() / PIXEL_ROWS);

        // Clear the screen with a background color
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, PIXEL_COLUMNS, PIXEL_ROWS);

        // Draw the frame buffer
        for (int y = 0; y < PIXEL_ROWS; y++) {
            for (int x = 0; x < PIXEL_COLUMNS; x++) {
                int pixelIndex = y * PIXEL_COLUMNS + x;
                byte pixelValue = frameBuffer[pixelIndex];
                Color pixelColor = PPU.getColorFromPalette(pixelValue);

                // Draw the pixel with the appropriate color
                g.setColor(pixelColor);
                g.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
        }

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

    public void setPPUFrameBuffer(byte[] ppuData) {
        this.frameBuffer = ppuData;
    }
}
