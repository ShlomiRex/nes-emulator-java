package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.CPU;
import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AssemnlyMainPane extends JPanel {


    private final Logger logger = LoggerFactory.getLogger(AssemnlyMainPane.class);
    public final AssemblyTextPane asm_text_pane;

    public AssemnlyMainPane(byte[] cpu_memory) {
        setBorder(BorderFactory.createLoweredBevelBorder());

        short starting_addr = (short) 0xC004;

        // Init assembly text area (left)
        int width = 300;
        int height = 320;
        asm_text_pane = new AssemblyTextPane(starting_addr, cpu_memory, 20);

        JPanel noWrapPanel = new JPanel( new BorderLayout() );
        noWrapPanel.add( asm_text_pane );

        JScrollPane scroll_pane = new JScrollPane(noWrapPanel);
        scroll_pane.setMinimumSize(new Dimension(width, height));
        scroll_pane.setPreferredSize(new Dimension(width, height));
        scroll_pane.setMaximumSize(new Dimension(width, height));

        // Initialize scroll bar (right)
        JScrollBar scrollbar = new JScrollBar(JScrollBar.VERTICAL);
        // Can select address 0x0000 - 0xFFFF
        scrollbar.setMinimum(0);
        scrollbar.setMaximum(0xFFFF);
        scrollbar.setUnitIncrement(1); // When clicking the arrows
        scrollbar.setBlockIncrement(0xF); // When clicking the bar / track
        scrollbar.setValue(starting_addr & 0xFFFF);
        scrollbar.setPreferredSize(new Dimension(20, height - 20));

        scrollbar.addAdjustmentListener(e -> {
            short addr = (short) e.getValue();
            asm_text_pane.generate_new_document(addr);
        });

        add(scroll_pane, BorderLayout.CENTER);
        add(scrollbar, BorderLayout.LINE_END);

        // Highlight first instruction
        asm_text_pane.ready_next_instruction();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        asm_text_pane.ready_next_instruction();
    }
}
