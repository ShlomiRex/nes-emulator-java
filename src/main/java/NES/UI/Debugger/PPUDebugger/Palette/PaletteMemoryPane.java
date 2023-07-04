package NES.UI.Debugger.PPUDebugger.Palette;

import NES.Common;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PaletteMemoryPane extends JPanel {
    public PaletteMemoryPane() {
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Palette Memory");
        setBorder(titledBorder);

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        for (int i = 0; i < 32; i++) {
            JPanel tile = new PaletteTilePane(0, 24, 32);
            int finalI = i;
            tile.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e); // Call PaletteTilePane mouseEntered (don't override it)
                    short palette_memory_location = (short) (0x3F00 + finalI);
                    titledBorder.setTitle("Palette Memory: " + Common.shortToHex(palette_memory_location, true));
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    titledBorder.setTitle("Palette Memory");
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
    }
}
