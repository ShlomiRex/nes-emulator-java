package NES.UI.Debugger.PPUDebugger.Nametable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NametablePane extends JPanel {

    private final Logger logger = LoggerFactory.getLogger(NametablePane.class);
    public NametablePane() {
        JPanel nametable_pane = new JPanel();

        NametableInfoPane tile_info = new NametableInfoPane();
        NametableCanvas canvas = new NametableCanvas(tile_info);

        tile_info.setHoverIndex(canvas.tile_hover);
        tile_info.setSelectedIndex(canvas.tile_selected);

        nametable_pane.setBorder(BorderFactory.createTitledBorder("Nametable Viewer"));

        nametable_pane.add(canvas);
        add(nametable_pane);
        add(tile_info);
    }
}
