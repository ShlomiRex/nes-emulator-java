package NES.UI.Game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import NES.NES;
import NES.Common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameWindow extends JFrame implements KeyListener {

    private final Logger logger = LoggerFactory.getLogger(GameWindow.class);
    private final NES nes;

    public GameWindow(NES nes, GamePanel panel) {
        this.nes = nes;

        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("NES Emulator - Written by: Shlomi Domnenko");

        // Set icon
        Image img_icon = new ImageIcon("resources/NES_icon.png").getImage();
        setIconImage(img_icon);

        setVisible(true);
        pack();
        setLocationRelativeTo(null);
        addKeyListener(this);

        ControllerGlassPane controllerGlassPane = new ControllerGlassPane(panel, nes.bus);
        this.setGlassPane(controllerGlassPane);
        controllerGlassPane.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //logger.debug("Key code pressed: " + e.getKeyCode());

        /*
        PC Keyboard     |   NES Controller
        -----------------------------------
          X             |      A Button
          Z             |      B Button
          Spacebar      |      Select Button
          Enter         |      Start Button
          Arrow Up      |      Up Direction
          Arrow Down    |      Down Direction
          Arrow Left    |      Left Direction
          Arrow Right   |      Right Direction
         */

        switch(e.getKeyCode()) {
            case 'X' -> nes.bus.controllers[0] |= 128;
            case 'Z' -> nes.bus.controllers[0] |= 64;
            case 32 -> nes.bus.controllers[0] |= 32;           // Select
            case 10 -> nes.bus.controllers[0] |= 16;           // Start
            case 38 -> nes.bus.controllers[0] |= 8;              // Arrow Up
            case 40 -> nes.bus.controllers[0] |= 4;           // Arrow Down
            case 37 -> nes.bus.controllers[0] |= 2;            // Arrow Left
            case 39 -> nes.bus.controllers[0] |= 1;         // Arrow Right
        }
        //logger.debug("Controller 0: " + Common.byteToBinary(nes.bus.controllers[0]));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Release keys
        switch(e.getKeyCode()) {
            case 'X' -> nes.bus.controllers[0] &= ~128;
            case 'Z' -> nes.bus.controllers[0] &= ~64;
            case 32 -> nes.bus.controllers[0] &= ~32;           // Select
            case 10 -> nes.bus.controllers[0] &= ~16;           // Start
            case 38 -> nes.bus.controllers[0] &= ~8;              // Arrow Up
            case 40 -> nes.bus.controllers[0] &= ~4;           // Arrow Down
            case 37 -> nes.bus.controllers[0] &= ~2;            // Arrow Left
            case 39 -> nes.bus.controllers[0] &= ~1;         // Arrow Right
        }
    }
}
