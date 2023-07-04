package NES.PPU;

import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class PPU {
    private static final int SCREEN_WIDTH = 256;
    private static final int SCREEN_HEIGHT = 240;

    private final Logger logger = LoggerFactory.getLogger(PPU.class);
    public final PPURegisters registers;

    /**
     * Contains 2 pattern tables, each is 4KB in size.
     * This memory translates to sprites.
     * Address: 0x0000 - 0x1FFF
     */
    private final byte[] chr_rom;

    /**
     * Contains 4 name tables, each is 1KB in size.
     * This memory translates to background / layout.
     * Address: 0x2000 - 0x2FFF
     */
    private final byte[] vram;

    /**
     * Contains 32 bytes, each byte is a color index (0,1,2,3).
     * This memory translates to colors.
     * Address: 0x3F00 - 0x3F1F
     */
    private final byte[] palette_ram;

    /**
     * PPU cycles. Reset to zero after 341 cycles.
     */
    public int cycle;
    /**
     * PPU scanlines. Reset to zero after 262 scanlines.
     */
    public int scanline;
    /**
     * PPU frames. Reset to zero after 60 frames.
     */
    public int frame;

    /**
     * The frame buffer is a 2D array of pixels and color. So its easy to access and draw.
     */
    private final Common.Pair<Boolean, Color>[][] frame_buffer;
    private Runnable trigger_game_canvas_repaint;

    public PPU(byte[] chr_rom, byte[] palette_ram) {
        if (chr_rom.length != 1024 * 8)
            throw new IllegalArgumentException("Unexpected CHR ROM / pattern table size");
        if (palette_ram.length != 32)
            throw new IllegalArgumentException("Unexpected palette RAM size");

        this.frame_buffer = new Common.Pair[SCREEN_HEIGHT][SCREEN_WIDTH];
        this.vram = new byte[1024 * 2];
        this.registers = new PPURegisters(this);
        this.chr_rom = chr_rom;
        this.palette_ram = palette_ram;

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
        System.arraycopy(chr_rom, (i & 0xFFFF), tile, 0, 16);
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
        // Simulate PPU updates with random data for demonstration
//        for (int i = 0; i < frameBuffer.length; i++) {
//            frameBuffer[i] = (byte) (Math.random() * 256); // Random pixel value between 0-255
//        }

        if (frame == 60) {
            frame = 0;
            return;
        }

        if (scanline == 262) {
            scanline = 0;
            frame ++;
            return;
        }

        if (cycle == 340) {
            cycle = 0;
            scanline ++;
            return;
        }

        // TODO: Decide what to do
//        if (scanline == 240 && cycle == 0) {
//            // The PPU is idle during this cycle, but FCEUX emulator renders frame at this exact time.
//            draw_frame();
//        }

        if (scanline == 241 && cycle == 1) {
            // VBlank start
            registers.setNmiEnabled(true);
            if (trigger_game_canvas_repaint != null)
                trigger_game_canvas_repaint.run();
        }

        if (scanline == 261 && cycle == 1) {
            // VBlank end
            registers.setNmiEnabled(false);
        }

        cycle ++;
    }

    public static Color getColorFromPalette(byte paletteIndex) {
        // Return the appropriate color based on the palette index

        // For example, a simple implementation that alternates between two colors:
        return (paletteIndex % 2 == 0) ? Color.WHITE : Color.GRAY;
    }

    /**
     * Called from within JavaSwing GUI thread.
     * @param g
     * @param width The container width
     * @param height The container height
     */
    public void draw_frame(Graphics g, int width, int height) {
        boolean show_background = Common.Bits.getBit(registers.getPPUMASK(), 3);
        boolean show_sprites = Common.Bits.getBit(registers.getPPUMASK(), 4);

        // Get pattern tables
        int left_pattern_table_index = 0x0000;
        int right_pattern_table_index = 0x1000;

        // Get nametables
        // A nametable is a 1024 byte area of memory used by the PPU to lay out backgrounds.
        // Each byte in the nametable controls one 8x8 pixel character cell
        int first_nametable_index = 0x2000 - 0x2000;
        int second_nametable_index = 0x2400 - 0x2000;
        int third_nametable_index = 0x2800 - 0x2000;
        int fourth_nametable_index = 0x2C00 - 0x2000;

        // Get attribute table
        // An attribute table is a 64-byte array at the end of each nametable that controls which palette is assigned to each part of the background.
        int first_attribute_table_index = first_nametable_index + 0x3C0;
        int second_attribute_table_index = second_nametable_index + 0x3C0;
        int third_attribute_table_index = third_nametable_index + 0x3C0;
        int fourth_attribute_table_index = fourth_nametable_index + 0x3C0;

        int pixel_width = 8;
        int pixel_height = 8;

        // For each nametable byte (960 bytes) - the remaining 64 bytes are attribute table bytes (for total of 1024)
        for (int row = 0; row < 30; row++) {
            for (int col = 0; col < 32; col++) {
                byte background_pattern_tile_index = vram[row * 32 + col];
                short full_pattern_index = (short) (right_pattern_table_index + background_pattern_tile_index); // Get from right pattern table (backgrounds)

                // Get the 8x8 pixel tile
                for (int pixel_row = 0; pixel_row < 8; pixel_row ++) {
                    for (int pixel_col = 0; pixel_col < 8; pixel_col++) {
                        byte pixelValue = chr_rom[full_pattern_index + pixel_row * 8 + pixel_col];
                        Color pixelColor = get_palette(pixelValue).getB();
                        g.setColor(pixelColor);
                        g.fillRect((col * 8 + pixel_col) * pixel_width, (row * 8 + pixel_row) * pixel_height, pixel_width, pixel_height);
                    }
                }
            }
        }

//        // Draw first pattern tile
//        for(int i = 0; i < 16; i++) {
//            chr_rom[pattern_index + i];
//            g.fillRect(i * pixel_width, 0, pixel_width, pixel_height);
//            frameBuffer[i] =
//        }

        // Nametable 0: 0x2000
        // Nametable 1: 0x2400
        // Nametable 2: 0x2800
        // Nametable 3: 0x2C00
    }


    /**
     * Write to PPU memory.
     * @param addr
     * @param value
     */
    public void write(short addr, byte value) {
        if (addr >= 0x0000 && addr <= 0x1FFF) {
            // CHR ROM / pattern table
            throw new RuntimeException("Cannot write to CHR ROM");
        } else if (addr >= 0x2000 && addr <= 0x2FFF) {
            // Name table
            vram[addr - 0x2000] = value;
        } else if (addr >= 0x3000 && addr <= 0x3EFF) {
            // Mirrors of 0x2000-0x2EFF
            vram[addr - 0x3000] = value;
        } else if (addr >= 0x3F00 && addr <= 0x3FFF) {
            // Palette RAM
            palette_ram[addr - 0x3F00] = value;
        }
    }

    /**
     * Read PPU memory.
     * @param addr
     * @return
     */
    public byte read(short addr) {
        if (addr >= 0x0000 && addr <= 0x1FFF) {
            // CHR ROM / pattern table
            return chr_rom[addr];
        } else if (addr >= 0x2000 && addr <= 0x2FFF) {
            // Name table
            return vram[addr - 0x2000];
        } else if (addr >= 0x3000 && addr <= 0x3EFF) {
            // Mirrors of 0x2000-0x2EFF
            return vram[addr - 0x3000];
        } else if (addr >= 0x3F00 && addr <= 0x3FFF) {
            // Palette RAM
            return palette_ram[addr - 0x3F00];
        } else {
            throw new RuntimeException("Invalid PPU memory address: " + addr);
        }
    }

    /**
     * Get palette color from palette RAM.
     * @param palette_index A number between 0-32
     * @return
     */
    public Common.Pair<Integer, Color> get_palette(int palette_index) {
        if (palette_index < 0 || palette_index > 32) {
            throw new IllegalArgumentException("Invalid palette index: " + palette_index);
        }

        Color[][] system_palette = SystemPallete.getSystemPallete();

        int color_index = palette_ram[palette_index];

        int row = color_index / 16;
        int col = color_index % 16;

        return new Common.Pair<>(color_index, system_palette[row][col]);
    }

    /**
     * Get nametable (1,2,3 or 4).
     * @param nametable_index A number between 1-4
     */
    public void get_nametable(int nametable_index) {
        if (nametable_index < 1 || nametable_index > 4) {
            throw new IllegalArgumentException("Invalid nametable index: " + nametable_index);
        }

    }

    public void addGameCanvasRepaintRunnable(Runnable runnable) {
        this.trigger_game_canvas_repaint = runnable;
    }
}
