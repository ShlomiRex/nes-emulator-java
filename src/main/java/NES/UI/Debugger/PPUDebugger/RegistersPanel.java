package NES.UI.Debugger.PPUDebugger;

import NES.Common;
import NES.PPU.PPURegisters;

import javax.swing.*;
import java.awt.*;

public class RegistersPanel extends JPanel {

    private final PPURegisters ppuRegisters;
    private final JTextField txt_ppu_ctrl;
    private final JTextField txt_ppu_mask;
    private final JTextField txt_ppu_status;
    private final JTextField txt_oam_addr;
    private final JTextField txt_oam_data;
    private final JTextField txt_ppu_scroll;
    private final JTextField txt_ppu_addr;
    private final JTextField txt_ppu_data;

    public RegistersPanel(PPURegisters ppuRegisters) {
        this.ppuRegisters = ppuRegisters;

        setBorder(BorderFactory.createTitledBorder("Registers"));

        setLayout(new GridLayout(2, 4));

        JLabel lbl_ppu_ctrl = new JLabel("PPUCTRL:");
        JLabel lbl_ppu_mask = new JLabel("PPUMASK:");
        JLabel lbl_ppu_status = new JLabel("PPUSTATUS:");
        JLabel lbl_oam_addr = new JLabel("OAMADDR:");
        JLabel lbl_oam_data = new JLabel("OAMDATA:");
        JLabel lbl_ppu_scroll = new JLabel("PPUSCROLL:");
        JLabel lbl_ppu_addr = new JLabel("PPUADDR:");
        JLabel lbl_ppu_data = new JLabel("PPUDATA:");

        lbl_ppu_ctrl.setToolTipText("PPU Control Register, Address: $2000");
        lbl_ppu_mask.setToolTipText("PPU Mask Register, Address: $2001");
        lbl_ppu_status.setToolTipText("PPU Status Register, Address: $2002");
        lbl_oam_addr.setToolTipText("OAM Address Register, Address: $2003");
        lbl_oam_data.setToolTipText("OAM Data Register, Address: $2004");
        lbl_ppu_scroll.setToolTipText("PPU Scroll Register, Address: $2005");
        lbl_ppu_addr.setToolTipText("PPU Address Register, Address: $2006");
        lbl_ppu_data.setToolTipText("PPU Data Register, Address: $2007");

        txt_ppu_ctrl = new JTextField("00");
        txt_ppu_mask = new JTextField("00");
        txt_ppu_status = new JTextField("00");
        txt_oam_addr = new JTextField("00");
        txt_oam_data = new JTextField("00");
        txt_ppu_scroll = new JTextField("00");
        txt_ppu_addr = new JTextField("00");
        txt_ppu_data = new JTextField("00");

        txt_ppu_ctrl.setEditable(false);
        txt_ppu_mask.setEditable(false);
        txt_ppu_status.setEditable(false);
        txt_oam_addr.setEditable(false);
        txt_oam_data.setEditable(false);
        txt_ppu_scroll.setEditable(false);
        txt_ppu_addr.setEditable(false);
        txt_ppu_data.setEditable(false);

        add(lbl_ppu_ctrl);
        add(txt_ppu_ctrl);

        add(lbl_ppu_mask);
        add(txt_ppu_mask);

        add(lbl_ppu_status);
        add(txt_ppu_status);

        add(lbl_oam_addr);
        add(txt_oam_addr);

        add(lbl_oam_data);
        add(txt_oam_data);

        add(lbl_ppu_scroll);
        add(txt_ppu_scroll);

        add(lbl_ppu_addr);
        add(txt_ppu_addr);

        add(lbl_ppu_data);
        add(txt_ppu_data);
    }

    @Override
    protected void paintComponent(Graphics g) {
        txt_ppu_ctrl.setText(Common.byteToHex(ppuRegisters.getCtrl(), false));
        txt_ppu_mask.setText(Common.byteToHex(ppuRegisters.getMask(), false));
        txt_ppu_status.setText(Common.byteToHex(ppuRegisters.getStatus(), false));
//        txt_oam_addr.setText(Common.byteToHexString(ppuRegisters.getOamAddr(), false));
//        txt_oam_data.setText(Common.byteToHexString(ppuRegisters.getOamData(), false));
//        txt_ppu_scroll.setText(Common.byteToHexString(ppuRegisters.getScroll(), false));
//        txt_ppu_addr.setText(Common.byteToHexString(ppuRegisters.getAddr(), false));
//        txt_ppu_data.setText(Common.byteToHexString(ppuRegisters.getData(), false));

    }
}
