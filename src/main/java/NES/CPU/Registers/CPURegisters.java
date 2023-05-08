package NES.CPU.Registers;

import NES.Common;

public class CPURegisters {

    public byte A, X, Y, S;
    public short PC;

    public StatusFlags P;

    public CPURegisters() {
        P = new StatusFlags();
        reset();
    }

    @Override
    public String toString() {
        String a = "A: " + Common.byteToHexString(A, false);
        String x = "X: " + Common.byteToHexString(X, false);
        String y = "Y: " + Common.byteToHexString(Y, false);
        String s = "S: " + Common.byteToHexString(S, false);
        String pc = "PC: " + Common.shortToHexString(PC, false);
        String p = "P: NV-BDIZC " + Common.byteToBinaryString(P.getAllFlags());

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
        P.reset();
        S = (byte) 0xFF;
        PC = 0;
    }

    public void p_modify_n(byte value) {
        P.setNegative(Common.Bits.getBit(value, 7));
    }

    public void p_modify_z(byte value) {
        P.setZero(value == 0);
    }

}
