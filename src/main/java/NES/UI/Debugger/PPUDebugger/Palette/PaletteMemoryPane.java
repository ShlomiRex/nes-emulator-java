package NES.UI.Debugger.PPUDebugger.Palette;

import NES.Common;
import NES.PPU.PPU;
import NES.UI.Debugger.PPUDebugger.PatternTable.PatternTablesPane;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class PaletteMemoryPane extends JPanel {

    private final PPU ppu;

    private final JPanel[] bg_palette_panels = new JPanel[4]; // Each panel will outline the selected palette
    private final PaletteTilePane[] palette_tiles = new PaletteTilePane[32];

    private int selected_palette = 0;

    public PaletteMemoryPane(PPU ppu, PatternTablesPane patternTablesPane) {
        this.ppu = ppu;

        String TITLE_PREFIX = "Palette RAM";
        int TILE_WIDTH = 24;
        int TILE_HEIGHT = 32;

        TitledBorder titledBorder = BorderFactory.createTitledBorder(TITLE_PREFIX);
        setBorder(titledBorder);

        setLayout(new GridBagLayout());;

        int tile_index = 0;
        for (int row = 0; row < 2; row++) {
            for (int palette_i = 0; palette_i < 4; palette_i++) {
                JPanel palette_pane = new JPanel(new GridBagLayout());

                for (int color_i = 0; color_i < 4; color_i++) {
                    PaletteTilePane tile = new PaletteTilePane(0, TILE_WIDTH, TILE_HEIGHT);
                    palette_tiles[tile_index++] = tile;

                    int gridx = row * 16 + palette_i * 4 + color_i;

                    tile.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            super.mouseEntered(e); // Call PaletteTilePane mouseEntered (don't override it)
                            boolean is_background_selected = gridx < 16;
                            short palette_memory_location = (short) (0x3F00 + gridx);
                            titledBorder.setTitle("Palette RAM: " +
                                    Common.shortToHex(palette_memory_location, true) +
                                    (is_background_selected? " (Background)" : " (Sprite)"));
                            repaint();
                        }
                        @Override
                        public void mouseExited(MouseEvent e) {
                            super.mouseExited(e);
                            titledBorder.setTitle("Palette RAM");
                            repaint();
                        }
                    });

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridx = gridx;
                    gbc.gridy = 0;

                    // Inner panel padding
                    int padding = 1;
                    gbc.insets = new Insets(padding, color_i == 0 ? padding : 0, padding, color_i == 3 ? padding : 0);

                    palette_pane.add(tile, gbc);
                }
                GridBagConstraints gbc2 = new GridBagConstraints();
                gbc2.gridx = palette_i;
                gbc2.gridy = row;
                gbc2.insets = new Insets((row == 1)? 4 : 0, 0, 0, 4);
                add(palette_pane, gbc2);

                if (row == 0) {
                    bg_palette_panels[palette_i] = palette_pane;
                }
            }
        }

        // Select a random palette and outline it
        int outline_thinkness = 2;
        Color outline_color = Color.BLACK;
        selected_palette = 0;
        bg_palette_panels[selected_palette].setBorder(new LineBorder(outline_color, outline_thinkness));

        add(new JSeparator(JSeparator.HORIZONTAL));
        add(new JLabel("Use palette: "));
        String[] comboOptions = {"1", "2", "3", "4"};
        JComboBox comboBox = new JComboBox(comboOptions);
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selected = (String) e.getItem();
                    int palette = Integer.parseInt(selected) - 1; // Since index starts at 0
                    patternTablesPane.changePalette(palette);

                    bg_palette_panels[selected_palette].setBorder(null); // Remove outline from previous palette
                    bg_palette_panels[palette].setBorder(new LineBorder(outline_color, outline_thinkness));
                    selected_palette = palette;
                }
            }
        });
        add(comboBox);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(int i = 0; i < 32; i++) {
            PaletteTilePane tile = palette_tiles[i];
            Common.Pair<Integer, Color> res = ppu.get_palette(i);
            int color_index = res.getA();
            Color color = res.getB();
            tile.updateColor(color_index, color);
        }
    }
}
