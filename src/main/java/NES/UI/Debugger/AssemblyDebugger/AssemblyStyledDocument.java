package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.AddressingMode;
import NES.CPU.Decoder.AssemblyInfo;
import NES.CPU.Decoder.Decoder;
import NES.CPU.Decoder.DecoderException;
import NES.Common;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AssemblyStyledDocument {

    private final StyledDocument styledDocument;
    private final AssemblyTextStructure assemblyTextStructure;

    private SimpleAttributeSet attr_black, attr_blue, attr_gray, attr_green, attr_magenta;

    /**
     * Internal counter for keeping track of PC when adding assembly lines.
     */
    private short pc;

    /**
     * Internal counter for keeping track of offset when adding assembly lines.
     */
    private int offset = 0;

    private final byte[] cpu_memory;

    private boolean use_symbols;

    /**
     *
     * @param assembly_text_pane
     * @param cpu_memory
     * @param use_symbols If true, then the assembly text will use symbols instead of addresses.
     */
    public AssemblyStyledDocument(JTextPane assembly_text_pane, byte[] cpu_memory, boolean use_symbols) {
        this.cpu_memory = cpu_memory;
        this.styledDocument = assembly_text_pane.getStyledDocument();
        this.use_symbols = use_symbols;

        initialize_style();

        // Initialized assembly text structure
        assemblyTextStructure = new AssemblyTextStructure();

        // Starting PC - we can start from 0 if we want
        //pc = (short) (cpu.registers.getPC() & 0xFFFF);
        pc = (short) 0xC004;

        for (int asm_line_num = 0; asm_line_num < 350; asm_line_num++) {
            short old_pc = pc;
            int old_offset = offset;

            // This will increment pc and offset
            try {
                append_assembly_line();
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }

            // Calculate line length by subtracting the old offset from the new offset
            int line_length = offset - old_offset;

            // Add document related information to the assembly text structure
            assemblyTextStructure.add_assembly_line(asm_line_num, old_pc, old_offset, line_length);
        }
    }

    private void initialize_style() {
        attr_black = new SimpleAttributeSet();
        attr_gray = new SimpleAttributeSet();
        attr_blue = new SimpleAttributeSet();
        attr_green = new SimpleAttributeSet();
        attr_magenta = new SimpleAttributeSet();

        StyleConstants.setForeground(attr_black, Color.BLACK);
        StyleConstants.setForeground(attr_gray, Color.GRAY);
        StyleConstants.setForeground(attr_blue, new Color(0, 30, 116));
        StyleConstants.setForeground(attr_green, new Color(8, 124, 0));
        StyleConstants.setForeground(attr_magenta, Color.MAGENTA);

        StyleConstants.setBold(attr_black, true);
        StyleConstants.setBold(attr_gray, true);
        StyleConstants.setBold(attr_blue, true);
        StyleConstants.setBold(attr_green, true);
        StyleConstants.setBold(attr_magenta, true);
    }

    /**
     * Appends the assembly line to the text pane.
     * @return - new offset in the text pane
     */
    private void append_assembly_line() throws BadLocationException {
        boolean is_instr_illegal;
        AssemblyLineRecord record = null;
        try {
            record = AssemblyDecoder.decode_assembly_line2(pc, cpu_memory);
            is_instr_illegal = record.is_instr_illegal();
        } catch (DecoderException e) {
            is_instr_illegal = true;
        }

        if (!is_instr_illegal) {
            // Legal instruction
            insert_addr();
            insert_string("\t");
            insert_instr_bytes(record.bytes(), record.opcode(), record.operand1(), record.operand2());
            insert_string("\t");
            insert_instr(record);
            insert_string(" ");
            insert_operands(record);

            pc += record.bytes();
        } else {
            // If illegal instruction or we could not find the instruction in the table.
            // I don't cover all of the instructions. Those who I don't cover are 100% illegal.
            insert_addr();
            insert_string("\t");
            insert_instr_bytes(1, cpu_memory[pc & 0xFFFF], null, null);
            insert_string("\t");
            insert_string("UNDEFINED", attr_blue);

            pc += 1;
        }



        insert_string("\n");
    }

    private void insert_addr() throws BadLocationException {
        styledDocument.insertString(offset, Common.shortToHex(pc, true), attr_black);
        offset += 6; // '0xFFFF' = 6 characters
    }

    private void insert_instr_bytes(int bytes, byte opcode, Byte oper1, Byte oper2) throws BadLocationException {
        styledDocument.insertString(offset, Common.byteToHex(opcode, false), attr_gray);
        offset += 2;

        if (bytes > 1) {
            insert_string(" ");
            styledDocument.insertString(offset, Common.byteToHex(oper1, false), attr_gray);
            offset += 2;
        }
        if (bytes > 2) {
            insert_string(" ");
            styledDocument.insertString(offset, Common.byteToHex(oper2, false), attr_gray);
            offset += 2;
        }
    }

    private void insert_instr(AssemblyLineRecord record) throws BadLocationException {
        String str = record.instr_name();
        styledDocument.insertString(offset, str, attr_blue);
        offset += str.length();

        // If RTS
        if (record.opcode() == 0x60) {
            insert_string(" -------------", attr_magenta);
        }
    }

    private void insert_string(String str) throws BadLocationException {
        insert_string(str, null);
    }

    private void insert_string(String str, AttributeSet attr) throws BadLocationException {
        styledDocument.insertString(offset, str, attr);
        offset += str.length();
    }

    private void insert_operands(AssemblyLineRecord record) throws BadLocationException {
        AddressingMode addrmode = record.addressingMode();
        int bytes = record.bytes();

        Byte op1 = record.operand1();
        Byte op2 = record.operand2();

        short instr_addr = record.addr();

        String operand1_str = "";
        String operand2_str = "";

        // If and only if both operand 1 and operand 2 are not null this variable is initialized
        short op1_and_op2_addr = -1;
        // If and only if both operand 1 and operand 2 are not null AND use_symbols = true, this variable is initialized
        String op1_and_op2_addr_symbol = "";
        // If and only if op1_and_op2_addr is initialized, this variable is initialized
        String op1_and_op2_addr_str = "";

        if (bytes > 1) {
            operand1_str = Common.byteToHex(record.operand1(), false);
        }
        if (bytes > 2) {
            operand2_str = Common.byteToHex(record.operand2(), false);
            op1_and_op2_addr = Common.makeShort(record.operand1(), record.operand2());
            op1_and_op2_addr_str = Common.shortToHex(op1_and_op2_addr, false);

            if (use_symbols)
                op1_and_op2_addr_symbol = AssemblyDecoder.convert_addr_to_symbol(op1_and_op2_addr);
        }

        boolean is_illegal = record.is_instr_illegal();

        if (is_illegal) {
            throw new RuntimeException("Did not expect illegal instruction here");
        }

        switch(addrmode) {
            case IMPLIED:
                break;
            case IMMEDIATE:
                insert_string("#", attr_blue);
                if (bytes == 2) {
                    insert_string("$" + operand1_str, attr_green);
                } else {
                    throw new RuntimeException("Immediate requires 2 bytes");
                }
                break;
            case ABSOLUTE:
                if (bytes == 3) {
                    if (use_symbols && op1_and_op2_addr_symbol != null) {
                        insert_string(op1_and_op2_addr_symbol, attr_blue);
                    } else {
                        insert_string("$" + op1_and_op2_addr_str, attr_green);
                    }
                } else {
                    throw new RuntimeException("Absolute requires 3 bytes");
                }
                break;
            case RELATIVE:
                if (bytes == 2) {
                    // Calculate relative address: PC + signed op1
                    short relative_addr = (short) (instr_addr + op1 + 2); // Last +2 is because of instruction 2 bytes
                    // TODO: Not using symbols here since its branch operation, should not be an address to registers
                    insert_string("$" + Common.shortToHex(relative_addr, false), attr_green);
                } else {
                    throw new RuntimeException("Relative requires 2 bytes");
                }
                break;
            case ABSOLUTE_X:
                if (bytes == 3) {
                    insert_string("$"+op1_and_op2_addr_str, attr_green);
                    insert_string(",X", attr_blue);
                } else {
                    throw new RuntimeException("Absolute X requires 3 bytes");
                }
                break;
            case ZEROPAGE:
                if (bytes == 2) {
                    insert_string("$"+operand1_str, attr_green);
                } else {
                    throw new RuntimeException("Zeropage requires 2 bytes");
                }
                break;
            case ACCUMULATOR:
                if (bytes == 1) {
                    // do nothing
                } else {
                    throw new RuntimeException("Accumulator requires 1 byte");
                }
                break;
            case ABSOLUTE_INDIRECT:
                if (bytes == 3) {
                    insert_string("(", attr_blue);
                    insert_string("$"+op1_and_op2_addr_str, attr_green);
                    insert_string(")", attr_blue);
                } else {
                    throw new RuntimeException("Absolute indirect requires 3 bytes");
                }
                break;
            case INDIRECT_Y:
                if (bytes == 2) {
                    insert_string("(", attr_blue);
                    insert_string("$"+operand1_str, attr_green);
                    insert_string("),Y", attr_blue);
                } else {
                    throw new RuntimeException("Indirect Y requires 2 bytes");
                }
                break;
            case INDIRECT_X:
                if (bytes == 2) {
                    insert_string("(", attr_blue);
                    insert_string("$"+operand1_str, attr_green);
                    insert_string(",X)", attr_blue);
                } else {
                    throw new RuntimeException("Indirect X requires 2 bytes");
                }
                break;
            case ZEROPAGE_X:
                if (bytes == 2) {
                    insert_string("$"+operand1_str, attr_green);
                    insert_string(",X", attr_blue);
                } else {
                    throw new RuntimeException("Zeropage X requires 2 bytes");
                }
                break;
            case ABSOLUTE_Y:
                if (bytes == 3) {
                    insert_string("$"+op1_and_op2_addr_str, attr_green);
                    insert_string(",Y", attr_blue);
                } else {
                    throw new RuntimeException("Absolute Y requires 3 bytes");
                }
                break;
            default:
                throw new RuntimeException("Not implemented yet");
        }
    }

    public AssemblyTextStructure.AssemblyLineTextStructure get_assembly_line(short pc) {
        return assemblyTextStructure.get_assembly_line(pc);
    }
}
