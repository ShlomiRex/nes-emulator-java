package NES.CPU.Decoder;

public class AssemblyInfo {
    public final Decoder.InstructionInfo instr_info;

    public final String str_instr_bytes;

    public final String decoded_operand_or_symbol;
    public AssemblyInfo(Decoder.InstructionInfo info, String str_instr_bytes, String decoded_operand_or_symbol) {
        this.instr_info = info;
        this.str_instr_bytes = str_instr_bytes;
        this.decoded_operand_or_symbol = decoded_operand_or_symbol;
    }
}
