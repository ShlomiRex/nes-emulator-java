package NES.Assembler;

import NES.CPU.AddressingMode;
import NES.CPU.Decoder.Decoder;
import NES.CPU.Decoder.InstructionInfo;
import NES.CPU.Instructions;
import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Assembler {
    private final static Logger logger = LoggerFactory.getLogger(Assembler.class);

    private enum OperandStartsWith {
        Hashtag,
        HashtagDollar,
        Dollar, Nothing, HashtagPercent
    }

    public static byte[] assemble(String[] program) {
        logger.debug("Assembling program...");

        byte[] output = new byte[program.length * 3]; // We assume that each line will be 3 bytes long but then we will resize it.
        int output_length = 0;

        for (String asm_line : program) {
            logger.debug("Processing: " + asm_line);

            byte[] bytes = line_assemble(asm_line);
            int length = bytes.length;

            output_length += length;
        }

        // Resize the output array
        output = Arrays.copyOf(output, output_length);

        return output;
    }

    private static byte[] line_assemble(String asm_line) {
        // Output
        byte[] output = new byte[3];
        int output_length_in_bytes = 0;

        // Preprocess
        asm_line = asm_line.trim().toUpperCase();

        // Read the instruction
        Instructions instruction = null;
        for (Instructions inst : Instructions.values()) {
            if (asm_line.startsWith(inst.toString())) {
                instruction = inst;
            }
        }
        if (instruction == null) {
            throw new IllegalArgumentException("Unknown instruction: " + asm_line);
        }

        // Find all the instruction that begins with the same instruction (LDA for example has 8 different addressing modes)
        ArrayList<InstructionInfo> candidates = new ArrayList<>();
        for (int i = 0; i < Decoder.instructions_table.length; i++) {
            InstructionInfo ii = Decoder.instructions_table[i];
            if (ii != null && ii.instr == instruction) {
                candidates.add(ii);
                logger.debug("Found candidate: " + Common.byteToHex((byte) i, true));
            }
        }
        logger.debug("Total candidates: " + candidates.size());



        // Eliminate all candidates until we reach one



        // Offset in string of the instruction substring and space to reach operand
        int offset = instruction.toString().length() + 1; // We skip the instruction and the space

        OperandStartsWith operandStartsWith = null;
        String operand_value = null;

        if (asm_line.startsWith("#$", offset)) {
            operandStartsWith = OperandStartsWith.HashtagDollar;
            offset += 2;
        } else if (asm_line.startsWith("$", offset)) {
            operandStartsWith = OperandStartsWith.Dollar;
            offset += 1;
        } else if (asm_line.startsWith("#%", offset)) {
            operandStartsWith = OperandStartsWith.HashtagPercent;
            offset += 2;
        } else if (asm_line.startsWith("#", offset)) {
            operandStartsWith = OperandStartsWith.Hashtag;
            offset += 1;

            // Expect two digits after hashtag
            if (!Character.isDigit(asm_line.charAt(offset)) || !Character.isDigit(asm_line.charAt(offset + 1))) {
                throw new IllegalArgumentException("Expected two digits after #");
            }
            operand_value = asm_line.substring(offset, offset + 2);
            offset += 2;

            logger.debug("Operand value: 0x" + operand_value);

            // Expect we reached end of string
            if (offset != asm_line.length()) {
                throw new IllegalArgumentException("Expected end of string after operand value");
            }

            output[1] = (byte) Integer.parseInt(operand_value, 16);
            output_length_in_bytes = 2;
        } else {
            operandStartsWith = OperandStartsWith.Nothing;

            // Expect two digits after hashtag
            if (!Character.isDigit(asm_line.charAt(offset)) || !Character.isDigit(asm_line.charAt(offset + 1))) {
                throw new IllegalArgumentException("Expected two digits after #");
            }

            operand_value = asm_line.substring(offset, offset + 2);
            offset += 2;

            logger.debug("Operand value: 0x" + operand_value);

            // Expect we reached end of string
            if (offset != asm_line.length()) {
                throw new IllegalArgumentException("Expected end of string after operand value");
            }

            output[1] = (byte) Integer.parseInt(operand_value, 16);
        }


        switch(operandStartsWith){
            case Hashtag:
            case HashtagDollar:
            case Dollar:
            case HashtagPercent:
                for (InstructionInfo ii : candidates) {
                    if (ii.addrmode == AddressingMode.IMMEDIATE) {
                        output[0] = ii.opcode;
                        break;
                    }
                }
                break;
            case Nothing:
                for (InstructionInfo ii : candidates) {
                    if (ii.addrmode == AddressingMode.ZEROPAGE) {
                        output[0] = ii.opcode;
                        break;
                    }
                }
                break;
        }

        // Check if we have ',' in the operand after offset
        boolean operand_is_indexed = false;
        int comma_offset = asm_line.indexOf(',', offset);
        if (comma_offset != -1) {
            operand_is_indexed = true;
        }

        // Keep the candidates that match index mode
        ArrayList<InstructionInfo> new_candidates = new ArrayList<>();
        if (operand_is_indexed) {
            for (InstructionInfo ii : candidates) {
                if (is_indexed(ii)) {
                    new_candidates.add(ii);
                    logger.debug("Found new candidate: " + Common.byteToHex(ii.opcode, true));
                }
            }
        } else {
            for (InstructionInfo ii : candidates) {
                if (!is_indexed(ii)) {
                    new_candidates.add(ii);
                    logger.debug("Found new candidate: " + Common.byteToHex(ii.opcode, true));
                }
            }
        }
        candidates = new_candidates; // Update candidates
        logger.debug("Total candidates after indexed check: " + candidates.size());

        return output;
    }

    private static boolean is_indexed(InstructionInfo ii) {
        return ii.addrmode == AddressingMode.ABSOLUTE_X ||
                ii.addrmode == AddressingMode.ABSOLUTE_Y ||

                ii.addrmode == AddressingMode.INDIRECT_X ||
                ii.addrmode == AddressingMode.INDIRECT_Y ||

                ii.addrmode == AddressingMode.ZEROPAGE_X ||
                ii.addrmode == AddressingMode.ZEROPAGE_Y;
    }
}