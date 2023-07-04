package NES.UI.Debugger.PPUDebugger.Palette;

import javax.swing.*;
import java.awt.*;

public class PaletteMemoryPane extends JPanel {
    public PaletteMemoryPane() {
        setBorder(BorderFactory.createTitledBorder("Palette Memory"));

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        for (int i = 0; i < 32; i++) {
            JPanel tile = new PaletteTilePane(0, 24, 32);

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
