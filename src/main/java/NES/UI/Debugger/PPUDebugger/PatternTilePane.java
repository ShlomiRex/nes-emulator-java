package NES.UI.Debugger.PPUDebugger;

import NES.Common;
import NES.PPU.PPU;

import javax.swing.*;
import javax.swing.plaf.metal.MetalToolTipUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PatternTilePane extends JPanel {
    private final PPU ppu;
    private final byte tile_index;
    private final boolean is_left_pattern_table;

    private final int panel_width, panel_height;


    public PatternTilePane(PPU ppu, byte tile_index, boolean is_left_pattern_table, JLabel selected_tile_label) {
        this.ppu = ppu;
        this.tile_index = tile_index;
        this.is_left_pattern_table = is_left_pattern_table;

        this.panel_width = 16;
        this.panel_height = 16;

        setPreferredSize(new Dimension(panel_width, panel_height));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                selected_tile_label.setText("Tile: $" + Common.byteToHexString(tile_index, false));


            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setBorder(BorderFactory.createEmptyBorder());
                selected_tile_label.setText("Tile:");
            }
        });

        setToolTipText("");

    }

    /**
     * Generic function to paint a tile, given the container width, height.
     * @param g
     * @param container_width
     * @param container_height
     */
    private void paintTile(Graphics g, int container_width, int container_height) {
        byte[] tile = ppu.get_pattern_tile(tile_index, is_left_pattern_table);
        byte[][] pixels = ppu.convert_pattern_tile_to_pixel_pattern(tile);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, container_width, container_height);

        int pixel_width = container_width / 8;
        int pixel_height = container_height / 8;

        g.setColor(Color.WHITE); //TODO: Use color index instead of checking (pixel != 0)
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                byte pixel = pixels[row][col];
                if (pixel != 0) {
                    g.fillRect(col * pixel_width, row * pixel_height, pixel_width, pixel_height);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintTile(g, panel_width, panel_height);
    }

    @Override
    public JToolTip createToolTip() {
        return new MyToolTip();
    }

    class MyToolTip extends JToolTip {
        public MyToolTip() {
            setUI(new LargeTileTooltipUI());
        }
    }

    class LargeTileTooltipUI extends MetalToolTipUI {

        private static final int WIDTH = 100;
        private static final int HEIGHT = 100;

        @Override
        public void paint(Graphics g, JComponent c) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 100, 100);

            paintTile(g, WIDTH, HEIGHT);
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(WIDTH, HEIGHT);
        }
    }
}