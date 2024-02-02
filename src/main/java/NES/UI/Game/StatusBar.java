package NES.UI.Game;

import javax.swing.*;

public class StatusBar extends JPanel {

    public static StatusBar instance = new StatusBar();

    private final JLabel lbl_mouse_x;
    private final JLabel lbl_mouse_y;
    private final JLabel lbl_nametable_x;
    private final JLabel lbl_nametable_y;
    private StatusBar() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(Box.createHorizontalStrut(10));

        lbl_mouse_x = new JLabel("0");
        lbl_mouse_y = new JLabel("0");

        add(new JLabel("X: "));
        add(lbl_mouse_x);
        add(new JLabel(" Y: "));
        add(lbl_mouse_y);

        add(Box.createHorizontalStrut(10));

        add(new JLabel("Nametable: "));
        lbl_nametable_x = new JLabel("0");
        lbl_nametable_y = new JLabel("0");

        add(lbl_nametable_x);
        add(new JLabel(", "));
        add(lbl_nametable_y);

//        add(new JLabel("FPS: "));
    }

    public void setMousePos(int x, int y) {
        lbl_mouse_x.setText(String.valueOf(x));
        lbl_mouse_y.setText(String.valueOf(y));
    }

    public void setNametablePos(int x, int y) {
        lbl_nametable_x.setText(String.valueOf(x));
        lbl_nametable_y.setText(String.valueOf(y));
    }
}
