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
        ppu.draw_frame(g, getWidth(), getHeight());
    }
}
