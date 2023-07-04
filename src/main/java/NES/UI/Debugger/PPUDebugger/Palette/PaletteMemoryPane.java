package NES.UI.Debugger.PPUDebugger.Palette;

import javax.swing.*;
import java.awt.*;

public class PaletteMemoryPane extends JPanel {
    public PaletteMemoryPane() {
        setBorder(BorderFactory.createTitledBorder("Palette Memory"));
        GridLayout  layout = new GridLayout(2, 16);
        layout.setHgap(4);
        layout.setVgap(4);
        setLayout(layout);

        for (int i = 0; i < 32; i++) {
            JPanel a = new PaletteTilePane(0, 32, 32);
            add(a);
        }
    }
}
