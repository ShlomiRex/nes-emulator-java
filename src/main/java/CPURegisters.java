public class CPURegisters {

    public byte A, X, Y, S, P;
    public short PC;

    public CPURegisters() {
        reset();
    }

    @Override
    public String toString() {
        String a = "A: " + Common.byteToHexString(A);
        String x = "X: " + Common.byteToHexString(X);
        String y = "Y: " + Common.byteToHexString(Y);
        String s = "S: " + Common.byteToHexString(S);
        String pc = "PC: " + Common.shortToHexString(PC, false);
        String p = "P: NV-BDIZC " + Common.byteToBinaryString(P);

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
        S = (byte) 0xFF;
        PC = 0;
    }
}
