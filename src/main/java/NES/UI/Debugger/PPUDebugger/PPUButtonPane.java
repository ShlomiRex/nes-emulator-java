package NES.UI.Debugger.PPUDebugger;

import NES.UI.Debugger.DebuggerUIEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class PPUButtonPane extends JPanel {

    private final Logger logger = LoggerFactory.getLogger(PPUButtonPane.class);
    private boolean is_running;
    private final JPanel debugger_pane;

    public PPUButtonPane(DebuggerUIEvents ui_events, JPanel debugger_pane) {
        this.debugger_pane = debugger_pane;

        JButton btn_tick = new JButton("Tick");
        JButton btn_run = new JButton("Run");
        JButton btn_stop = new JButton("Stop");

        btn_stop.setEnabled(false);

        btn_tick.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Tick clicked");
                synchronized (ui_events) {
                    ui_events.next_tick_request = true;
                    ui_events.notify();
                    try {
                        ui_events.wait();
                        debugger_pane.repaint();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });

        btn_run.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Run clicked");
                synchronized (ui_events) {
                    ui_events.run_request = true;
                    ui_events.stop_request = false;
                    ui_events.notify();
                }
                is_running = true;
                btn_tick.setEnabled(false);
                btn_run.setEnabled(false);
                btn_stop.setEnabled(true);

                new RepainterThread().start();
            }
        });

        btn_stop.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Stop clicked");
                ui_events.stop_request = true;
                ui_events.run_request = false;

                is_running = false;
                btn_tick.setEnabled(true);
                btn_run.setEnabled(true);
                btn_stop.setEnabled(false);

                debugger_pane.repaint();
            }
        });

        add(btn_tick);
        add(btn_run);
        add(btn_stop);
    }

    class RepainterThread extends Thread {
        @Override
        public void run() {
            while(is_running) {
                debugger_pane.repaint();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
