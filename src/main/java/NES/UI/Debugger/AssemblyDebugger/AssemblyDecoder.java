package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.AddressingMode;
import NES.CPU.Decoder.Decoder;
import NES.CPU.Decoder.InstructionInfo;
import NES.Common;

public class AssemblyDecoder {
    public static AssemblyLineRecord decode_assembly_line2(short addr, byte[] cpu_memory) {
        byte opcode = cpu_memory[addr & 0xFFFF];
        InstructionInfo info = Decoder.decode_opcode(opcode);

        int bytes = 1;
        if (info != null)
            bytes = info.bytes;

        Byte operand1 = null;
        Byte operand2 = null;

        if (bytes > 1) {
            operand1 = cpu_memory[(addr + 1) & 0xFFFF];

        }
        if (bytes > 2) {
            operand2 = cpu_memory[(addr + 2) & 0xFFFF];
        }

        return new AssemblyLineRecord(addr, bytes, opcode, operand1, operand2, info.instr.toString(), info.addrmode, info.is_illegal);
    }

    public static String convert_addrmode_and_operands_to_text(AddressingMode addrmode, byte operand1, short pc, boolean use_symbols) {
        String addr_str = Common.byteToHex(operand1, false);

        if (use_symbols) {
            String symbol = convert_addr_to_symbol(Common.makeShort((byte) 0, operand1));
            if (symbol != null)
                addr_str = symbol;
        }

        switch (addrmode) {
            case IMMEDIATE -> {
                return "#$" + addr_str;
            }
            case RELATIVE -> {
                // operand1 is offset
                // We add +2 because the debugger starts after the instruction is completed, i.e.
                // the PC is the next instruction. We want the old instruction
                short relative_addr = (short) (pc + operand1 + 2);
                return "$" + Common.shortToHex(relative_addr, false);
            }
            case ZEROPAGE -> {
                return "$" + addr_str;
            }
            case ZEROPAGE_X -> {
                return "$" + addr_str + ",X";
            }
            case INDIRECT_X -> {
                return "($" + addr_str + ",X)";
            }
            case INDIRECT_Y -> {
                return "($" + addr_str + "),Y";
            }
            default -> throw new RuntimeException("Not implemented yet");
        }
    }

    public static String convert_2_operands_to_human_readable_text(AddressingMode addrmode, byte operand1, byte operand2, boolean use_symbols) {
        short addr = Common.makeShort(operand2, operand1);
        String addr_str = Common.shortToHex(addr, false);
        switch (addrmode) {
            case ABSOLUTE:
                // Little endian = switch order of operands that represent the address
                return "#$" + Common.byteToHex(operand2, false) + Common.byteToHex(operand1, false);
            case ABSOLUTE_X:
                return "$" + addr_str + ",X";
            case ABSOLUTE_INDIRECT:
                return "($" + addr_str + ")";
            case ABSOLUTE_Y:
                addr = Common.makeShort(operand1, operand2); // switch order
                return "$" + addr_str + ",Y";
            default:
                throw new RuntimeException("Not implemented yet");
        }
    }

    public static String convert_addr_to_symbol(short addr) {
        switch (addr) {
            case 0x2000 -> {
                return "PPU_CTRL";
            }
            case 0x2001 -> {
                return "PPU_MASK";
            }
            case 0x2002 -> {
                return "PPU_STATUS";
            }
            case 0x2003 -> {
                return "OAM_ADDRESS";
            }
            case 0x2004 -> {
                return "OAM_DATA";
            }
            case 0x2005 -> {
                return "PPU_SCROLL";
            }
            case 0x2006 -> {
                return "PPU_ADDRRESS";
            }
            case 0x2007 -> {
                return "PPU_DATA";
            }
            case 0x4014 -> {
                return "OAM_DMA";
            }
            case 0x4015 -> {
                return "APU_STATUS";
            }
            case 0x4017 -> {
                return "JOY2_FRAME";
            }
            default -> {
                return null;
            }
        }
    }
}
