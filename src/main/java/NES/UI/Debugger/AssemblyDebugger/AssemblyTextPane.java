package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.CPU;
import NES.CPU.Registers.CPURegisters;
import NES.Common;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

public class AssemblyTextPane extends JTextPane {

    private final CPURegisters cpuRegisters;
    private final Highlighter.HighlightPainter highlightPainter;
    private String text;

    public AssemblyTextPane(CPU cpu, byte[] cpu_memory) {
        this.cpuRegisters = cpu.registers;

        AssemblyStyledDocument assemblyDocument = new AssemblyStyledDocument(this, cpu, cpu_memory);

        highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

        setEditable(false);
        setFont(new Font("monospaced", Font.PLAIN, 12));
    }

    // Call when CPU finishes executing instruction and is ready for next instruction.
    // Here we move the highlighter to the next instruction.
    public void ready_next_instruction() {
        return;
//        Highlighter highlighter = getHighlighter();
//        highlighter.removeAllHighlights();
//        try {
//            short pc = cpuRegisters.getPC();
//            String search = Common.shortToHex(pc, true);
//
//            if (text == null)
//                text = getDocument().getText(0, getDocument().getLength());
//
//            int i = text.indexOf(search);
//            int first_new_line = text.indexOf('\n', i);
//            int line_length = first_new_line - i;
//
//            highlighter.addHighlight(i, i + line_length, highlightPainter);
//        } catch (BadLocationException e) {
//            throw new RuntimeException(e);
//        }
    }
}
