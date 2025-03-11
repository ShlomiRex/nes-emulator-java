package NES.UI.Debugger.PPUDebugger;

import NES.Common;
import NES.PPU.PPU;

import javax.swing.*;
import java.awt.*;

public class PPUInternalRegistersPane extends JPanel {
    private final PPU ppu;

    private JTextField txt_loopy_v, txt_loopy_t, txt_x;
    private JCheckBox chk_w;

//    private JTextField loopy_v_coarse_x_scroll, loopy_v_coarse_y_scroll, loopy_v_nametable_select, loopy_v_fine_y_scroll;
//    private JTextField loopy_t_coarse_x_scroll, loopy_t_coarse_y_scroll, loopy_t_nametable_select, loopy_t_fine_y_scroll;

    public PPUInternalRegistersPane(PPU ppu) {
        this.ppu = ppu;

//        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createTitledBorder("Internal Registers"));

        // Internal registers
        JPanel internal_registers_pane = new JPanel();
        internal_registers_pane.setLayout(new BoxLayout(internal_registers_pane, BoxLayout.PAGE_AXIS));
//        internal_registers_pane.setBorder(BorderFactory.createTitledBorder(""));

        JPanel flow_pane1 = new JPanel(); // loopy_v
        JPanel loopy_v_pane = new JPanel();
        loopy_v_pane.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel flow_pane2 = new JPanel(); // loopy_t
        JPanel flow_pane3 = new JPanel(); // x
        JPanel flow_pane4 = new JPanel(); // w

        txt_loopy_v = new JTextField("0000");
//        loopy_v_coarse_x_scroll = new JTextField("0");
//        loopy_v_coarse_y_scroll = new JTextField("0");
//        loopy_v_nametable_select = new JTextField("0");
//        loopy_v_fine_y_scroll = new JTextField("0");

        txt_loopy_t = new JTextField("0000");
//        loopy_t_coarse_x_scroll = new JTextField("0");
//        loopy_t_coarse_y_scroll = new JTextField("0");
//        loopy_t_nametable_select = new JTextField("0");
//        loopy_t_fine_y_scroll = new JTextField("0");

        txt_x = new JTextField("00");
        chk_w = new JCheckBox();

        txt_loopy_v.setEditable(false);
//        loopy_v_coarse_x_scroll.setEditable(false);
//        loopy_v_coarse_y_scroll.setEditable(false);
//        loopy_v_nametable_select.setEditable(false);
//        loopy_v_fine_y_scroll.setEditable(false);

        txt_loopy_t.setEditable(false);
//        loopy_t_coarse_x_scroll.setEditable(false);
//        loopy_t_coarse_y_scroll.setEditable(false);
//        loopy_t_nametable_select.setEditable(false);
//        loopy_t_fine_y_scroll.setEditable(false);

        txt_x.setEditable(false);
        chk_w.setEnabled(false);

        txt_loopy_v.setColumns(4);
//        loopy_v_coarse_x_scroll.setColumns(1);
//        loopy_v_coarse_y_scroll.setColumns(1);
//        loopy_v_nametable_select.setColumns(1);
//        loopy_v_fine_y_scroll.setColumns(1);

        txt_loopy_t.setColumns(4);
//        loopy_t_coarse_x_scroll.setColumns(1);
//        loopy_t_coarse_y_scroll.setColumns(1);
//        loopy_t_nametable_select.setColumns(1);

        txt_x.setColumns(2);

        // loopy_v
        flow_pane1.add(new JLabel("loopy_v:"));
        flow_pane1.add(txt_loopy_v);
//        loopy_v_pane.add(new JLabel("Coarse X scroll:"));
//        loopy_v_pane.add(loopy_v_coarse_x_scroll);
//        loopy_v_pane.add(new JLabel("Coarse Y scroll:"));
//        loopy_v_pane.add(loopy_v_coarse_y_scroll);
//        loopy_v_pane.add(new JLabel("Nametable select:"));
//        loopy_v_pane.add(loopy_v_nametable_select);
//        loopy_v_pane.add(new JLabel("Fine Y scroll:"));
//        loopy_v_pane.add(loopy_v_fine_y_scroll);

        internal_registers_pane.add(flow_pane1);
//        internal_registers_pane.add(loopy_v_pane);

        // loopy_t
        flow_pane2.add(new JLabel("loopy_t:"));
        flow_pane2.add(txt_loopy_t);
        internal_registers_pane.add(flow_pane2);

        // x
        flow_pane3.add(new JLabel("x:"));
        flow_pane3.add(txt_x);
        internal_registers_pane.add(flow_pane3);

        // w
        flow_pane4.add(new JLabel("w:"));
        flow_pane4.add(chk_w);
        internal_registers_pane.add(flow_pane4);

        // Add panels
        add(internal_registers_pane);



//        // Add hint for loopy_v when the user hovers over the jtextfield dynamically
//        txt_loopy_v.setToolTipText("""
//                loopy_v:
//                Coarse X scroll: 0-4
//                Coarse Y scroll: 5-9
//                Nametable select: 10
//                Fine Y scroll: 12-14""");

    }

    @Override
    protected void paintComponent(Graphics g) {
        txt_loopy_v.setText(Common.shortToHex(ppu.registers.loopy_v, false));
        txt_loopy_t.setText(Common.shortToHex(ppu.registers.loopy_t, false));
        txt_x.setText(Common.byteToHex(ppu.bg_shifter_attrib_hi, false));
        chk_w.setSelected(ppu.registers.w);
    }
}
