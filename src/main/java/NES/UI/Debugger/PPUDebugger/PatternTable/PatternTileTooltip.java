package NES.UI.Debugger.PPUDebugger.PatternTable;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToolTipUI;
import java.awt.*;

public class PatternTileTooltip extends JToolTip {

    private final PatternTilePane pattern_tile_pane;
    public PatternTileTooltip(PatternTilePane pattern_tile_pane) {
        this.pattern_tile_pane = pattern_tile_pane;
        setUI(new LargeTileTooltipUI());
    }

    class LargeTileTooltipUI extends MetalToolTipUI {

        private static final int WIDTH = 100;
        private static final int HEIGHT = 100;

        @Override
        public void paint(Graphics g, JComponent c) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 100, 100);

            pattern_tile_pane.paintTile(g, WIDTH, HEIGHT);

            // Draw white border so it's easier to see
            g.setColor(Color.WHITE);
            g.drawRect(0, 0, 100, 100);
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(WIDTH, HEIGHT);
        }
    }
}
