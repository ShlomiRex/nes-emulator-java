package NES.UI.Debugger.PPUDebugger.Nametable;

import NES.Common;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NametableInfoPane extends JPanel {

    private final JTextField txt_index;
    private AtomicInteger hover_tile_index, selected_tile_index;

    public NametableInfoPane() {
        setBorder(BorderFactory.createTitledBorder("Tile Info"));

        JLabel lbl_index = new JLabel("Selected tile: ");

        txt_index = new JTextField("");
        txt_index.setColumns(4);
        txt_index.setEditable(false);

        add(lbl_index);
        add(txt_index);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (hover_tile_index != null)
            txt_index.setText(Common.shortToHex(hover_tile_index.shortValue(), false));

        if (selected_tile_index != null)
            txt_index.setText(Common.shortToHex(selected_tile_index.shortValue(), false));
    }

    public void setHoverIndex(AtomicInteger index) {
        hover_tile_index = index;
    }

    public void setSelectedIndex(AtomicInteger tileSelected) {
        selected_tile_index = tileSelected;
    }
}
