package NES.UI.Debugger.CPUDebugger;

import NES.CPU.CPU;
import NES.CPU.Decoder;
import NES.Common;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class InstructionsPane extends JPanel {

    private final CPU cpu;
    private final Decoder decoder;
    private final byte[] cpu_memory;

    private final JLabel opcode;
    private final JLabel operand1;
    private final JLabel operand2;

    private final JTextField decoded_instr;

    public InstructionsPane(CPU cpu, byte[] cpu_memory) {
        this.cpu = cpu;
        this.cpu_memory = cpu_memory;

        setBorder(new TitledBorder("Next instruction"));

        opcode = new JLabel("00");
        operand1 = new JLabel("00");
        operand2 = new JLabel("00");

        opcode.setEnabled(false);
        operand1.setEnabled(false);
        operand2.setEnabled(false);

        opcode.setMinimumSize(opcode.getSize());
        operand1.setMinimumSize(operand1.getSize());
        operand2.setMinimumSize(operand2.getSize());

        add(opcode);
        add(operand1);
        add(operand2);

        decoded_instr = new JTextField("LDA PPU_STATUS", 10);
        decoded_instr.setEditable(false);
        decoded_instr.setMinimumSize(decoded_instr.getSize());

        add(decoded_instr);

        decoder = new Decoder();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Read opcode
        byte opcode = cpu_memory[cpu.registers.getPC() & 0xFFFF];
        this.opcode.setText(Common.byteToHexString(opcode, false));

        // Decode opcode
        Decoder.InstructionInfo info = decoder.decode_opcode(opcode);

        String decoded_instr = info.instr.toString();

        if (info.bytes == 1) {
            this.operand1.setText("--");
            this.operand2.setText("--");
        }
        else if (info.bytes == 2) {
            byte operand1 = cpu_memory[cpu.registers.getPC() + 1 & 0xFFFF];
            this.operand1.setText(Common.byteToHexString(operand1, false));
            this.operand2.setText("--");

            decoded_instr += " " + convert_1_operands_to_human_readable_text(info.addrmode, operand1);;
        } else if (info.bytes == 3) {
            byte operand1 = cpu_memory[cpu.registers.getPC() + 1 & 0xFFFF];
            byte operand2 = cpu_memory[cpu.registers.getPC() + 2 & 0xFFFF];
            this.operand1.setText(Common.byteToHexString(operand1, false));
            this.operand2.setText(Common.byteToHexString(operand2, false));

            decoded_instr += " " + convert_2_operands_to_human_readable_text(info.addrmode, operand1, operand2);;
        } else {
            throw new RuntimeException("Unexpected amount of bytes in instruction, must be at most 3");
        }

        this.decoded_instr.setText(decoded_instr);
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
                short relative_addr = (short) (cpu.registers.getPC() + operand1 + 2);
                return "$" + Common.shortToHexString(relative_addr, false);
            }
            default -> throw new RuntimeException("Not implemented yet");
        }
    }

    private String convert_2_operands_to_human_readable_text(Decoder.AddressingMode addrmode, byte operand1, byte operand2) {
        switch (addrmode) {
            case ABSOLUTE -> {
                // Little endian = switch order of operands that represent the address
                short addr = Common.makeShort(operand2, operand1);
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
