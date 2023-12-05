package NES.UI.Debugger;

import javax.swing.*;

public class MainControlsPane extends JPanel {

    public MainControlsPane() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Main Controls"));

        JButton btn_run = new JButton("Run");
        JCheckBox chk_max_speed = new JCheckBox("Max Speed");

        add(btn_run);
        add(chk_max_speed);
    }
}
