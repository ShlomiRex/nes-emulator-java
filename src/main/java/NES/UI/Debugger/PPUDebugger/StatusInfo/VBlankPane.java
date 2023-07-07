package NES.UI.Debugger.PPUDebugger.StatusInfo;

import NES.Common;
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

        ppu_vblank.setToolTipText("PPUSTATUS Bit 7 - VBlank flag");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        boolean nmi = Common.Bits.getBit(ppu.registers.getPPUSTATUS(), 7);
        ppu_vblank.setSelected(nmi);
    }
}
