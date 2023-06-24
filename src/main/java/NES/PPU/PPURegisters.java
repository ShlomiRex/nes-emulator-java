package NES.PPU;

import NES.Common;

public class PPURegisters {

    private byte ctrl; // 0x2000
    private byte mask; // 0x2001
    private byte status; // 0x2002
    private byte oam_addr; // 0x2003
    private byte oam_data; // 0x2004
    private byte scroll; // 0x2005
    private byte addr; // 0x2006
    private byte data; // 0x2007

    public void reset() {
        status = ctrl = mask = oam_data = 0;
    }

    public byte readStatus() {
        // TODO: Clear VBlank
        byte status = this.status;
        this.status = Common.Bits.setBit(status, 7, false);
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

    @Override
    public String toString() {
        return String.format("status=%s, ctrl=%s, mask=%s",
                Common.byteToHex(status, true),
                Common.byteToHex(ctrl, true),
                Common.byteToHex(mask, true));
    }

    public byte readOAMData() {
        return oam_data;
    }

    public byte readData() {
        return data;
    }

    public void setCtrl(byte value) {
        ctrl = value;
    }

    public void setMask(byte value) {
        mask = value;
    }

    public void setOamAddr(byte value) {
        oam_addr = value;
    }

    public void setOamData(byte value) {
        oam_data = value;
    }

    public void setScroll(byte value) {
        scroll = value;
    }

    public void setAddr(byte value) {
        addr = value;
    }

    public void setData(byte value) {
        data = value;
    }
}
