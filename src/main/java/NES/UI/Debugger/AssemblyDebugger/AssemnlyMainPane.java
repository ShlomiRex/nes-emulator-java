package NES.UI.Debugger.AssemblyDebugger;

import NES.NES;

import javax.swing.*;
import java.awt.*;

public class AssemnlyMainPane extends JPanel {

    public final AssemblyTextPane assembly_text_area;

    public AssemnlyMainPane(NES nes) {
        setBorder(BorderFactory.createLoweredBevelBorder());

        assembly_text_area = new AssemblyTextPane(nes);

        // Highlight first instruction
        assembly_text_area.highlight_current_instruction();

        JScrollPane scrollPane = new JScrollPane(assembly_text_area);
        scrollPane.setPreferredSize(new Dimension(300, 600));
        assembly_text_area.setScrollPane(scrollPane);

        add(scrollPane);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        assembly_text_area.highlight_current_instruction();
    }
}
