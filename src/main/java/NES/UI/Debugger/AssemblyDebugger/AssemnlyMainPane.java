package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.Decoder;
import NES.CPU.Registers.CPURegisters;
import NES.Common;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AssemnlyMainPane extends JPanel {

    public final AssemblyTextPane assembly_text_area;
    private final Decoder decoder;
    private final byte[] cpu_memory;
    private final CPURegisters cpuRegisters;

    private final SimpleAttributeSet attr_regular;
    private final SimpleAttributeSet attr_instr;
    private final SimpleAttributeSet attr_instr_bytes;
    private final SimpleAttributeSet attr_operands_prefix, attr_operands_suffix;

    public AssemnlyMainPane(CPURegisters cpuRegisters, byte[] cpu_memory) {
        this.cpu_memory = cpu_memory;
        this.cpuRegisters = cpuRegisters;
        this.decoder = new Decoder();

        setBorder(BorderFactory.createLoweredBevelBorder());

        assembly_text_area = new AssemblyTextPane(cpuRegisters);

        JScrollPane scrollPane = new JScrollPane(assembly_text_area);

        try {
            attr_regular = new SimpleAttributeSet();
            attr_instr_bytes = new SimpleAttributeSet();
            attr_instr = new SimpleAttributeSet();
            attr_operands_prefix = new SimpleAttributeSet();
            attr_operands_suffix = new SimpleAttributeSet();

            StyleConstants.setForeground(attr_regular, Color.BLACK);
            StyleConstants.setForeground(attr_instr_bytes, Color.GRAY);
            StyleConstants.setForeground(attr_instr, new Color(0, 30, 116));
            StyleConstants.setForeground(attr_operands_prefix, new Color(0, 30, 116));
            StyleConstants.setForeground(attr_operands_suffix, new Color(8, 124, 0));

            StyleConstants.setBold(attr_regular, true);
            StyleConstants.setBold(attr_instr_bytes, true);
            StyleConstants.setBold(attr_instr, true);
            StyleConstants.setBold(attr_operands_prefix, true);
            StyleConstants.setBold(attr_operands_suffix, true);

            initializeAssemblyText();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        // Highlight first instruction
        assembly_text_area.ready_next_instruction();

        add(assembly_text_area);
        add(scrollPane);
    }

    private void initializeAssemblyText() throws BadLocationException {
        short pc = (short) (cpuRegisters.getPC() & 0xFFFF);

        for (int assembly_line_num = 0; assembly_line_num < 34; assembly_line_num++) {
            Decoder.AssemblyInfo info = decoder.decode_assembly_line(cpu_memory, pc);

            // Assembly line address
            String str_addr = Common.shortToHex(pc, true);
            append(str_addr+"\t", attr_regular);

            // Instructions bytes (1-3 bytes)
            append(info.str_instr_bytes+"\t", attr_instr_bytes);

            // Opcode
            append(info.instr_info.instr.toString()+" ", attr_instr);

            // Operands
            String operands = info.decoded_operand_or_symbol;
            if (operands != null) {
                if (operands.contains("#")) {
                    // Not symbol - get the prefix and suffix
                    String[] split = operands.split("\\$");
                    append(split[0], attr_operands_prefix);
                    append("$"+split[1], attr_operands_suffix);
                } else {
                    // Symbol
                    append(operands, attr_operands_prefix);
                }
            }

            append("\n", attr_regular);
            pc += info.instr_info.bytes;
        }
    }

    private void append(String str, SimpleAttributeSet set) throws BadLocationException {
        Document doc = assembly_text_area.getStyledDocument();
        doc.insertString(doc.getLength(), str, set);
    }

}
