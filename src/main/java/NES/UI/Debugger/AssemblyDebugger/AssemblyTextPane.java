package NES.UI.Debugger.AssemblyDebugger;

import NES.NES;
import NES.CPU.Registers.CPURegisters;
import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

public class AssemblyTextPane extends JTextPane {

    private final Logger logger = LoggerFactory.getLogger(AssemblyTextPane.class);
    private final CPURegisters cpuRegisters;
    private JScrollPane scrollPane;
    private final Highlighter.HighlightPainter highlightPainter;

    private final Highlighter highlighter;
    private final AssemblyStyledDocument styledDocument;
    private final int lines_to_display = 1024 * 1;

    public AssemblyTextPane(NES nes) {
        this.cpuRegisters = nes.cpu.registers;

        this.highlighter = getHighlighter();
        this.highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
        this.styledDocument = new AssemblyStyledDocument(
                this, nes.cpu_memory, true, lines_to_display);

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
            if (lines_to_display > 0)
                logger.error("Assembly line to highlight is not loaded, PC: {}", Common.shortToHex(pc, true));
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
