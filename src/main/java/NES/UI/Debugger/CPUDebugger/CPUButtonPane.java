package NES.UI.Debugger.CPUDebugger;

import NES.CPU.CPU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CPUButtonPane extends JPanel {

    private final Logger logger = LoggerFactory.getLogger(CPUButtonPane.class);
    private boolean is_running;
    private final JPanel debugger_pane;

    /**
     *
     * @param debugger_pane The panel to repaint to update UI once the tick is done
     */
    public CPUButtonPane(CPU cpu, JPanel debugger_pane) {
        this.debugger_pane = debugger_pane;

        JButton btn_tick = new JButton("Tick");
        JButton btn_run = new JButton("Run");
        JButton btn_stop = new JButton("Stop");

        btn_stop.setEnabled(false);

        btn_tick.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Tick clicked");

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        cpu.clock_tick();
                        return null;
                    }

                    @Override
                    protected void done() {
                        debugger_pane.repaint();
                    }
                };
                worker.execute();
            }
        });

        btn_run.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Run clicked");

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        is_running = true;
                        btn_tick.setEnabled(false);
                        btn_run.setEnabled(false);
                        btn_stop.setEnabled(true);

                        while (is_running) {
                            cpu.clock_tick();
                            publish();
                        }
                        return null;
                    }

                    @Override
                    protected void process(List<Void> chunks) {
                        debugger_pane.repaint();
                    }
                };
                worker.execute();
            }
        });

        btn_stop.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Stop clicked");

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
}