package NES.UI.Debugger.PPUDebugger.PatternTable;

import NES.PPU.PPU;
import NES.UI.Debugger.PPUDebugger.Palette.PaletteCanvasPane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PatternTablesPane extends JPanel {

    private final PaletteCanvasPane left_table__canvas, right_table__canvas;

    public PatternTablesPane(PPU ppu) {
        setBorder(new TitledBorder("Pattern Tables"));

        JPanel left_table = new JPanel(new BorderLayout());
        JPanel right_table = new JPanel(new BorderLayout());
        JLabel left_table__lbl_tile_index = new JLabel("Tile:");
        JLabel right_table__lbl_tile_index = new JLabel("Tile:");

        left_table__canvas = new PaletteCanvasPane(ppu, 0, true, left_table__lbl_tile_index);
        right_table__canvas = new PaletteCanvasPane(ppu, 0, false, right_table__lbl_tile_index);

        left_table.add(left_table__lbl_tile_index, BorderLayout.PAGE_START);
        left_table.add(left_table__canvas, BorderLayout.CENTER);

        right_table.add(right_table__lbl_tile_index, BorderLayout.PAGE_START);
        right_table.add(right_table__canvas, BorderLayout.CENTER);

        add(left_table);
        add(right_table);
    }

    /**
     * This will change the pattern tables to display the selected palette (only color change, not the pixels).
     * @param palette_index
     */
    public void changePalette(int palette_index) {
        left_table__canvas.changePalette(palette_index);
        right_table__canvas.changePalette(palette_index);

        repaint();
    }
}
