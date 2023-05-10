package NES.UI.Debugger.PPUDebugger;

import NES.PPU.PPU;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PatternTablesPane extends JPanel {

    private final PPU ppu;
    //private final PatternTable left_table, right_table;

    public PatternTablesPane(PPU ppu) {
        this.ppu = ppu;

        setBorder(new TitledBorder("Pattern Tables"));
        setLayout(new GridLayout(16, 16));

        for(byte row = 0; row < 16; row ++) {
            for (byte col = 0; col < 16; col++) {
                byte tile_index = (byte)(col + row * 16);
                add(new PatternTilePane(ppu, tile_index, true));
            }
        }
    }
}
