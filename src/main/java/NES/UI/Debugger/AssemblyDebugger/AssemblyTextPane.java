package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.CPU;
import NES.CPU.Registers.CPURegisters;
import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import java.awt.*;

public class AssemblyTextPane extends JTextPane {

    private final Logger logger = LoggerFactory.getLogger(AssemblyTextPane.class);
    private final Highlighter.HighlightPainter highlightPainter;

    private final Highlighter highlighter;
    private AssemblyStyledDocument assemblyDocument;
    private final byte[] cpu_memory;
    private final int lines_to_display;

    public AssemblyTextPane(short starting_addr, byte[] cpu_memory, int lines_to_display) {
        this.cpu_memory = cpu_memory;
        this.lines_to_display = lines_to_display;

        this.highlighter = getHighlighter();
        this.highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

        generate_new_document(starting_addr);

        setEditable(false);
        setFont(new Font("monospaced", Font.PLAIN, 12));

        addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            int scrollAmount = e.getScrollAmount();
            // notches negative = Scroll down - go up!
            // notches positive = Scroll up - go down!
            assemblyDocument.scroll(notches < 0);
        });
    }

    public void generate_new_document(short starting_addr) {
        this.assemblyDocument = new AssemblyStyledDocument(starting_addr, lines_to_display, cpu_memory, true);
        setDocument(assemblyDocument);
    }

    // Call when CPU finishes executing instruction and is ready for next instruction.
    // Here we move the highlighter to the next instruction.
    public void ready_next_instruction() {
        //TODO: Uncomment
//        highlighter.removeAllHighlights();
//
//        // Convert address to assembly line, and get starting offset of that line and end offset.
//        short pc = cpuRegisters.getPC();
//        AssemblyTextStructure.AssemblyLineTextStructure structure
//                = assemblyDocument.get_assembly_line(pc);
//        int start_offset = structure.document_offset();
//        int end_offset = start_offset + structure.line_length();
//
//        try {
//            highlighter.addHighlight(start_offset, end_offset, highlightPainter);
//        } catch (BadLocationException e) {
//            throw new RuntimeException(e);
//        }


    }
}
