package NES.UI.Debugger.PPUDebugger.PatternTable;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToolTipUI;
import java.awt.*;

public class PatternTileTooltip extends JToolTip {


    /*
    Why 96x96 pixels and not 100x100?

    Since a pattern tile is 8x8 pixels, we want to scale to 100x100 pixels, we need to scale to 96x96 pixels,
    since 96/8 = whole number. If we do it 100x100 then we will see 4 pixels gap between the real size of the pattern
    tile and the container border.
     */

    private static final int WIDTH = 96;
    private static final int HEIGHT = 96;
    private final PatternTilePane pattern_tile_pane;

    public PatternTileTooltip(PatternTilePane pattern_tile_pane) {
        this.pattern_tile_pane = pattern_tile_pane;
        setUI(new LargeTileTooltipUI());
    }

    class LargeTileTooltipUI extends MetalToolTipUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            pattern_tile_pane.paintTile(g, WIDTH, HEIGHT);

            // Draw white border so it's easier to see
            g.setColor(Color.WHITE);
            g.drawRect(0, 0, WIDTH, HEIGHT);

            g.drawString("Tile: $" + pattern_tile_pane.tile_index, 0, 10);
            g.drawString("Palette: " + pattern_tile_pane.palette_index, 0, 20);
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(WIDTH, HEIGHT);
        }
    }
}
