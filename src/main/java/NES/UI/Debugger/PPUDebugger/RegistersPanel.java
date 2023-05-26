package NES.UI.Debugger.PPUDebugger;

import javax.swing.*;
import java.awt.*;

public class RegistersPanel extends JPanel {
    public RegistersPanel() {
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

        JTextField txt_ppu_ctrl = new JTextField("00");
        JTextField txt_ppu_mask = new JTextField("00");
        JTextField txt_ppu_status = new JTextField("00");
        JTextField txt_oam_addr = new JTextField("00");
        JTextField txt_oam_data = new JTextField("00");
        JTextField txt_ppu_scroll = new JTextField("00");
        JTextField txt_ppu_addr = new JTextField("00");
        JTextField txt_ppu_data = new JTextField("00");

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
}
