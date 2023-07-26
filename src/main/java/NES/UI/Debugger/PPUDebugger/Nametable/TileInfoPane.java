package NES.UI.Debugger.PPUDebugger.Nametable;

import NES.Common;
import NES.PPU.PPU;
import NES.UI.Debugger.PPUDebugger.PatternTable.PatternTilePane;

import javax.swing.*;
import java.awt.*;

public class TileInfoPane extends JPanel {

    private final JTextField txt_location, txt_index;
    private byte selected_tile_index;
    public static final int SCALE = 16;
    private final PatternTilePane tile;
    private String selected_location;

    public TileInfoPane(PPU ppu) {
        setBorder(BorderFactory.createTitledBorder("Tile Info"));

        JLabel lbl_location = new JLabel("Location: ");
        JLabel lbl_index = new JLabel("Selected tile: ");

        txt_index = new JTextField("");
        txt_location = new JTextField("");

        txt_location.setColumns(4);
        txt_index.setColumns(4);

        txt_location.setEditable(false);
        txt_index.setEditable(false);

        add(lbl_location);
        add(txt_location);

        add(lbl_index);
        add(txt_index);

        tile = new PatternTilePane(ppu, 8 * SCALE, 8 * SCALE, (byte)0, true, null);
        add(tile);
    }

    public void setSelectedTileIndex(byte selected_tile_index) {
        this.selected_tile_index = selected_tile_index;
        tile.change_tile_index(selected_tile_index);

        selected_location = "(" + (selected_tile_index % 16) + ", " + (selected_tile_index / 16) + ")";
        txt_location.setText(selected_location);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (selected_tile_index != -1)
            txt_index.setText(Common.shortToHex((short) selected_tile_index, false));
    }

}
