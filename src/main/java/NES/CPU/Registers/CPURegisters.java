package NES.CPU.Registers;

import NES.Common;
import NES.UI.Debugger.CPUDebugger.RegistersPanel;

import java.beans.PropertyChangeEvent;

public class CPURegisters {

    private byte A, X, Y, S;
    private short PC;

    private StatusFlags P;

    public CPURegisters() {
        P = new StatusFlags();
        reset();
    }

    @Override
    public String toString() {
        String a = "A: " + Common.byteToHex(A, false);
        String x = "X: " + Common.byteToHex(X, false);
        String y = "Y: " + Common.byteToHex(Y, false);
        String s = "S: " + Common.byteToHex(S, false);
        String pc = "PC: " + Common.shortToHex(PC, false);
        String p = "P: NV-BDIZC " + Common.byteToBinary(P.getAllFlags()) + " (" + Common.byteToHex(P.getAllFlags(), true) + ")";

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

    public short getPC() {
        return PC;
    }

    public byte getA() {
        return A;
    }

    public byte getX() {
        return X;
    }

    public byte getY() {
        return Y;
    }

    public byte getS() {
        return S;
    }

    public StatusFlags getP() {
        return P;
    }

    public void setA(byte A) {
        this.A = A;
    }

    public void setX(byte X) {
        this.X = X;
    }

    public void setY(byte Y) {
        this.Y = Y;
    }

    public void setP(StatusFlags P) {
        this.P = P;
    }

    public void setPC(short PC) {
        this.PC = PC;
    }

    public void setS(byte s) {
        this.S = s;
    }

    public void incrementPC() {
        PC++;
    }
}
