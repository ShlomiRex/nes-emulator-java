package NES.CPU.Decoder;

import NES.CPU.AddressingMode;
import NES.CPU.Instructions;

public class InstructionInfo {
    public final byte inst_byte;
    public final Instructions instr;
    public final AddressingMode addrmode;
    public final int bytes, cycles;
    public final Decoder.OopsCycle oopsCycle;
    public final boolean is_illegal;

    public InstructionInfo(byte inst_byte, Instructions instr, AddressingMode addrmode, int bytes, int cycles,
                           Decoder.OopsCycle oopsCycle, boolean is_illegal) {
        this.inst_byte = inst_byte;
        this.instr = instr;
        this.addrmode = addrmode;
        this.bytes = bytes;
        this.cycles = cycles;
        this.oopsCycle = oopsCycle;
        this.is_illegal = is_illegal;
    }

    public InstructionInfo(byte inst_byte, Instructions instr, AddressingMode addrmode, int bytes, int cycles,
                        Decoder.OopsCycle oopsCycle) {
        this(inst_byte, instr, addrmode, bytes, cycles, oopsCycle, false);
    }

    @Override
    public String toString() {
        return "InstructionInfo{" +
                instr +
                ", " + addrmode +
                ", bytes=" + bytes +
                ", cycles=" + cycles +
                ", oopsCycle=" + oopsCycle +
                ", is_illegal=" + is_illegal +
                '}';
    }
}