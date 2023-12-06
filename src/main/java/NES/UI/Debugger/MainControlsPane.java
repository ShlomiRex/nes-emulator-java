package NES.UI.Debugger;

import NES.UI.Debugger.AssemblyDebugger.AssemblyTextPane;
import NES.UI.Debugger.CPUDebugger.CPUButtonPane;
import NES.UI.Debugger.PPUDebugger.PPUButtonPane;

import javax.swing.*;

public class MainControlsPane extends JPanel {

    public MainControlsPane(NES.NES nes,
                            Runnable repaint_ppu_pane_runnable,
                            CPUButtonPane cpuButtonPane,
                            PPUButtonPane ppuButtonPane,
                            AssemblyTextPane assembly_text_pane) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Main Controls"));

        JButton btn_run = new JButton("Run");
        JButton btn_stop = new JButton("Stop");
        JCheckBox chk_max_speed = new JCheckBox("Max emulator speed");

        btn_stop.setEnabled(false);

        btn_run.addActionListener(e -> {
            nes.run(chk_max_speed.isSelected());

            btn_run.setEnabled(false);
            btn_stop.setEnabled(true);
            cpuButtonPane.disableControls();
            ppuButtonPane.disableControls();
            chk_max_speed.setEnabled(false);
        });

        btn_stop.addActionListener(e -> {
            nes.stop();

            btn_run.setEnabled(true);
            btn_stop.setEnabled(false);
            cpuButtonPane.enableControls();
            ppuButtonPane.enableControls();
            chk_max_speed.setEnabled(true);

            repaint_ppu_pane_runnable.run();
            assembly_text_pane.highlight_current_instruction();
        });

        add(btn_run);
        add(btn_stop);
        add(chk_max_speed);
    }

}
