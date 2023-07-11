package NES.UI.Debugger.CPUDebugger;

import NES.CPU.CPU;
import NES.UI.Debugger.AssemblyDebugger.AssemblyTextPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CPUButtonPane extends JPanel {

    private final Logger logger = LoggerFactory.getLogger(CPUButtonPane.class);
    private boolean is_running;
    private final JPanel debugger_pane;

    private Runnable repaint_ppu_pane_runnable;

    /**
     * @param debugger_pane      The panel to repaint to update UI once the tick is done
     */
    public CPUButtonPane(CPU cpu, JPanel debugger_pane, AssemblyTextPane assembly_text_pane) {
        this.debugger_pane = debugger_pane;

        JButton btn_tick = new JButton("Tick");
        JButton btn_run = new JButton("Run");
        JButton btn_stop = new JButton("Stop");

        JPanel box_pane = new JPanel();
        box_pane.setLayout(new BoxLayout(box_pane, BoxLayout.PAGE_AXIS));
        box_pane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        box_pane.add(btn_tick);
        box_pane.add(btn_run);
        box_pane.add(btn_stop);

        btn_stop.setEnabled(false);

        add(box_pane);

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
                        repaint_ppu_pane_runnable.run();
                        assembly_text_pane.highlight_current_instruction();
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
                        assembly_text_pane.highlight_current_instruction();
                    }

                    @Override
                    protected void done() {
                        debugger_pane.repaint();
                        repaint_ppu_pane_runnable.run();
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
    }

    // Called when we need to update the PPU panel
    public void setRepaintPpuPane(Runnable repaint_ppu_pane_runnable) {
        this.repaint_ppu_pane_runnable = repaint_ppu_pane_runnable;
    }
}
