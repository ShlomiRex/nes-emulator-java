package NES.CPU.Decoder;

import NES.CPU.AddressingMode;
import NES.CPU.Instructions;
import NES.Common;

public class InstructionInfo {
    public final byte opcode;
    public final Instructions instr;
    public final AddressingMode addrmode;
    public final int bytes, cycles;
    public final Decoder.OopsCycle oopsCycle;
    public final boolean is_illegal;

    public InstructionInfo(byte opcode, Instructions instr, AddressingMode addrmode, int bytes, int cycles,
                           Decoder.OopsCycle oopsCycle, boolean is_illegal) {
        this.opcode = opcode;
        this.instr = instr;
        this.addrmode = addrmode;
        this.bytes = bytes;
        this.cycles = cycles;
        this.oopsCycle = oopsCycle;
        this.is_illegal = is_illegal;
    }

    public InstructionInfo(byte opcode, Instructions instr, AddressingMode addrmode, int bytes, int cycles,
                           Decoder.OopsCycle oopsCycle) {
        this(opcode, instr, addrmode, bytes, cycles, oopsCycle, false);
    }

    @Override
    public String toString() {
        return "InstructionInfo{" +
                Common.byteToHex(opcode, true) + ", " +
                instr +
                ", " + addrmode +
                ", bytes=" + bytes +
                ", cycles=" + cycles +
                ", oopsCycle=" + oopsCycle +
                ", is_illegal=" + is_illegal +
                '}';
    }
}