package NES.UI.Debugger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class ButtonPane extends JPanel {
    public ButtonPane(AtomicBoolean next_tick, JPanel debugger_pane) {
        JButton btn_tick = new JButton("Next tick");
        btn_tick.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                next_tick.set(true);
                while(next_tick.get()) {
                    // Wait until CPU executes the next instruction
                    // Because this is UI thread, we can do infinite while loop. Doesn't affect CPU
                }
                debugger_pane.repaint();
            }
        });

        add(btn_tick);
    }
}
