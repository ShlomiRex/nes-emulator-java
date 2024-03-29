package NES.UI.Debugger.PPUDebugger;

import NES.Common;
import NES.PPU.PPU;
import NES.UI.Debugger.AssemblyDebugger.AssemblyTextPane;
import NES.UI.Debugger.CPUDebugger.CPUMainPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class PPUButtonPane extends JPanel {

    private final Logger logger = LoggerFactory.getLogger(PPUButtonPane.class);
    private boolean is_running;

    private final JButton btn_tick, btn_run, btn_stop, btn_run_custom, btn_run_scanline_custom, btn_run_until_vblank;

    public PPUButtonPane(PPU ppu, JPanel ppu_debugger_pane, CPUMainPane cpu_main_pane, AssemblyTextPane assemblyTextPane) {
        btn_tick = new JButton("Tick");
        btn_run = new JButton("Run");
        btn_stop = new JButton("Stop");
        btn_run_custom = new JButton("Run custom ticks");
        btn_run_scanline_custom = new JButton("Run custom scanlines");
        btn_run_until_vblank = new JButton("Run until VBlank");

        JPanel box_pane = new JPanel();
        JPanel box_pane2 = new JPanel();
        JPanel flow1_pane = new JPanel();
        JPanel flow2_pane = new JPanel();

        JSeparator separator = new JSeparator();

        JTextField txt_run_custom = new JTextField("50", 4);
        JTextField txt_run_scanline_custom = new JTextField("1", 4);


        box_pane.setLayout(new BoxLayout(box_pane, BoxLayout.PAGE_AXIS));
        box_pane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        box_pane2.setLayout(new BoxLayout(box_pane2, BoxLayout.PAGE_AXIS));
        btn_run_custom.setToolTipText("Run a custom number of ticks");
        btn_run_scanline_custom.setToolTipText("Run a custom number of scanlines");

        box_pane.add(btn_tick);
        box_pane.add(btn_run);
        box_pane.add(btn_stop);
        add(box_pane);
        add(separator);
        flow1_pane.add(btn_run_custom);
        flow1_pane.add(txt_run_custom);
        flow2_pane.add(btn_run_scanline_custom);
        flow2_pane.add(txt_run_scanline_custom);
        box_pane2.add(flow1_pane);
        box_pane2.add(flow2_pane);
        box_pane2.add(btn_run_until_vblank);
        add(box_pane2);

        btn_stop.setEnabled(false);

        btn_tick.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Tick clicked");

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        ppu.clock_tick();
                        return null;
                    }

                    @Override
                    protected void done() {
                        ppu_debugger_pane.repaint();
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

                        disableControls();
                        btn_stop.setEnabled(true);

                        while (is_running) {
                            ppu.clock_tick();
                            publish();
                        }
                        return null;
                    }

                    @Override
                    protected void process(List<Void> chunks) {
                        ppu_debugger_pane.repaint();
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

                enableControls();
                btn_stop.setEnabled(false);

                ppu_debugger_pane.repaint();

                assemblyTextPane.highlight_current_instruction();
            }
        });

        btn_run_custom.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Run custom clicked with " + txt_run_custom.getText() + " ticks");

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        is_running = true;

                        disableControls();
                        btn_stop.setEnabled(true);

                        int ticks = Integer.parseInt(txt_run_custom.getText());
                        for (int i = 0; i < ticks; i++) {
                            if (!is_running)
                                break;
                            ppu.clock_tick();
                            publish();
                        }
                        enableControls();
                        return null;
                    }

                    @Override
                    protected void process(List<Void> chunks) {
                        ppu_debugger_pane.repaint();
                    }
                };
                worker.execute();
            }
        });

        btn_run_scanline_custom.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        logger.debug("Run custom scanlines clicked with " + txt_run_scanline_custom.getText() + " scanlines");

                        is_running = true;

                        disableControls();
                        btn_stop.setEnabled(true);

                        int scanlines = Integer.parseInt(txt_run_scanline_custom.getText());
                        for(int scanline = 0; scanline < scanlines; scanline++) {
                            for (int i = 0; i < 341; i++) {
                                if (!is_running)
                                    break;
                                ppu.clock_tick();
                            }
                            ppu_debugger_pane.repaint();
                        }
                        enableControls();
                        btn_stop.setEnabled(false);
                        return null;
                    }
                };
                worker.execute();
            }
        });

        btn_run_until_vblank.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Run until VBlank clicked");

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        is_running = true;
                        btn_tick.setEnabled(false);
                        btn_run.setEnabled(false);
                        btn_stop.setEnabled(true);

                        // While NMI bit is 0
                        while (is_running && !Common.Bits.getBit(ppu.registers.getPPUSTATUS(), 7)) {
                            ppu.clock_tick();
                        }
                        publish();
                        //TODO: Don't know why the worker never reaches this code. It just calls 'done' and stops.
                        return null;
                    }

                    @Override
                    protected void process(List<Void> chunks) {
                        ppu_debugger_pane.repaint();
                    }

                    @Override
                    protected void done() {
                        is_running = false;
                        btn_tick.setEnabled(true);
                        btn_run.setEnabled(true);
                        btn_stop.setEnabled(false);
                        ppu_debugger_pane.repaint();
                        cpu_main_pane.repaint();
                    }
                };
                worker.execute();
            }
        });
    }


    public void disableControls() {
        btn_tick.setEnabled(false);
        btn_run.setEnabled(false);
        btn_stop.setEnabled(false);
        btn_run_custom.setEnabled(false);
        btn_run_scanline_custom.setEnabled(false);
        btn_run_until_vblank.setEnabled(false);
    }

    public void enableControls() {
        btn_tick.setEnabled(true);
        btn_run.setEnabled(true);
        btn_stop.setEnabled(false);
        btn_run_custom.setEnabled(true);
        btn_run_scanline_custom.setEnabled(true);
        btn_run_until_vblank.setEnabled(true);
    }
}
