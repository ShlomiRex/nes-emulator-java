package NES.PPU;

import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PPURegisters {

    private final Logger logger = LoggerFactory.getLogger(PPURegisters.class);

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


    protected byte PPUCTRL, PPUMASK, PPUSTATUS, OAMADDR, OAMDATA, PPUDATA, OAMDMA;

    private short PPUADDR;

    /**
     * This flipflop is used to determine whether to write to high byte or low byte of PPUADDR or PPUSCROLL.
     * This bitflag is shared among PPUSCROLL and PPUADDR (this is what the real hardware does).
     *
     * Since to write to PPUADDR the CPU needs to write twice (once for high byte, once for low byte),
     * we need to keep track of which byte we are writing to.
     *
     * As for PPUSCROLL, This flipflop is used to determine whether to write to high byte (horizontal scroll offset)
     * or low byte (vertical scroll offset) of PPUSCROLL.
     */
    private boolean address_latch;

    /**
     * Current VRAM address (15 bits).
     */
    private LoopyRegister v;

    /**
     * Temporary VRAM address (15 bits).
     */
    private LoopyRegister t;

    /**
     * Fine X scroll (3 bits).
     */
    private byte fine_x_scroll;

    /**
     * This buffer is used to store the value of PPUDATA read from the PPU.
     * This is a real register inside the PPU, basically the PPU has 2 registers regarding PPUDATA.
     */
    private byte PPUDATA_read_buffer;

    private final PPU ppu;

    public PPURegisters(PPU ppu) {
        this.ppu = ppu;
    }

    public void reset() {
        PPUSTATUS = PPUCTRL = PPUMASK = 0;
    }

    /**
     * Clears bit 7 of PPUSTATUS.
     * Sets the PPUADDR flipflop to write high byte next time.
     * Sets the PPUSCROLL flipflop to write high byte next time.
     * @return PPUSTATUS before clearing bit 7
     */
    public byte readPPUSTATUS() {
        byte before = PPUSTATUS;

        // Clear vblank flag
        PPUSTATUS = Common.Bits.setBit(PPUSTATUS, 7, false);

        address_latch = false;
        return before;
    }

    public void writePPUMASK(byte value) {
        PPUMASK = value;
    }

    public void writeOAMADDR(byte value) {
        OAMADDR = value;
    }

    public void writeOAMDATA(byte value) {
        OAMDATA = value;
        OAMADDR++;
    }

    public void writePPUSCROLL(byte value) {
        if (address_latch) {
            // Second write (w=1)

        } else {
            // First write (w=0)

        }

        address_latch = !address_latch;
    }

    public void writePPUADDR(byte value) {
        if (address_latch) {
            // Write to low byte
            PPUADDR = (short) ((PPUADDR & 0xFF00) | (value & 0x00FF));
        } else {
            // Write to high byte
            PPUADDR = (short) ((PPUADDR & 0x00FF) | ((value & 0x00FF) << 8));
        }
        // Flip the flipflop
        address_latch = !address_latch;
    }

    public void writePPUDATA(byte value) {
        PPUDATA = value;

        ppu.write(PPUADDR, value);

        // Bit 2 of PPUCTRL determines whether to increment PPUADDR by 1 or 32 after each write to PPUDATA.
        PPUADDR += (short) (Common.Bits.getBit(PPUCTRL, 2) ? 32 : 1);

        // Wrap around to 0x3FFF.
        if (PPUADDR > 0x3FFF) {
            PPUADDR &= 0x3FFF;
        }
    }

    public byte readPPUDATA() {
        byte ret = PPUDATA_read_buffer;
        PPUDATA_read_buffer = ppu.read(PPUADDR);

        /*
        If address is in palette range, the data is not delayed.
        Source: https://www.nesdev.org/wiki/PPU_registers#The%20PPUDATA%20read%20buffer%20(post-fetch)
        When reading while the VRAM address is in the range 0–$3EFF (i.e., before the palettes), the read will return
        the contents of an internal read buffer.
        After the CPU reads and gets the contents of the internal buffer, the PPU will immediately update the
        internal buffer with the byte at the current VRAM address.
        Reading palette data from $3F00–$3FFF works differently. The palette data is placed immediately on the data bus,
        and hence no priming read is required. Reading the palettes still updates the internal buffer though
         */
        if (PPUADDR >= 0x3F00)
            ret = PPUDATA_read_buffer;

        // VRAM address increment per CPU read/write of PPUDATA
        // (0: add 1, going across; 1: add 32, going down)
        boolean vram_addr_increment = Common.Bits.getBit(PPUCTRL, 2);
        PPUADDR += (short) (vram_addr_increment ? 32 : 1);

        // Post fetch
        if (PPUADDR > 0x3FFF) {
            PPUADDR &= 0x3FFF;
        }
        return ret;
    }

    public void writeOAMDMA(byte value) {
        OAMDMA = value;
    }

    public byte readOamData() {
        return OAMDATA;
    }

    /**
     * Used only for testing, or debugging.
     */
    public byte getPPUCTRL() {
        return PPUCTRL;
    }

    /**
     * Used only for testing, or debugging, or internal PPU use.
     */
    public byte getPPUMASK() {
        return PPUMASK;
    }

    /**
     * Used only for testing, or debugging.
     */
    public byte getPPUSTATUS() {
        return PPUSTATUS;
    }

    /**
     * Used only for testing, or debugging.
     */
    public byte getOAMADDR() {
        return OAMADDR;
    }

    /**
     * Used only for testing, or debugging.
     */
    public byte getOAMDATA() {
        return OAMDATA;
    }

    //TODO: PPUSCROLL is not one register
//    /**
//     * Used only for testing, or debugging.
//     */
//    public short getPPUSCROLL() {
//        return PPUSCROLL;
//    }

    /**
     * Used only for testing, or debugging.
     */
    public short getPPUADDR() {
        return PPUADDR;
    }

    /**
     * Used only for testing, or debugging.
     */
    public byte getPPUDATA() {
        return PPUDATA;
    }

    /**
     * Used only for testing, or debugging.
     */
    public byte getOAMDMA() {
        return OAMDMA;
    }

    public void writePPUCTRL(byte value) {
        PPUCTRL = value;
    }
}
