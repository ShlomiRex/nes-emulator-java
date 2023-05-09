package NES.UI.Debugger;

import NES.CPU.CPU;
import NES.CPU.Decoder;
import NES.Common;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class InstructionsPane extends JPanel {

    private CPU cpu;
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

        add(opcode);
        add(operand1);
        add(operand2);

        decoded_instr = new JTextField("LDX $#FF");
        decoded_instr.setEditable(false);
        add(decoded_instr);

        decoder = new Decoder();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Read opcode
        byte opcode = cpu_memory[cpu.registers.PC & 0xFFFF];
        this.opcode.setText(Common.byteToHexString(opcode, false));

        // Decode opcode
        Decoder.InstructionInfo info = decoder.decode_opcode(opcode);

        String decoded_instr = info.instr.toString();

        if (info.bytes == 1) {
            this.operand1.setText("--");
            this.operand2.setText("--");
        }
        else if (info.bytes == 2) {
            byte operand1 = cpu_memory[cpu.registers.PC + 1 & 0xFFFF];
            this.operand1.setText(Common.byteToHexString(operand1, false));
            this.operand2.setText("--");

            decoded_instr += " " + convert_operands_to_human_readable_text(info.addrmode, operand1);;
        } else if (info.bytes == 3) {
            byte operand1 = cpu_memory[cpu.registers.PC + 1 & 0xFFFF];
            byte operand2 = cpu_memory[cpu.registers.PC + 2 & 0xFFFF];
            this.operand1.setText(Common.byteToHexString(operand1, false));
            this.operand2.setText(Common.byteToHexString(operand2, false));
        } else {
            throw new RuntimeException("Unexpected amount of bytes in instruction, must be at most 3");
        }

        this.decoded_instr.setText(decoded_instr);
    }

    private String convert_operands_to_human_readable_text(Decoder.AddressingMode addrmode, byte operand1) {
        switch (addrmode) {
            case IMMEDIATE -> {
                return "#$"+Common.byteToHexString(operand1, false);
            }
            case RELATIVE -> {
                return ""; //TODO: Complete
            }
            default -> throw new RuntimeException("Not implemented yet");
        }
    }
}
