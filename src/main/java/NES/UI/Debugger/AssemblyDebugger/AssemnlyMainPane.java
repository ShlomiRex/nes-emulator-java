package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.Decoder;
import NES.CPU.Registers.CPURegisters;
import NES.Common;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AssemnlyMainPane extends JPanel {

    private final JTextPane assembly_text_area;
    private final Decoder decoder;
    private final byte[] cpu_memory;
    private final CPURegisters cpuRegisters;

    private final SimpleAttributeSet attr_regular;
    private final SimpleAttributeSet attr_instr;

    public AssemnlyMainPane(CPURegisters cpuRegisters, byte[] cpu_memory) {
        this.cpu_memory = cpu_memory;
        this.cpuRegisters = cpuRegisters;
        this.decoder = new Decoder();

        setBorder(BorderFactory.createLoweredBevelBorder());

        assembly_text_area = new JTextPane();
        assembly_text_area.setEditable(false);
        assembly_text_area.setAutoscrolls(true);
        assembly_text_area.setFont(new Font("monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(assembly_text_area);

        try {
            attr_regular = new SimpleAttributeSet();
            StyleConstants.setForeground(attr_regular, Color.BLACK);
            StyleConstants.setBold(attr_regular, true);

            attr_instr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr_instr, Color.BLUE);
            StyleConstants.setBold(attr_instr, true);

            initializeAssemblyText();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }


        add(assembly_text_area);
        add(scrollPane);
    }

    private void initializeAssemblyText() throws BadLocationException {
        int pc = cpuRegisters.getPC() & 0xFFFF;

        for (int assembly_line_num = 0; assembly_line_num < 30; assembly_line_num++) {
            // Read opcode
            byte opcode = cpu_memory[pc & 0xFFFF];
            String str_opcode = Common.byteToHexString(opcode, false);

            // Decode opcode
            Decoder.InstructionInfo info = decoder.decode_opcode(opcode);
            String decoded_instr = info.instr.toString();

            String str_operand1;
            String str_operand2;

            if (info.bytes == 1) {
                str_operand1 = "  ";
                str_operand2 = "  ";
            }
            else if (info.bytes == 2) {
                byte operand1 = cpu_memory[pc + 1];
                str_operand1 = Common.byteToHexString(operand1, false);
                str_operand2 = "  ";

                decoded_instr += " " + convert_1_operands_to_human_readable_text(info.addrmode, operand1);;
            } else if (info.bytes == 3) {
                byte operand1 = cpu_memory[pc + 1 & 0xFFFF];
                byte operand2 = cpu_memory[pc + 2 & 0xFFFF];
                str_operand1 = Common.byteToHexString(operand1, false);
                str_operand2 = Common.byteToHexString(operand2, false);

                decoded_instr += " " + convert_2_operands_to_human_readable_text(info.addrmode, operand1, operand2);;
            } else {
                throw new RuntimeException("Unexpected amount of bytes in instruction, must be at most 3");
            }

            String str_addr = Common.shortToHexString((short) pc, true) + "\t";
            append_without_style(str_addr);

            String str_opcode_and_operands = str_opcode + " " + str_operand1 + " " + str_operand2 + " \t";
            append_without_style(str_opcode_and_operands);

            append_instr(decoded_instr + "\n");

            pc += info.bytes;
        }
    }

    private void append_without_style(String str) throws BadLocationException {
        Document doc = assembly_text_area.getStyledDocument();
        doc.insertString(doc.getLength(), str, attr_regular);
    }

    private void append_instr(String str_instr) throws BadLocationException {
        Document doc = assembly_text_area.getStyledDocument();
        doc.insertString(doc.getLength(), str_instr, attr_instr);
    }

    private String convert_1_operands_to_human_readable_text(Decoder.AddressingMode addrmode, byte operand1) {
        switch (addrmode) {
            case IMMEDIATE -> {
                return "#$"+Common.byteToHexString(operand1, false);
            }
            case RELATIVE -> {
                // operand1 is offset
                // We add +2 because the debugger starts after the instruction is completed, i.e.
                // the PC is the next instruction. We want the old instruction
                short relative_addr = (short) (cpuRegisters.getPC() + operand1 + 2);
                return "$" + Common.shortToHexString(relative_addr, false);
            }
            default -> throw new RuntimeException("Not implemented yet");
        }
    }

    private String convert_2_operands_to_human_readable_text(Decoder.AddressingMode addrmode, byte operand1, byte operand2) {
        switch (addrmode) {
            case ABSOLUTE -> {
                // Little endian = switch order of operands that represent the address
                short addr = Common.convert_2_bytes_to_short(operand2, operand1);
                String knownTag = convert_addr_to_tag(addr);
                if (knownTag != null)
                    return knownTag;
                else
                    return "#$"+Common.byteToHexString(operand1, false);
            }
            default -> throw new RuntimeException("Not implemented yet");
        }
    }

    private String convert_addr_to_tag(short addr) {
        switch (addr) {
            case 0x2000 -> {
                return "PPU_CTRL";
            }
            case 0x2002 -> {
                return "PPU_STATUS";
            }
            default -> {
                return null;
            }
        }
    }
}
