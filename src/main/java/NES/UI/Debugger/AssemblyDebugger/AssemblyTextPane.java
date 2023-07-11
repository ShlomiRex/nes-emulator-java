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
    private JScrollPane scrollPane;
    private final Highlighter.HighlightPainter highlightPainter;

    private final Highlighter highlighter;
    private final AssemblyStyledDocument styledDocument;

    public AssemblyTextPane(CPU cpu, byte[] cpu_memory) {
        this.cpuRegisters = cpu.registers;

        this.highlighter = getHighlighter();
        this.highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
        this.styledDocument = new AssemblyStyledDocument(
                this, cpu_memory, true, 2048);

        setEditable(false);
        setFont(new Font("monospaced", Font.PLAIN, 12));
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    // Here we move the highlighter to the next instruction, and jump the view to it.
    public void highlight_current_instruction() {
        highlighter.removeAllHighlights();

        // Convert address to assembly line, and get starting offset of that line and end offset.
        short pc = cpuRegisters.PC;
        AssemblyTextStructure.AssemblyLineTextStructure structure
                = styledDocument.get_assembly_line(pc);
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

        // TODO: Scroll to highlighted line.
        if (scrollPane == null) {
            return;
        }
        Rectangle caretRectangle = null;
        try {
            caretRectangle = modelToView(start_offset);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        Rectangle viewRectangle = new Rectangle(0, caretRectangle.y -
                (scrollPane.getHeight() - caretRectangle.height) / 2,
                scrollPane.getWidth(), scrollPane.getHeight());
        scrollRectToVisible(viewRectangle);

    }
}
