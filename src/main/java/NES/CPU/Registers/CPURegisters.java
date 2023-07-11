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

    public void setA(byte A) {
        this.A = A;
    }

    public void setX(byte X) {
        this.X = X;
    }

    public void setY(byte Y) {
        this.Y = Y;
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

    public void setInterruptDisable(boolean b) {
        P = Common.Bits.setBit(P, 2, b);
    }




    public boolean getCarry() {
        return Common.Bits.getBit(P, 0);
    }
    public boolean getZero() {
        return Common.Bits.getBit(P, 1);
    }
    public boolean getInterruptDisable() {
        return Common.Bits.getBit(P, 2);
    }
    public boolean getDecimal() {
        return Common.Bits.getBit(P, 3);
    }
    public boolean getBreak() {
        return Common.Bits.getBit(P, 4);
    }
    public boolean getUnused() {
        return Common.Bits.getBit(P, 5);
    }
    public boolean getNegative() {
        return Common.Bits.getBit(P, 7);
    }
    public boolean getOverflow() {
        return Common.Bits.getBit(P, 6);
    }



    public void modify_n(byte value) {
        setFlag(Flags.NEGATIVE, Common.Bits.getBit(value, 7));
    }

    public void modify_z(byte result) {
        setFlag(Flags.ZERO, result == 0);
    }




}
