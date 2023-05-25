package NES.PPU;

import NES.CPU.CPU;
import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PPU {

    private final Logger logger = LoggerFactory.getLogger(PPU.class);
    public final PPURegisters registers;

    // Memory
    private final byte[] pattern_tables;

    // Renderer variables

    private int cycle;
    private int scanline;

    public PPU(byte[] pattern_tables) {
        this.registers = new PPURegisters();
        this.pattern_tables = pattern_tables;

        if (pattern_tables.length != 1024 * 8)
            throw new RuntimeException("Unexpected pattern table length");

        reset();
    }

    public void reset() {
        registers.reset();
        cycle = 0;
        scanline = 0;
    }

    public byte[] get_pattern_tile(byte tile_index, boolean is_left_table) {
        // Each pattern tile is 16 bytes in size. We jump by 16 bytes.
        // The tile index can be 0x0-0xFF, but the actual bytes needed are 0xFF times 16, which fits in u16.
        short i = (short) ((tile_index & 0xFF) * 16);

        if (!is_left_table)
            i += (16 * 0xFF);

        //TODO: This can cause regression problems. A lot of copying memory, each tile, for each frame?
        // For now I leave this as is
        byte[] tile = new byte[16];
        System.arraycopy(pattern_tables, (i & 0xFFFF), tile, 0, 16);
        return tile;
    }

    /**
     *
     * @param tile A tile (16 bytes), regular tile from CHR ROM.
     * @return A pixel pattern (8x8=64 pixels), each byte = pixel is color index (0,1,2,3).
     */
    public byte[][] convert_pattern_tile_to_pixel_pattern(byte[] tile) {
        byte[][] pixels = new byte[8][8]; // Each pixel is 1 byte with values 0,1,2 or 3. No more.

        // Loop over bit planes (each plane = 8 bytes)
        for(int i = 0; i < 8; i++) {
            byte bit_plane_1_byte = tile[i];
            byte bit_plane_2_byte = tile[8 + i];

            // Loop over byte bits
            for (int j = 0; j < 8; j++) {
                boolean bit_plane_1 = Common.Bits.getBit(bit_plane_1_byte, j);
                boolean bit_plane_2 = Common.Bits.getBit(bit_plane_2_byte, j);

                byte pixelValue = 0; // both are on
                if (bit_plane_1 && !bit_plane_2) {
                    pixelValue = 1; // bit in bit plane 1 is on, bit in bit plane 2 is off
                } else if (!bit_plane_1 && bit_plane_2) {
                    pixelValue = 2; // bit in bit plane 1 is off, bit in bit plane 2 is on
                } else if (bit_plane_1 && bit_plane_2) {
                    pixelValue = 3; // both are off
                }
                pixels[i][7 - j] = pixelValue;
            }
        }

        return pixels;
    }

    /**
     * Clock tick for PPU.
     */
    public void clock_tick() {
        logger.debug("PPU: Clock tick, cycle: "+cycle+", scanline: "+scanline);

        // Implement PPU clock cycle

        if (scanline >= 0 && scanline <= 239) {
            if (cycle == 0) {
                // Do pre-render scanline setup
            } else if (cycle >= 1 && cycle <= 256) {
                // Visible scanline
            } else if (cycle >= 257 && cycle <= 320) {
                // Sprite evaluation
            } else if (cycle >= 321 && cycle <= 336) {
                // Sprite loading
            } else if (cycle >= 337 && cycle <= 340) {
                // Background loading
            }
        } else if (scanline == 240) {
            if (cycle == 0) {
                // End of visible scanlines
            }
        } else if (scanline >= 241 && scanline <= 260) {
            if (scanline == 241 && cycle == 1) {
                // Start of vblank
                // Set vblank flag and generate NMI if enabled
            }
        }

        // Increment cycle and scanline counters
        cycle++;
        if (cycle > 340) {
            cycle = 0;
            scanline++;
            if (scanline > 260) {
                scanline = 0;
                logger.debug("PPU: Frame complete, cycle: "+cycle+", scanline: "+scanline);
                //frame++;
                //oddFrame = !oddFrame;
            }
        }
    }

    public void runFrame() {
        for(int i = 0; i < 241; i++) {
            clock_tick();
        }
    }
}
