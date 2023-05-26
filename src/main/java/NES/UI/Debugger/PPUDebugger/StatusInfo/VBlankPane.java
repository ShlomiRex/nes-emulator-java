package NES.UI.Debugger.PPUDebugger.StatusInfo;

import NES.PPU.PPU;

import javax.swing.*;
import java.awt.*;

public class VBlankPane extends JPanel {

    private final PPU ppu;
    private final JCheckBox ppu_vblank;

    public VBlankPane(PPU ppu) {
        this.ppu = ppu;
        this.ppu_vblank = new JCheckBox("VBlank");

        ppu_vblank.setEnabled(false);
        add(ppu_vblank);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ppu_vblank.setSelected(ppu.registers.isNmiEnabled());
    }
}
