package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.CPU;
import NES.CPU.Decoder.AssemblyInfo;
import NES.CPU.Decoder.Decoder;
import NES.CPU.Registers.CPURegisters;
import NES.Common;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AssemnlyMainPane extends JPanel {

    public final AssemblyTextPane assembly_text_area;

    public AssemnlyMainPane(CPU cpu, byte[] cpu_memory) {

        setBorder(BorderFactory.createLoweredBevelBorder());

        assembly_text_area = new AssemblyTextPane(cpu, cpu_memory);

        // Highlight first instruction
        assembly_text_area.ready_next_instruction();

        JScrollPane scrollPane = new JScrollPane(assembly_text_area);
        assembly_text_area.setScrollPane(scrollPane);
        scrollPane.setPreferredSize(new Dimension(300, 600));
        add(scrollPane);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        assembly_text_area.ready_next_instruction();
    }
}
