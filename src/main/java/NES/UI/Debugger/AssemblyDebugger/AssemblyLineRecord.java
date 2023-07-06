package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.AddressingMode;

/**
 * Assembly line record that describes the assembly line.
 * @param addr Adress of the assembly line.
 * @param bytes Number of bytes used for the instruction.
 * @param opcode The opcode of the instruction.
 * @param operand1 Optional (nullable): The first operand of the instruction.
 * @param operand2 Optional (nullable): The second operand of the instruction.
 * @param instr_name Must (Not nullable): The name of the instruction (SEI, LDA, etc.)
 */
public record AssemblyLineRecord(short addr,
                                 int bytes,
                                 byte opcode,
                                 Byte operand1,
                                 Byte operand2,
                                 String instr_name,
                                 AddressingMode addressingMode) {
}
