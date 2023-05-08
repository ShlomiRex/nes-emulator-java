package NES.UI.Debugger;

import NES.NES;

import javax.swing.*;

public class DebuggerWin extends JFrame {

    private NES nes;

    public DebuggerWin(NES nes) {
        this.nes = nes;

        setTitle("6502 Debugger");
        //setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel reg_pane = new RegistersPanel(nes.cpu.registers);
        JPanel stack_pane = new StackPanel();

        JPanel main_pane = new JPanel();
        main_pane.add(reg_pane);
        main_pane.add(stack_pane);
        add(main_pane);

        pack();
        setVisible(true);
    }
}
