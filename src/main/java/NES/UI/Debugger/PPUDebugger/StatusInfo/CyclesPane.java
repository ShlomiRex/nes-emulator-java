package NES.UI.Debugger.PPUDebugger.StatusInfo;

import NES.PPU.PPU;

import javax.swing.*;
import java.awt.*;

public class CyclesPane extends JPanel {

    private final PPU ppu;
    private final JLabel ppu_cycles;

    public CyclesPane(PPU ppu) {
        this.ppu = ppu;
        this.ppu_cycles = new JLabel("0");

        add(new JLabel("Cycles: "));
        add(ppu_cycles);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ppu_cycles.setText(""+ppu.cycle);
    }
}
