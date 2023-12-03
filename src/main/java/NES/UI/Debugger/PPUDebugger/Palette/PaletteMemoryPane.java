package NES.UI.Debugger.PPUDebugger.Palette;

import NES.Common;
import NES.PPU.PPU;
import NES.UI.Debugger.PPUDebugger.PatternTable.PatternTablesPane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;

public class PaletteMemoryPane extends JPanel {

    private final PPU ppu;
    private final PaletteTilePane[] palette_tiles = new PaletteTilePane[32];

    public PaletteMemoryPane(PPU ppu, PatternTablesPane patternTablesPane) {
        this.ppu = ppu;
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Palette RAM");
        setBorder(titledBorder);

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        for (int i = 0; i < 32; i++) {
            PaletteTilePane tile = new PaletteTilePane(0, 24, 32);
            palette_tiles[i] = tile;

            int finalI = i;
            tile.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e); // Call PaletteTilePane mouseEntered (don't override it)
                    boolean is_background_selected = finalI < 16;
                    short palette_memory_location = (short) (0x3F00 + finalI);
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

            int col = i % 16;
            int row = i / 16;

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = row;

            // Add bottom gap between rows
            Insets insets = new Insets(0, 0, 4, 0);

            if (col % 4 == 0) {
                // Add horizontal gap every 4 columns
                insets.left = 4;
            }

            gbc.insets = insets;

            add(tile, gbc);
        }

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
