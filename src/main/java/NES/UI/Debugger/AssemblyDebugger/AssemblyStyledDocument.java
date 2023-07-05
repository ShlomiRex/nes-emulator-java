package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.CPU;
import NES.CPU.Decoder.AssemblyInfo;
import NES.CPU.Decoder.Decoder;
import NES.Common;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AssemblyStyledDocument {

    private final StyledDocument styledDocument;
    private final CPU cpu;
    private final AssemblyTextStructure assemblyTextStructure;

    private SimpleAttributeSet attr_black, attr_blue, attr_gray, attr_green;

    /**
     * Internal counter for keeping track of PC when adding assembly lines.
     */
    private short pc;

    /**
     * Internal counter for keeping track of offset when adding assembly lines.
     */
    private int offset = 0;

    public AssemblyStyledDocument(JTextPane assembly_text_pane, CPU cpu, byte[] cpu_memory) {
        this.styledDocument = assembly_text_pane.getStyledDocument();
        this.cpu = cpu;

        initialize_style();

        // Initialized assembly text structure
        assemblyTextStructure = new AssemblyTextStructure(cpu, cpu_memory);

        // Starting PC - we can start from 0 if we want
        pc = (short) (cpu.registers.getPC() & 0xFFFF);

        for(int i = 0; i < 10; i++)
            append_assembly_line();
    }

    private void initialize_style() {
        attr_black = new SimpleAttributeSet();
        attr_gray = new SimpleAttributeSet();
        attr_blue = new SimpleAttributeSet();
        attr_green = new SimpleAttributeSet();

        StyleConstants.setForeground(attr_black, Color.BLACK);
        StyleConstants.setForeground(attr_gray, Color.GRAY);
        StyleConstants.setForeground(attr_blue, new Color(0, 30, 116));
        StyleConstants.setForeground(attr_green, new Color(8, 124, 0));

        StyleConstants.setBold(attr_black, true);
        StyleConstants.setBold(attr_gray, true);
        StyleConstants.setBold(attr_blue, true);
        StyleConstants.setBold(attr_green, true);
    }

    /**
     * Appends the assembly line to the text pane.
     * @return - new offset in the text pane
     */
    private void append_assembly_line() {
        AssemblyLineRecord record = assemblyTextStructure.getAssemblyLineRecord(pc);
        try {
            insert_addr(record);
            insert_string("\t");
            insert_instr_bytes(record);
            insert_string("\t");
            insert_instr(record);
            insert_string(" ");
            insert_string("\n");
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        pc += record.bytes();
    }

    private void insert_addr(AssemblyLineRecord record) throws BadLocationException {
        styledDocument.insertString(offset, Common.shortToHex(record.addr(), true), attr_black);
        offset += 6; // '0xFFFF' = 6 characters
    }

    private void insert_instr_bytes(AssemblyLineRecord record) throws BadLocationException {
        styledDocument.insertString(offset, Common.byteToHex(record.opcode(), false), attr_gray);
        offset += 2;

        if (record.bytes() > 1) {
            insert_string(" ");
            styledDocument.insertString(offset, Common.byteToHex(record.operand1(), false), attr_gray);
            offset += 2;
        }
        if (record.bytes() > 2) {
            insert_string(" ");
            styledDocument.insertString(offset, Common.byteToHex(record.operand2(), false), attr_gray);
            offset += 2;
        }
    }

    private void insert_instr(AssemblyLineRecord record) throws BadLocationException {
        String str = record.instr_name();
        styledDocument.insertString(offset, str, attr_blue);
        offset += str.length();
    }

    private void insert_string(String str) throws BadLocationException {
        styledDocument.insertString(offset, str, null);
        offset += str.length();
    }
}
