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

    //private static final int PIXEL_SIZE = 4;
    private static final int PIXEL_COLUMNS = 256;
    private static final int PIXEL_ROWS = 240;

    public static final int SCALE = 2;

    private final PPU ppu;

    public GamePanel(PPU ppu) {
        this.ppu = ppu;
        setPreferredSize(new Dimension(PIXEL_COLUMNS * SCALE, PIXEL_ROWS * SCALE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // TODO: uncomment
        ppu.draw_frame(g, getWidth(), getHeight());
//
//        // Clear the screen
//        g.setColor(Color.BLACK);
//        g.fillRect(0, 0, getWidth(), getHeight());
//
//        if (frameBuffer == null)
//            return;
//
//        int pixel_width = getWidth() / PIXEL_COLUMNS;
//        int pixel_height = getHeight() / PIXEL_ROWS;
//        int tile_width = getWidth() / TILE_COLUMNS;
//        int tile_height = getHeight() / TILE_ROWS;
//
//        // Draw the frame buffer
//        for (int y = 0; y < PIXEL_ROWS; y++) {
//            for (int x = 0; x < PIXEL_COLUMNS; x++) {
//                int pixelIndex = y * PIXEL_COLUMNS + x;
//                byte pixelValue = frameBuffer[pixelIndex];
//                Color pixelColor = PPU.getColorFromPalette(pixelValue);
//
//                // Draw the pixel with the appropriate color
//                g.setColor(pixelColor);
//                g.fillRect(x * pixel_width, y * pixel_height, pixel_width, pixel_height);
//            }
//        }
//
////        // Draw tile grid
////        g.setColor(Color.BLUE);
////        for (int row = 0; row < TILE_ROWS; row++) {
////            for (int col = 0; col < TILE_COLUMNS; col++) {
////                int x = col * tile_width;
////                int y = row * tile_height;
////                g.drawRect(x, y, tile_width, tile_height);
////            }
////        }
    }
}
