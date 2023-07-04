package NES.UI.Debugger.PPUDebugger.Palette;

import NES.UI.Debugger.PPUDebugger.PatternTable.PatternTileTooltip;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToolTipUI;
import java.awt.*;

public class PaletteTileTooltip extends JToolTip {

    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;
    private final PaletteTilePane palette_tile_pane;

    public PaletteTileTooltip(PaletteTilePane palette_tile_pane) {
        this.palette_tile_pane = palette_tile_pane;
        setUI(new LargeTileTooltipUI());
    }

    class LargeTileTooltipUI extends MetalToolTipUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            palette_tile_pane.paintTile(g, WIDTH, HEIGHT, true);
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(WIDTH, HEIGHT);
        }
    }
}
