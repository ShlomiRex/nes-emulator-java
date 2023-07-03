package NES.PPU;

import NES.Common;

public class PPURegisters {

    /*
     * PPU Registers:
     * - $2000: PPUCTRL
     * - $2001: PPUMASK
     * - $2002: PPUSTATUS
     * - $2003: OAMADDR
     * - $2004: OAMDATA
     * - $2005: PPUSCROLL
     * - $2006: PPUADDR
     * - $2007: PPUDATA
     * - $4014: OAMDMA
     */


    private byte PPUCTRL, PPUMASK, PPUSTATUS, OAMADDR, OAMDATA, PPUSCROLL, PPUDATA, OAMDMA;

    private short PPUADDR;

    /**
     * This flipflop is used to determine whether to write to high byte or low byte of PPUADDR.
     * Since to write to PPUADDR the CPU needs to write twice (once for high byte, once for low byte),
     * we need to keep track of which byte we are writing to.
     */
    private boolean PPUADDR_flipflop_write_high = true;

    /**
     * This buffer is used to store the value of PPUDATA read from the PPU.
     * This is a real register inside the PPU, basically the PPU has 2 registers regarding PPUDATA.
     */
    private byte PPUDATA_read_buffer;

    private final VRAM vram;
    private final PaletteRAM palette_ram;

    public PPURegisters(VRAM vram, PaletteRAM palette_ram) {
        this.vram = vram;
        this.palette_ram = palette_ram;
    }

    public void reset() {
        PPUSTATUS = PPUCTRL = PPUMASK = 0;
    }

    /**
     * Clears bit 7 of PPUSTATUS.
     * Also sets the PPUADDR flipflop to write high byte next time.
     * @return PPUSTATUS before clearing bit 7
     */
    public byte readPPUSTATUS() {
        byte before = PPUSTATUS;
        PPUSTATUS = Common.Bits.setBit(PPUSTATUS, 7, false);
        PPUADDR_flipflop_write_high = true;
        return before;
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
        //TODO: Write twice
        PPUSCROLL = value;
    }

    public void writePPUADDR(byte value) {
        if (PPUADDR_flipflop_write_high) {
            // Write to high byte
            PPUADDR = (short) ((PPUADDR & 0x00FF) | ((value & 0x00FF) << 8));
        } else {
            // Write to low byte
            PPUADDR = (short) ((PPUADDR & 0xFF00) | (value & 0x00FF));

            // Mirror down to 14 bits (0x3FFF): https://www.nesdev.org/wiki/PPU_registers#Address_($2006)_%3E%3E_write_x2
            // TODO: Not sure if mirroring is done here or if it should be done in both high byte and low byte.
            PPUADDR &= 0x3FFF;
        }
        // Flip the flipflop
        PPUADDR_flipflop_write_high = !PPUADDR_flipflop_write_high;
    }

    public void writePPUDATA(byte value) {
        PPUDATA = value;

        if (PPUADDR > 0x3EFF) {
            // Palette RAM
            palette_ram.write(PPUADDR, value);
        } else {
            // VRAM
            vram.write(PPUADDR, value);
        }

        // Bit 2 of PPUCTRL determines whether to increment PPUADDR by 1 or 32 after each write to PPUDATA.
        PPUADDR += (short) (Common.Bits.getBit(PPUCTRL, 2) ? 32 : 1);

        // Wrap around to 0x3FFF.
        if (PPUADDR > 0x3FFF) {
            PPUADDR &= 0x3FFF;
        }
    }

    public byte readPPUDATA() {
        byte value = PPUDATA_read_buffer;
        PPUDATA_read_buffer = vram.read(PPUADDR);
        PPUADDR += (short) (Common.Bits.getBit(PPUCTRL, 2) ? 32 : 1);

        // Post fetch
        if (PPUADDR > 0x3FFF) {
            PPUADDR &= 0x3FFF;
        }
        return value;
    }

    public void setOamDma(byte value) {
        OAMDMA = value;
    }

    public byte readOamData() {
        return OAMDATA;
    }

    /**
     * Used only for testing, or debugging.
     */
    public short getPPUADDR() {
        return PPUADDR;
    }

    /**
     * Used only for testing, or debugging.
     */
    public byte getPPUSTATUS() {
        return PPUSTATUS;
    }
}
