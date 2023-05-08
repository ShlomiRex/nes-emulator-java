package NES.CPU.Registers;

import NES.Common;

public class StatusFlags {

    private byte p;

    public void reset() {
        p = 0b0010_0000; // Set 'UNUSED' flag to 1
    }

    public void setCarry(boolean value) {
        p = Common.Bits.setBit(p, 0, value);
    }

    public void setZero(boolean value) {
        p = Common.Bits.setBit(p, 1, value);
    }

    public void setInterruptDisable(boolean value) {
        p = Common.Bits.setBit(p, 2, value);
    }

    public void setDecimal(boolean value) {
        p = Common.Bits.setBit(p, 3, value);
    }

    public void setOverflow(boolean value) {
        p = Common.Bits.setBit(p, 6, value);
    }

    public void setNegative(boolean value) {
        p = Common.Bits.setBit(p, 7, value);
    }

    public boolean getCarry() {
        return Common.Bits.getBit(p, 0);
    }

    public boolean getZero() {
        return Common.Bits.getBit(p, 1);
    }

    public boolean getInterruptDisable() {
        return Common.Bits.getBit(p, 2);
    }

    public boolean getDecimal() {
        return Common.Bits.getBit(p, 3);
    }

    public boolean getB() {
        return Common.Bits.getBit(p, 4);
    }

    public boolean getUnused() {
        return Common.Bits.getBit(p, 5);
    }

    public boolean getOverflow() {
        return Common.Bits.getBit(p, 6);
    }

    public boolean getNegative() {
        return Common.Bits.getBit(p, 7);
    }

    public byte getAllFlags() {
        return p;
    }

}
