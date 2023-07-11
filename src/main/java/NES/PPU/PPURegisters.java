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

    private short PPUADDR, PPUSCROLL;

    /**
     * This flipflop is used to determine whether to write to high byte or low byte of PPUADDR.
     * Since to write to PPUADDR the CPU needs to write twice (once for high byte, once for low byte),
     * we need to keep track of which byte we are writing to.
     */
    private boolean PPUADDR_flipflop_write_high = true;

    /**
     * This flipflop is used to determine whether to write to high byte (horizontal scroll offset)
     * or low byte (vertical scroll offset) of PPUSCROLL.
     */
    private boolean PPUSCROLL_flipflop_write_high = true;

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

        PPUADDR_flipflop_write_high = true;
        PPUSCROLL_flipflop_write_high = true;
        return before;
    }

    public void writePPUCTRL(byte value) {
        byte old_value = PPUCTRL;
        boolean old_nmi = Common.Bits.getBit(old_value, 7);
        boolean new_nmi = Common.Bits.getBit(value, 7);

        if (old_nmi && !new_nmi) {
            logger.info("Disabling 'Generate NMI' in PPUCTRL");
        } else {
            logger.info("Enabling 'Generate NMI' in PPUCTRL");
        }

        PPUCTRL = value;
    }

    public void writePPUMASK(byte value) {
        PPUMASK = value;
    }

    public void writePPUSTATUS(byte b) {
        PPUSTATUS = b;
    }

    public void writeOAMADDR(byte value) {
        OAMADDR = value;
    }

    public void writeOAMDATA(byte value) {
        OAMDATA = value;
        OAMADDR++;
    }

    public void writePPUSCROLL(byte value) {
        // TODO: Test this in FCEUX
        if (PPUSCROLL_flipflop_write_high) {
            // Write to high byte
            PPUSCROLL = (short) ((PPUSCROLL & 0x00FF) | ((value & 0x00FF) << 8));
        } else {
            // Write to low byte
            PPUSCROLL = (short) ((PPUSCROLL & 0xFF00) | (value & 0x00FF));
        }

        PPUSCROLL_flipflop_write_high = !PPUSCROLL_flipflop_write_high;
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
            // TODO: Do something about mirroring
            //PPUADDR &= 0x3FFF;
        }
        // Flip the flipflop
        PPUADDR_flipflop_write_high = !PPUADDR_flipflop_write_high;
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

    /**
     * Used only for testing, or debugging.
     */
    public short getPPUSCROLL() {
        return PPUSCROLL;
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
    public byte getPPUDATA() {
        return PPUDATA;
    }

    /**
     * Used only for testing, or debugging.
     */
    public byte getOAMDMA() {
        return OAMDMA;
    }
}
