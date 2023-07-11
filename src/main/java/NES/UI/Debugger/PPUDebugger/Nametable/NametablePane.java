package NES.UI.Debugger.PPUDebugger.Nametable;

import javax.swing.*;
import java.awt.*;

public class NametablePane extends JPanel {
    public NametablePane() {
        JPanel nametable_pane = new JPanel();
        NametableCanvas canvas = new NametableCanvas();
        TileInfoPane tile_info = new TileInfoPane();

        nametable_pane.setBorder(BorderFactory.createTitledBorder("Nametable Viewer"));

        nametable_pane.add(canvas);
        add(nametable_pane);
        add(tile_info);
    }
}
