package NES.UI.Game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import NES.NES;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindow extends JFrame implements KeyListener {

    private final Logger logger = LoggerFactory.getLogger(GameWindow.class);
    private final NES nes;

    public GameWindow(NES nes, GamePanel panel) {
        this.nes = nes;

        // Look and feel
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("NES Emulator - Written by: Shlomi Domnenko");

        // Set icon
        Image img_icon = new ImageIcon("resources/NES_icon.png").getImage();
        setIconImage(img_icon);

        addMenuBar();
        addBottomStatusBar();

        setVisible(true);
        pack();
        setLocationRelativeTo(null);
        addKeyListener(this);

        ControllerGlassPane controllerGlassPane = new ControllerGlassPane(panel, nes.bus);
        this.setGlassPane(controllerGlassPane);
        controllerGlassPane.setVisible(true);
    }

    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open ROM");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(openItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Debug menu
        JMenu debugMenu = new JMenu("Debug");
        JMenuItem nametable_gridlines = new JCheckBoxMenuItem("Show nametable gridlines");
        JMenuItem nametable_hover = new JCheckBoxMenuItem("Show nametable cell outline");
        JMenuItem pixel_hover = new JCheckBoxMenuItem("Show pixel hover outline");
        JMenuItem ppu_pixel = new JCheckBoxMenuItem("Show current processed pixel in PPU");
        debugMenu.add(nametable_gridlines);
        debugMenu.add(nametable_hover);
        debugMenu.add(pixel_hover);
        debugMenu.add(ppu_pixel);
        menuBar.add(debugMenu);

        // Read preferences and set the menu items accordingly
        nametable_gridlines.setSelected(MenuBarStatePreferences.instance.isNametableGridlines());
        nametable_hover.setSelected(MenuBarStatePreferences.instance.isNametableHover());
        pixel_hover.setSelected(MenuBarStatePreferences.instance.isPixelHover());

        // If user exits the menu, save the state of the menu items
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                logger.info("Exiting the program");
                MenuBarStatePreferences.instance.saveState(MenuBarStatePreferences.NAMETABLE_GRIDLINES, nametable_gridlines.isSelected());
                MenuBarStatePreferences.instance.saveState(MenuBarStatePreferences.NAMETABLE_HOVER, nametable_hover.isSelected());
                MenuBarStatePreferences.instance.saveState(MenuBarStatePreferences.PIXEL_HOVER, pixel_hover.isSelected());
            }
        });

        // Add change listeners to the menu items
        nametable_gridlines.addChangeListener(e -> {
            MenuBarStatePreferences.instance.saveState(MenuBarStatePreferences.NAMETABLE_GRIDLINES, nametable_gridlines.isSelected());
        });
        nametable_hover.addChangeListener(e -> {
            MenuBarStatePreferences.instance.saveState(MenuBarStatePreferences.NAMETABLE_HOVER, nametable_hover.isSelected());
        });
        pixel_hover.addChangeListener(e -> {
            MenuBarStatePreferences.instance.saveState(MenuBarStatePreferences.PIXEL_HOVER, pixel_hover.isSelected());
        });

        ppu_pixel.addChangeListener(e -> {
            MenuBarStatePreferences.instance.saveState(MenuBarStatePreferences.PPU_PIXEL, ppu_pixel.isSelected());
        });

        exitItem.addActionListener(e -> {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });

        setJMenuBar(menuBar);
    }

    private void addBottomStatusBar() {
        add(StatusBar.instance, BorderLayout.SOUTH);
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
