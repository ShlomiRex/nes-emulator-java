package NES.UI.Debugger.PPUDebugger;

import NES.PPU.PPU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class PPUButtonPane extends JPanel {

    private final Logger logger = LoggerFactory.getLogger(PPUButtonPane.class);
    private boolean is_running;

    public PPUButtonPane(PPU ppu, JPanel debugger_pane) {
        JButton btn_tick = new JButton("Tick");
        JButton btn_run = new JButton("Run");
        JButton btn_stop = new JButton("Stop");
        JSeparator separator = new JSeparator();
        JPanel box_pane = new JPanel();
        JPanel flow1_pane = new JPanel();
        JButton btn_run_custom = new JButton("Run custom ticks");
        JTextField txt_run_custom = new JTextField("50", 4);
        JPanel flow2_pane = new JPanel();
        JButton btn_run_scanline_custom = new JButton("Run custom scanlines");
        JTextField txt_run_scanline_custom = new JTextField("1", 4);
        JButton btn_run_until_vblank = new JButton("Run until VBlank");

        btn_stop.setEnabled(false);
        box_pane.setLayout(new BoxLayout(box_pane, BoxLayout.PAGE_AXIS));
        btn_run_custom.setToolTipText("Run a custom number of ticks");
        btn_run_scanline_custom.setToolTipText("Run a custom number of scanlines");

        add(btn_tick);
        add(btn_run);
        add(btn_stop);
        add(separator);
        flow1_pane.add(btn_run_custom);
        flow1_pane.add(txt_run_custom);
        flow2_pane.add(btn_run_scanline_custom);
        flow2_pane.add(txt_run_scanline_custom);
        box_pane.add(flow1_pane);
        box_pane.add(flow2_pane);
        box_pane.add(btn_run_until_vblank);
        add(box_pane);

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
                            ppu.clock_tick();
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

        btn_run_custom.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Run custom clicked with " + txt_run_custom.getText() + " ticks");

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        is_running = true;
                        btn_tick.setEnabled(false);
                        btn_run.setEnabled(false);
                        btn_stop.setEnabled(true);

                        int ticks = Integer.parseInt(txt_run_custom.getText());
                        for (int i = 0; i < ticks; i++) {
                            if (!is_running)
                                break;
                            ppu.clock_tick();
                            publish();
                        }
                        btn_tick.setEnabled(true);
                        btn_run.setEnabled(true);
                        btn_stop.setEnabled(false);
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

        btn_run_scanline_custom.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        logger.debug("Run custom scanlines clicked with " + txt_run_scanline_custom.getText() + " scanlines");

                        is_running = true;
                        btn_tick.setEnabled(false);
                        btn_run.setEnabled(false);
                        btn_stop.setEnabled(true);

                        int scanlines = Integer.parseInt(txt_run_scanline_custom.getText());
                        for(int scanline = 0; scanline < scanlines; scanline++) {
                            for (int i = 0; i < 341; i++) {
                                if (!is_running)
                                    break;
                                ppu.clock_tick();
                            }
                            debugger_pane.repaint();
                        }
                        btn_tick.setEnabled(true);
                        btn_run.setEnabled(true);
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

                        while (is_running && !ppu.registers.isNmiEnabled()) {
                            ppu.clock_tick();
                            publish();
                        }

                        is_running = false;
                        btn_tick.setEnabled(true);
                        btn_run.setEnabled(true);
                        btn_stop.setEnabled(false);

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
    }


}
