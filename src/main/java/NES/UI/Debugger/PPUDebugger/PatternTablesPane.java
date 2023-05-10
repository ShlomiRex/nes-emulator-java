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

//        this.left_table = new PatternTable();
//        this.right_table = new PatternTable();
//
//        add(left_table);
//        add(right_table);
    }
}
