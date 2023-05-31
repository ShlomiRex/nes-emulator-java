package NES.UI.Debugger.PPUDebugger.Palette;

import NES.Common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PaletteTilePane extends JPanel {

    private final int tile_index;
    private final Color color;
    private final int width, height;

    public PaletteTilePane(int tile_index, Color color, int width, int height) {
        this.tile_index = tile_index;
        this.color = color;
        this.width = width;
        this.height = height;

        String tooltip_text = ""+Common.byteToHex((byte) tile_index, true)+
                " Color: ("+color.getRed()+", "+color.getGreen()+", "+color.getBlue()+")";
        if (tile_index == 0x0D)
            tooltip_text += " Note: blacker than black";

        setPreferredSize(new Dimension(width, height));

        String finalTooltip_text = tooltip_text;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                //selected_tile_label.setText("Tile: $" + Common.byteToHexString(tile_index, false));
                setToolTipText(finalTooltip_text);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setBorder(BorderFactory.createEmptyBorder());
                //selected_tile_label.setText("Tile:");
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(color);
        g.fillRect(0, 0, width, height);

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

        //int str_width = g.getFontMetrics().stringWidth("00");
        g.drawString(Common.byteToHex((byte) tile_index, false), 0, height / 4);

        // Show the 0x0D color as big red X
        if (tile_index == 0x0D) {
            g.setColor(Color.RED);
            g.drawLine(0, 0, width, height);
            g.drawLine(width, 0, 0, height);
        }
    }
}
