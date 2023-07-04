package NES.UI.Debugger.PPUDebugger.Palette;

import NES.Common;
import NES.PPU.SystemPallete;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PaletteTilePane extends JPanel {

    private final int tile_index;
    private final Color color;
    private final int width, height;

    public PaletteTilePane(int tile_index, int width, int height) {
        this.tile_index = tile_index;
        // Get color from system palette
        Color[][] system_palette = SystemPallete.getSystemPallete();
        this.color = system_palette[tile_index / 16][tile_index % 16];
        this.width = width;
        this.height = height;

        String tooltip_text = ""+Common.byteToHex((byte) tile_index, true)+
                " Color: ("+color.getRed()+", "+color.getGreen()+", "+color.getBlue()+")";
        if (tile_index == 0x0D)
            tooltip_text += " Note: blacker than black";

        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));

        String finalTooltip_text = tooltip_text;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                //selected_tile_label.setText("Tile: $" + Common.byteToHexString(tile_index, false));
                //setToolTipText(finalTooltip_text);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setBorder(BorderFactory.createEmptyBorder());
                //selected_tile_label.setText("Tile:");
            }
        });

        setToolTipText("");
    }

    /**
     *
     * @param g
     * @param container_width
     * @param container_height
     * @param tooltip Only used for tooltip. It paints differently on tooltip.
     */
    protected void paintTile(Graphics g, int container_width, int container_height, boolean tooltip) {
        g.setColor(color);
        g.fillRect(0, 0, container_width, container_height);

        // Determine color, like here: https://www.nesdev.org/wiki/PPU_palettes#2C02_and_2C07
        if (tile_index < 0x1F) {
            g.setColor(Color.WHITE);
            if (tile_index == 0x10)
                g.setColor(Color.BLACK);
        } else if (tile_index >= 0x20 && tile_index <= 0x2C) {
            g.setColor(Color.BLACK);
        } else if (tile_index >= 0x30 && tile_index <= 0x3D) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.WHITE);
        }

        Color no_0d_color = g.getColor(); // use it for text drawing

        // Show the 0x0D color as big red X
        if (tile_index == 0x0D) {
            g.setColor(Color.RED);
            g.drawLine(0, 0, container_width, container_height);
            g.drawLine(container_width, 0, 0, container_height);
        }

        g.setColor(no_0d_color);
        if (tooltip == false) {
            //int str_width = g.getFontMetrics().stringWidth("00");
            g.drawString(Common.byteToHex((byte) tile_index, false), 0, container_height / 4);
        } else {
            // Tooltip draw as hex in the middle
            g.setFont(new Font("Serif", Font.PLAIN, 22));
            g.drawString(Common.byteToHex((byte) tile_index, true), container_width / 4, container_height / 2);

            g.setFont(new Font("Serif", Font.PLAIN, 16));

            if (tile_index != 0x0D)
                g.drawString("Color: ("+color.getRed()+", "+color.getGreen()+", "+color.getBlue()+")", 0, container_height / 4 * 3);
            else
                g.drawString("Blacker than black", 0, container_height / 4 * 3);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintTile(g, width, height, false);
    }

    @Override
    public JToolTip createToolTip() {
        return new PaletteTileTooltip(this);
    }
}
