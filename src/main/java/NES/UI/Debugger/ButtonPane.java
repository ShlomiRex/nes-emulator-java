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
                synchronized (next_tick) {
                    next_tick.set(true);
                    next_tick.notify();
                    try {
                        next_tick.wait();
                        debugger_pane.repaint();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        add(btn_tick);
    }
}
