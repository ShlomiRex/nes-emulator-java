package NES.UI.Debugger.AssemblyDebugger;

/**
 * Assembly line record that describes the assembly line.
 * @param addr Adress of the assembly line.
 * @param bytes Number of bytes used for the instruction.
 * @param opcode The opcode of the instruction.
 * @param operand1 Optional: The first operand of the instruction.
 * @param operand2 Optional: The second operand of the instruction.
 * @param instr_name The name of the instruction (SEI, LDA, etc.)
 */
public record AssemblyLineRecord(short addr,
                                 int bytes,
                                 byte opcode,
                                 Byte operand1,
                                 Byte operand2,
                                 String instr_name) {
}
