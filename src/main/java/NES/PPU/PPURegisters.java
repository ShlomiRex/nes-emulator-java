package NES.PPU;

import NES.Common;

public class PPURegisters {

    /**
     * $2000
     */
    private byte PPUCTRL;
    /**
     * $2001
     */
    private byte PPUMASK;
    /**
     * $2002
     */
    private byte PPUSTATUS;

    /**
     * $2003
     */
    private byte OAMADDR;

    /**
     * $2004
     */
    private byte OAMDATA;

    /**
     * $2005
     */
    private byte PPUSCROLL;

    /**
     * $2006
     */
    private byte PPUADDR;

    /**
     * $2007
     */
    private byte PPUDATA;

    /**
     * $4014
     */
    private byte OAMDMA;

    public void reset() {
        PPUSTATUS = PPUCTRL = PPUMASK = 0;
    }

    public byte readStatus() {
        // TODO: Clear VBlank
        return PPUSTATUS;
    }

    /**
     * Do not use in CPU. Only outside CPU.
     * In CPU, use this instead: `readStatus()`
     * @return
     */
    public byte getPPUSTATUS() {
        return PPUSTATUS;
    }

    public byte getPPUCTRL() {
        return PPUCTRL;
    }

    public byte getPPUMASK() {
        return PPUMASK;
    }

    public boolean isNmiEnabled() {
        //return (ctrl & 0x80) != 0;
        return Common.Bits.getBit(PPUSTATUS, 7);
    }

    public void setNmiEnabled(boolean enabled) {
        PPUSTATUS = Common.Bits.setBit(PPUSTATUS, 7, enabled);
//        if (enabled) {
//            ctrl |= 0x80;
//        } else {
//            ctrl &= 0x7F;
//        }
    }

    public void setPPUCTRL(byte value) {
        PPUCTRL = value;
    }

    public void setPPUMASK(byte value) {
        PPUMASK = value;
    }

    public void setOamAddr(byte value) {
        OAMADDR = value;
    }

    public void setOamData(byte value) {
        OAMDATA = value;
    }

    public void setScroll(byte value) {
        PPUSCROLL = value;
    }

    public void setAddr(byte value) {
        PPUADDR = value;
    }

    public void setData(byte value) {
        PPUDATA = value;
    }

    public void setOamDma(byte value) {
        OAMDMA = value;
    }

    public byte readOamData() {
        return OAMDATA;
    }

    public byte readData() {
        return PPUDATA;
    }
}
