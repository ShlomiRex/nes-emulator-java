package NES.PPU;

import NES.Common;

public class PPURegisters {

    private byte status;
    private byte ctrl;
    private byte mask;

    public void reset() {
        status = ctrl = mask = 0;
    }

    public byte readStatus() {
        // TODO: Clear VBlank
        return status;
    }

    /**
     * Do not use in CPU. Only outside CPU.
     * In CPU, use this instead: `readStatus()`
     * @return
     */
    public byte getStatus() {
        return status;
    }

    public byte getCtrl() {
        return ctrl;
    }

    public byte getMask() {
        return mask;
    }

    public boolean isNmiEnabled() {
        //return (ctrl & 0x80) != 0;
        return Common.Bits.getBit(status, 7);
    }

    public void setNmiEnabled(boolean enabled) {
        status = Common.Bits.setBit(status, 7, enabled);
//        if (enabled) {
//            ctrl |= 0x80;
//        } else {
//            ctrl &= 0x7F;
//        }
    }

    public void setCtrl(byte value) {
        ctrl = value;
    }

    public void setMask(byte value) {
        mask = value;
    }

    public void setOamAddr(byte value) {
        throw new RuntimeException("not implemented yet");
    }

    public void setOamData(byte value) {
        throw new RuntimeException("not implemented yet");
    }

    public void setScroll(byte value) {
        throw new RuntimeException("not implemented yet");
    }

    public void setAddr(byte value) {
        throw new RuntimeException("not implemented yet");
    }

    public void setData(byte value) {
        throw new RuntimeException("not implemented yet");
    }

    public void setOamDma(byte value) {
        throw new RuntimeException("not implemented yet");
    }

    public byte readOamData() {
        throw new RuntimeException("not implemented yet");
    }

    public byte readData() {
        throw new RuntimeException("not implemented yet");
    }
}
