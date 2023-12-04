package NES.UI.Debugger.PPUDebugger.Nametable;

import NES.Cartridge.Mirroring;
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
    private int selected_tile_index = -1;
    public static final int SCALE = 16;
    private final PatternTilePane pattern_tile;
    private final PPU ppu;
    private final NametablePane nametable_pane;
    private final Mirroring mirroring;

    public TileInfoPane(NametablePane nametablePane, PPU ppu, Mirroring mirroring) {
        this.nametable_pane = nametablePane;
        this.ppu = ppu;
        this.mirroring = mirroring;

        setBorder(BorderFactory.createTitledBorder("Tile Info"));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

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


        int palette_index = 0; // TODO: Change this, or get as parameter
        pattern_tile = new PatternTilePane(ppu,
                8 * SCALE, 8 * SCALE, (byte)0,
                palette_index, true);


        JCheckBox chk_tile_grid = new JCheckBox("Show Tile Grid");
        JCheckBox chk_attr_grid = new JCheckBox("Show Attribute Grid");

        chk_tile_grid.addActionListener(e -> {
            nametable_pane.setShowTileGrid(chk_tile_grid.isSelected());
        });

        chk_attr_grid.setSelected(false);
        chk_attr_grid.addActionListener(e -> {
            nametable_pane.setShowAttributeGrid(chk_attr_grid.isSelected());
        });


        JPanel wrapper = new JPanel();
        wrapper.add(pattern_tile);
        add(pattern_tile);

        add(createGrid());
        add(chk_tile_grid);
        add(chk_attr_grid);
    }

    private JPanel createGrid() {
        JPanel gridPane = new JPanel(new GridLayout(0, 2));

        JLabel lbl_ppu_addr = new JLabel("PPU Addr: ");
        JLabel lbl_nametable = new JLabel("Nametable: ");
        JLabel lbl_location = new JLabel("Location: ");
        JLabel lbl_tile_index = new JLabel("Tile index: ");
        JLabel lbl_tile_addr = new JLabel("Tile Addr: ");
        JLabel lbl_attr_data = new JLabel("Attribute Data: ");
        JLabel lbl_attr_addr = new JLabel("Attribute Addr: ");
        JLabel lbl_palette_addr = new JLabel("Palette Addr: ");

        gridPane.add(lbl_ppu_addr);
        gridPane.add(txt_ppu_addr);
        gridPane.add(lbl_nametable);
        gridPane.add(txt_nametable);
        gridPane.add(lbl_location);
        gridPane.add(txt_location);
        gridPane.add(lbl_tile_index);
        gridPane.add(txt_tile_index);
        gridPane.add(lbl_tile_addr);
        gridPane.add(txt_tile_addr);
        gridPane.add(lbl_attr_data);
        gridPane.add(txt_attr_data);
        gridPane.add(lbl_attr_addr);
        gridPane.add(txt_attr_addr);
        gridPane.add(lbl_palette_addr);
        gridPane.add(txt_palette_addr);

        return gridPane;
    }

    public void setSelectedTile(int selected_tile_index, int table_index) {
        int selected_col = selected_tile_index % 32;
        int selected_row = (selected_tile_index - selected_col) / 30;

        boolean is_nametable_A;
        if (mirroring == Mirroring.HORIZONTAL) {
            if (table_index == 0 || table_index == 1) {
                is_nametable_A = true;
            } else {
                is_nametable_A = false;
            }
        } else if (mirroring == Mirroring.VERTICAL) {
            if (table_index == 0 || table_index == 2) {
                is_nametable_A = true;
            } else {
                is_nametable_A = false;
            }
        } else {
            throw new RuntimeException("Not yet implemented");
        }

        short ppu_addr;
        if (is_nametable_A) {
            ppu_addr = 0x2000;
        } else {
            ppu_addr = 0x2400;
        }
        ppu_addr += (short) (selected_row * 32 + selected_col);
        //short attr_addr = (short) (ppu_addr - (ppu_addr % 0x400) + 0x3C0 + (selected_col / 4) + (selected_row / 4)*8); // TODO: Uncomment, calculation incorrect


        byte patternTileIndex = (byte) (ppu.read(ppu_addr) & 0xFF);

        // Update fields
        txt_ppu_addr.setText(Common.shortToHex(ppu_addr, true));
        txt_nametable.setText(String.valueOf(table_index + 1)); // Plus 1 for human display
        txt_tile_index.setText(Common.byteToHex(patternTileIndex, false));
        txt_location.setText("(" + selected_col + ", " + selected_row + ")");
        //txt_attr_addr.setText(Common.shortToHex(attr_addr, false)); // TODO: Uncomment, attr addr is not correct

        // Update pattern tile canvas
        pattern_tile.change_tile_index(patternTileIndex);

        // Update parameters
        this.selected_tile_index = patternTileIndex;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
