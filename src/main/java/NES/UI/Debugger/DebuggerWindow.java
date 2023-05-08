package NES.UI.Debugger;

import NES.NES;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DebuggerWindow extends JFrame {

    private NES nes;

    public DebuggerWindow(NES nes, AtomicBoolean next_tick_event) {
        this.nes = nes;

        setTitle("6502 Debugger");
        //setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel main_pane = new JPanel();

        JPanel reg_pane = new RegistersPanel(nes.cpu.registers);
        JPanel stack_pane = new StackPanel();
        JPanel button_pane = new ButtonPane(next_tick_event, main_pane);
        //JPanel cycles_pane = new CyclesPane();

        main_pane.add(button_pane);
        main_pane.add(reg_pane);
        main_pane.add(stack_pane);
        //main_pane.add(cycles_pane);

        add(main_pane);
        pack();
        setVisible(true);
    }

    class CyclesPane extends JPanel {
        private final JLabel cpu_cycles;

        public CyclesPane() {
            this.cpu_cycles = new JLabel("0");

            add(new JLabel("CPU Cycles: "));
            add(cpu_cycles);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            cpu_cycles.setText(""+nes.cpu.cycles);
        }
    }
}
