package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.CPU;
import NES.CPU.Registers.CPURegisters;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

public class AssemblyTextPane extends JTextPane {

    private final CPURegisters cpuRegisters;
    private final Highlighter.HighlightPainter highlightPainter;
    private String text;

    private final Highlighter highlighter;
    private final AssemblyStyledDocument assemblyDocument;

    public AssemblyTextPane(CPU cpu, byte[] cpu_memory) {
        this.cpuRegisters = cpu.registers;

        this.highlighter = getHighlighter();
        this.highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
        this.assemblyDocument = new AssemblyStyledDocument(
                this, cpu_memory, true, 1024);

        setEditable(false);
        setFont(new Font("monospaced", Font.PLAIN, 12));
    }

    // Call when CPU finishes executing instruction and is ready for next instruction.
    // Here we move the highlighter to the next instruction.
    public void ready_next_instruction() {
        highlighter.removeAllHighlights();

        // Convert address to assembly line, and get starting offset of that line and end offset.
        short pc = cpuRegisters.getPC();
        AssemblyTextStructure.AssemblyLineTextStructure structure
                = assemblyDocument.get_assembly_line(pc);
        if (structure == null) {
            // do not throw exception, the assembly line to highlight is not loaded.
            return;
        }
        int start_offset = structure.document_offset();
        int end_offset = start_offset + structure.line_length();

        try {
            highlighter.addHighlight(start_offset, end_offset, highlightPainter);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }


    }
}
