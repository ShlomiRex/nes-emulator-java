package NES.UI.Debugger.PPUDebugger.StatusInfo;

import NES.PPU.PPU;

import javax.swing.*;
import java.awt.*;

public class FramePane extends JPanel {

    private final PPU ppu;
    private final JLabel ppu_frame;

    public FramePane(PPU ppu) {
        this.ppu = ppu;
        this.ppu_frame = new JLabel("0");

        add(new JLabel("Frame: "));
        add(ppu_frame);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ppu_frame.setText(""+ppu.frame);
    }
}
