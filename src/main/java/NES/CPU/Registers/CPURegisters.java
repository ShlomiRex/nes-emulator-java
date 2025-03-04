package NES.CPU.Registers;

import NES.Common;

public class CPURegisters {

    public byte A, X, Y, S, P;
    public short PC;

    public CPURegisters() {
        reset();
    }

    @Override
    public String toString() {
        String a = "A: " + Common.byteToHex(A, false);
        String x = "X: " + Common.byteToHex(X, false);
        String y = "Y: " + Common.byteToHex(Y, false);
        String s = "S: " + Common.byteToHex(S, false);
        String pc = "PC: " + Common.shortToHex(PC, false);
        String p = "P: NV-BDIZC " + Common.byteToBinary(P) + " (" + Common.byteToHex(P, true) + ")";

        StringBuilder sb = new StringBuilder();
        sb.append(a).append("\t");
        sb.append(x).append("\t");
        sb.append(y).append("\t");
        sb.append(s).append("\t");
        sb.append(pc).append("\t");
        sb.append(p);
        return sb.toString();
    }

    public void reset() {
        A = X = Y = 0;
        P = 0b0010_0000; // Set 'UNUSED' flag to 1
        S = (byte) 0xFD;
        PC = 0;
    }

    public void setFlag(Flags flag, boolean value) {
        P = Common.Bits.setBit(P, flag.getBit(), value);
    }

    public boolean getFlag(Flags flag) {
        return Common.Bits.getBit(P, flag.getBit());
    }



    public void modify_n(byte value) {
        setFlag(Flags.NEGATIVE, Common.Bits.getBit(value, 7));
    }

    public void modify_z(byte result) {
        setFlag(Flags.ZERO, result == 0);
    }

    /**
     * Adds three numbers together and sets carry bit (if result > 0xFF wrapping around, unsigned overflow occurred)
     * https://www.nesdev.org/wiki/Instruction_reference#ADC
     */
    public void modify_c(byte value1, byte value2, byte value3) {

        throw new RuntimeException("Not implemented yet");
    }

    /**
     * Adds three numbers together and sets overflow bit (if the result's sign is different from both A's and memory's, signed overflow (or underflow) occurred.)
     * https://www.nesdev.org/wiki/Instruction_reference#ADC
     */
    public void modify_v(byte value1, byte value2, byte value3) {
        throw new RuntimeException("Not implemented yet");
    }

}
