package NES.UI.Debugger.PPUDebugger.Nametable;

import NES.Common;
import NES.PPU.PPU;
import NES.UI.Debugger.PPUDebugger.PatternTable.PatternTilePane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class TileInfoPane extends JPanel {

    private final Logger logger = LoggerFactory.getLogger(TileInfoPane.class);
    private final JTextField txt_location, txt_index;
    private int selected_tile_index;
    public static final int SCALE = 16;
    private final PatternTilePane pattern_tile;
    private final PPU ppu;

    public TileInfoPane(PPU ppu) {
        this.ppu = ppu;
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

        pattern_tile = new PatternTilePane(ppu, 8 * SCALE, 8 * SCALE, (byte)0, true, null);
        add(pattern_tile);
    }

    public void setSelectedTileIndex(boolean is_nametable_A, int selected_tile_index) {
        int selected_col = selected_tile_index % 32;
        int selected_row = (selected_tile_index - selected_col) / 30;

        short addr;
        if (is_nametable_A) {
            addr = 0x2000;
        } else {
            addr = 0x2400;
        }
        addr += (short) (selected_row * 32 + selected_col);
        byte patternTileIndex = ppu.read(addr);
        logger.debug("Selected pattern tile index: {}", patternTileIndex);


        pattern_tile.change_tile_index(patternTileIndex);
        this.selected_tile_index = patternTileIndex;

        String selected_location = "(" + selected_col + ", " + selected_row + ")";
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
