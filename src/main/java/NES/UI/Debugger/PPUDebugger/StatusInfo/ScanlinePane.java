package NES.UI.Debugger.PPUDebugger.StatusInfo;

import NES.PPU.PPU;

import javax.swing.*;
import java.awt.*;

public class ScanlinePane extends JPanel {

    private final PPU ppu;
    private final JLabel ppu_scanline;

    public ScanlinePane(PPU ppu) {
        this.ppu = ppu;
        this.ppu_scanline = new JLabel("0");

        add(new JLabel("Scanline: "));
        add(ppu_scanline);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ppu_scanline.setText(""+ppu.scanline);
    }
}
