package NES.UI.Game;

import NES.PPU.PPU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(GamePanel.class);

    // Scale the game by this factor, including screen size
    public static final int SCALE = 4;

    private final PPU ppu;

    public GamePanel(PPU ppu) {
        this.ppu = ppu;
        setPreferredSize(new Dimension(256 * SCALE, 240 * SCALE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ppu.draw_frame(g, getWidth(), getHeight());

//        // Draw controllers
//        g.setColor(Color.BLUE);
//        g.fillRect(0, 0, 64, 64);
    }
}
