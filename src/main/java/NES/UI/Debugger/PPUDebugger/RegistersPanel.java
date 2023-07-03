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
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel flow_pane1 = new JPanel();
        JPanel flow_pane2 = new JPanel();

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

        txt_ppu_ctrl.setColumns(2);
        txt_ppu_mask.setColumns(2);
        txt_ppu_status.setColumns(2);
        txt_oam_addr.setColumns(2);
        txt_oam_data.setColumns(2);
        txt_ppu_scroll.setColumns(2);
        txt_ppu_addr.setColumns(2);
        txt_ppu_data.setColumns(2);


        flow_pane1.add(lbl_ppu_ctrl);
        flow_pane1.add(txt_ppu_ctrl);

        flow_pane1.add(lbl_ppu_mask);
        flow_pane1.add(txt_ppu_mask);

        flow_pane1.add(lbl_ppu_status);
        flow_pane1.add(txt_ppu_status);

        flow_pane1.add(lbl_oam_addr);
        flow_pane1.add(txt_oam_addr);

        flow_pane2.add(lbl_oam_data);
        flow_pane2.add(txt_oam_data);

        flow_pane2.add(lbl_ppu_scroll);
        flow_pane2.add(txt_ppu_scroll);

        flow_pane2.add(lbl_ppu_addr);
        flow_pane2.add(txt_ppu_addr);

        flow_pane2.add(lbl_ppu_data);
        flow_pane2.add(txt_ppu_data);

        add(flow_pane1);
        add(flow_pane2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        txt_ppu_ctrl.setText(Common.byteToHex(ppuRegisters.getPPUCTRL(), false));
        txt_ppu_mask.setText(Common.byteToHex(ppuRegisters.getPPUMASK(), false));
        txt_ppu_status.setText(Common.byteToHex(ppuRegisters.getPPUSTATUS(), false));
//        txt_oam_addr.setText(Common.byteToHexString(ppuRegisters.getOamAddr(), false));
//        txt_oam_data.setText(Common.byteToHexString(ppuRegisters.getOamData(), false));
//        txt_ppu_scroll.setText(Common.byteToHexString(ppuRegisters.getScroll(), false));
//        txt_ppu_addr.setText(Common.byteToHexString(ppuRegisters.getAddr(), false));
//        txt_ppu_data.setText(Common.byteToHexString(ppuRegisters.getData(), false));

    }
}
