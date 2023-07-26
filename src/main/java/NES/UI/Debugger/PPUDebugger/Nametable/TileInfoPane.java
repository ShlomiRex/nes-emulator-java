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
    private final JTextField
            txt_ppu_addr,
            txt_nametable,
            txt_location,
            txt_tile_index,
            txt_tile_addr,
            txt_attr_data,
            txt_attr_addr,
            txt_palette_addr;
    private int selected_tile_index;
    public static final int SCALE = 16;
    private final PatternTilePane pattern_tile;
    private final PPU ppu;
    private final NametablePane nametable_pane;

    public TileInfoPane(NametablePane nametablePane, PPU ppu) {
        this.nametable_pane = nametablePane;
        this.ppu = ppu;

        setBorder(BorderFactory.createTitledBorder("Tile Info"));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel row1_pane = new JPanel();
        JPanel row2_pane = new JPanel();
        JPanel row3_pane = new JPanel();
        JPanel row4_pane = new JPanel();
        JPanel row5_pane = new JPanel();
        JPanel row6_pane = new JPanel();
        JPanel row7_pane = new JPanel();
        JPanel row8_pane = new JPanel();
        JPanel last_row_pane = new JPanel();

        JLabel lbl_ppu_addr = new JLabel("PPU Addr: ");
        JLabel lbl_nametable = new JLabel("Nametable: ");
        JLabel lbl_location = new JLabel("Location: ");
        JLabel lbl_tile_index = new JLabel("Tile index: ");
        JLabel lbl_tile_addr = new JLabel("Tile Addr: ");
        JLabel lbl_attr_data = new JLabel("Attribute Data: ");
        JLabel lbl_attr_addr = new JLabel("Attribute Addr: ");
        JLabel lbl_palette_addr = new JLabel("Palette Addr: ");

        JCheckBox chk_tile_grid = new JCheckBox("Show Tile Grid");
        JCheckBox chk_attr_grid = new JCheckBox("Show Attribute Grid");

        txt_ppu_addr = new JTextField("");
        txt_nametable = new JTextField("");
        txt_location = new JTextField("");
        txt_tile_index = new JTextField("");
        txt_tile_addr = new JTextField("");
        txt_attr_data = new JTextField("");
        txt_attr_addr = new JTextField("");
        txt_palette_addr = new JTextField("");

        txt_ppu_addr.setColumns(4);
        txt_nametable.setColumns(4);
        txt_location.setColumns(4);
        txt_tile_index.setColumns(4);
        txt_tile_addr.setColumns(4);
        txt_attr_data.setColumns(4);
        txt_attr_addr.setColumns(4);
        txt_palette_addr.setColumns(4);

        txt_ppu_addr.setEditable(false);
        txt_nametable.setEditable(false);
        txt_location.setEditable(false);
        txt_tile_index.setEditable(false);
        txt_tile_addr.setEditable(false);
        txt_attr_data.setEditable(false);
        txt_attr_addr.setEditable(false);
        txt_palette_addr.setEditable(false);

        row1_pane.add(lbl_ppu_addr);
        row1_pane.add(txt_ppu_addr);

        row2_pane.add(lbl_nametable);
        row2_pane.add(txt_nametable);

        row3_pane.add(lbl_location);
        row3_pane.add(txt_location);

        row4_pane.add(lbl_tile_index);
        row4_pane.add(txt_tile_index);

        row5_pane.add(lbl_tile_addr);
        row5_pane.add(txt_tile_addr);

        row6_pane.add(lbl_attr_data);
        row6_pane.add(txt_attr_data);

        row7_pane.add(lbl_attr_addr);
        row7_pane.add(txt_attr_addr);

        row8_pane.add(lbl_palette_addr);
        row8_pane.add(txt_palette_addr);

        pattern_tile = new PatternTilePane(ppu, 8 * SCALE, 8 * SCALE, (byte)0, true, null);
        last_row_pane.add(pattern_tile);

        add(row1_pane);
        add(row2_pane);
        add(row3_pane);
        add(row4_pane);
        add(row5_pane);
        add(row6_pane);
        add(row7_pane);
        add(row8_pane);
        add(chk_tile_grid);
        add(chk_attr_grid);
        add(last_row_pane);

        chk_tile_grid.setSelected(true);
        chk_tile_grid.addActionListener(e -> {
            nametable_pane.setShowTileGrid(chk_tile_grid.isSelected());
        });

        chk_attr_grid.setSelected(false);
        chk_attr_grid.addActionListener(e -> {
            nametable_pane.setShowAttributeGrid(chk_attr_grid.isSelected());
        });
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

        txt_tile_index.setText(Common.byteToHex(patternTileIndex, true));

        this.selected_tile_index = patternTileIndex;

        String selected_location = "(" + selected_col + ", " + selected_row + ")";
        txt_location.setText(selected_location);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (selected_tile_index != -1)
            txt_tile_index.setText(Common.shortToHex((short) selected_tile_index, false));
    }

}
