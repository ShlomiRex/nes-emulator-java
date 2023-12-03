package NES.PPU;

import NES.Bus.Bus;
import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

public class PPU {
    private final Logger logger = LoggerFactory.getLogger(PPU.class);
    public final PPURegisters registers;

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

    private Runnable trigger_game_canvas_repaint;

    protected Bus bus;

    /**
     * The index color model is used in buffered image to map the color index to the actual color.
     * This is an optimization. Instead of directly drawing pixels with 'g.fillRect'.
     */
    private final IndexColorModel indexColorModel;

    /**
     * The buffered image that will be drawn each frame.
     * This is an optimization. Instead of directly drawing pixels with 'g.fillRect'.
     */
    private final BufferedImage bufferedImage;

    /**
     * The color of each pixel in the buffered image. Note: the size of this array is 1,
     * since we use indexed color model. The value is the index of the color.
     */
    private final int[] buffered_pixel_color;

    /**
     * Object Attribute Memory
     * Contains 256 bytes, each byte determines how sprites are rendered
     * Contains 64 sprites, each sprite is 4 bytes in size.
     * Address: 0x3F00 - 0x3F1F
     */
    protected final byte[] oam;

    public PPU() {
//        if (chr_rom.length != 1024 * 8)
//            throw new IllegalArgumentException("Unexpected CHR ROM / pattern table size");

        this.registers = new PPURegisters(this);

        this.oam = new byte[64 * 4];

        byte[] red = new byte[64];
        byte[] green = new byte[64];
        byte[] blue = new byte[64];

        int paletteIndex = 0;
        for (Color c : Bus.SYSTEM_PALETTE) {
            red[paletteIndex] = (byte) c.getRed();
            green[paletteIndex] = (byte) c.getGreen();
            blue[paletteIndex] = (byte) c.getBlue();
            paletteIndex++;
        }

        /*
        Each pixel color is 2 bits (0, 1, 2 or 3), which is index inside specific palette.
        There are 64 colors in total.
        Each color is represented by 3 bytes (red, green, blue).
         */
        this.indexColorModel = new IndexColorModel(2, 64, red, green, blue);
        this.bufferedImage = new BufferedImage(256, 240, BufferedImage.TYPE_BYTE_INDEXED, indexColorModel);
        /*
         * Each pixel's final color is a single index (0-63) from the system palette.
         */
        this.buffered_pixel_color = new int[1];

        reset();
    }

    public void reset() {
        registers.reset();
        cycle = 0;
        scanline = 0;
    }

    /**
     * Clock tick for PPU.
     */
    public void clock_tick() {
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

        if (scanline == -1 && cycle == 1) {
            // Pre-render scanline

            // Clear vblank flag
            registers.PPUSTATUS = Common.Bits.setBit(registers.PPUSTATUS, 7, false);
        }

        if (scanline == 241 && cycle == 1) {
            // VBlank start

            //logger.debug("VBlank start");

            registers.PPUSTATUS = Common.Bits.setBit(registers.PPUSTATUS, 7, true);

            /**
             * Bit 7 of PPUCTRL: Generate an NMI at the start of the vertical blanking interval (0: off; 1: on)
             * The PPUCTRL controls the NMI line.
             */
            if (Common.Bits.getBit(registers.getPPUCTRL(), 7)) {
                bus.nmi_line = true;
                //logger.debug("Generating NMI interrupt");
            } else {
                //logger.debug("Not generating NMI interrupt");
            }

            // Repaint the game canvas
            if (trigger_game_canvas_repaint != null)
                trigger_game_canvas_repaint.run();
        }

        // Scanline 261: Post render scanline
        if (scanline == 261 && cycle == 1) {
            // VBlank end
            registers.PPUSTATUS = Common.Bits.setBit(registers.PPUSTATUS, 7, false);
        }

        cycle ++;
    }

    /**
     * Called from within JavaSwing GUI thread.
     * @param g
     * @param width The container width
     * @param height The container height
     */
    public void draw_frame(Graphics g, int width, int height) {
        // Draw backgrounds
        for (int tile_row = 0; tile_row < 30; tile_row++) {
            for (int tile_col = 0; tile_col < 32; tile_col++) {
                draw_tile(tile_row, tile_col);
            }
        }

        // Draw sprites
        boolean bank = Common.Bits.getBit(registers.PPUCTRL, 3);
        boolean sprite_size = Common.Bits.getBit(registers.PPUCTRL, 5);
        for (int sprite_index = 0; sprite_index < 64; sprite_index++) {
            draw_sprite(sprite_index, bank, sprite_size);
        }

        g.drawImage(bufferedImage, 0, 0, width, height, null);
    }

    /**
     *
     * @param sprite_index Index of sprite in OAM (0-63)
     * @param bank (0: $0000; 1: $1000; ignored in 8x16 mode)
     * @param sprite_size (0: 8x8; 1: 8x16)
     */
    private void draw_sprite(int sprite_index, boolean bank, boolean sprite_size) {
        // Each sprite is 8x8 pixels and is 4 bytes in size.
        int sprite_y = oam[sprite_index * 4] & 0xFF;
        int tile_index = oam[sprite_index * 4 + 1] & 0xFF;
        byte attributes = oam[sprite_index * 4 + 2];
        int sprite_x = oam[sprite_index * 4 + 3] & 0xFF;

//        // Check offscreen - don't render this sprite if offscreen
//        if (sprite_y >= 239)
//            return;

        // Attributes - bit 2,3,4 ignored
        int palette_id = attributes & 0b11;
        // TODO: Finish sprite priority
        boolean priority = Common.Bits.getBit(attributes, 5); // (0: in front of background; 1: behind background)
        boolean flip_horizontal = Common.Bits.getBit(attributes, 6);
        boolean flip_vertical = Common.Bits.getBit(attributes, 7);

        short tile_base_addr = (short) ((bank? 0x1000 : 0) + tile_index * 16);
        for (int pixel_row = 0; pixel_row < 8; pixel_row++) {
            // Read 2 bitplanes (8 bits per bitplane) from pattern table
            byte tile_lsb = read((short) (tile_base_addr + pixel_row));
            byte tile_msb = read((short) (tile_base_addr + pixel_row + 8));

            for (int pixel_col = 0; pixel_col < 8; pixel_col++) {
                // Get pixel value (color) from bitplanes (the value must be between 0-3 since we add 2 bits and each can be 0 or 1)
                byte colorIndex = (byte) ((tile_lsb & 1) + (tile_msb & 1) * 2);

                tile_lsb >>= 1;
                tile_msb >>= 1;

                // Do not draw the pixel - else, it will overwrite the background
                if (colorIndex == 0)
                    continue;

                // We read from palettes 4 through 7 (sprite palettes). 0x3F00 is base palette address.
                // We skip the first 4 palettes (each palette is 4 bytes).
                // And then we select which sprite palette to use using the attribute palette_id.
                // Then we select the color from the palette by using the colorIndex.
                byte pixelColor = read((short) (0x3F00 + 4*4 + palette_id * 4 + colorIndex));

                int color_row = pixelColor / 16;
                int color_col = pixelColor % 16;

                int color_index_in_system_palette = color_row * 16 + color_col;

                buffered_pixel_color[0] = color_index_in_system_palette;

                int pixel_x;
                int pixel_y;

                if (flip_horizontal)
                    pixel_x = sprite_x + pixel_col;
                else
                    pixel_x = sprite_x + (7- pixel_col);

                if (flip_vertical)
                    pixel_y = sprite_y + (7-pixel_row);
                else
                    pixel_y = sprite_y + pixel_row;

                // TODO: Not sure if to put this check here
                if (pixel_x > 255 || pixel_y > 239)
                    continue;

                bufferedImage.getRaster().setPixel(pixel_x, pixel_y, buffered_pixel_color);
            }
        }
    }

    public void draw_tile(Graphics g, int container_width, int container_height, int tile_row, int tile_col) {
        // Determine base addresses

        //same
//        int nametable_addr = (registers.PPUCTRL & 0b11) == 0 ? 0x2000 : 0x2400;
        int nametable_addr = (registers.loopy_t.nametable_select & 0b11) == 0 ? 0x2000 : 0x2400;

        short attributetable_addr = (short) (nametable_addr + 0x3C0);
        short pattern_table_addr = (short) ((registers.PPUCTRL & 0b10000) == 0 ? 0x0000 : 0x1000);

        // Read nametable byte - this is the index of the tile in the pattern table. This index points to 16 bytes of pattern data (2 bitmap planes).
        byte patternIndex = read((short) (nametable_addr + tile_row * 32 + tile_col));

        // Read corresponding attribute table byte - this is the palette index of 4x4 tiles.
        byte attributeByte = read((short) (attributetable_addr + ((tile_row / 4) * 8) + (tile_col / 4)));

        /*
        Get the corresponding palette index from attribute byte - 2 bits per tile, the byte represents 4x4 tiles.
        Bit 0,1 - top left tile, 2,3 - top right tile, 4,5 - bottom left tile, 6,7 - bottom right tile:
        -------------
        | 0 1 | 2 3 |
        -------------
        | 4 5 | 6 7 |
        -------------
         */
        int bitOffset = (tile_row % 4 / 2) * 2 + (tile_col % 4 / 2);
        int paletteIndex = (attributeByte >> (bitOffset * 2)) & 0b11;

        // Read 16 bytes from pattern table - this will form the tile pixels, and their colors, which are chosen from the palette.
        // To avoid copying 16 bytes (for regression reasons), we can just loop over each row, and do this bitmap calculation for each row.

        short tile_base_addr = (short) (pattern_table_addr + (patternIndex & 0xFF) * 16);
        for (int pixel_row = 0; pixel_row < 8; pixel_row ++) {
            // Read 2 bitplanes (8 bits per bitplane) from pattern table
            byte tile_lsb = read((short) (tile_base_addr + pixel_row));
            byte tile_msb = read((short) (tile_base_addr + pixel_row + 8));

            for (int pixel_col = 0; pixel_col < 8; pixel_col++) {
                // Get pixel value (color) from bitplanes (the value must be between 0-3 since we add 2 bits and each can be 0 or 1)
                byte colorIndex = (byte) ((tile_lsb & 1) + (tile_msb & 1) * 2);
                tile_lsb >>= 1;
                tile_msb >>= 1;

                // Now we have the pixel value, we can get the color from the palette
                // Read pixel color from palette RAM
                byte pixelColor = read((short) (0x3F00 + paletteIndex * 4 + colorIndex));

                int color_row = pixelColor / 16;
                int color_col = pixelColor % 16;
                int pixel_x = tile_col * 8 + (7- pixel_col);
                int pixel_y = tile_row * 8 + pixel_row;

                int color_index_in_system_palette = color_row * 16 + color_col;

                g.setColor(Bus.SYSTEM_PALETTE[color_index_in_system_palette]);
                g.drawRect(pixel_col, pixel_row, container_width / 8, container_height / 8);
//
//                buffered_pixel_color[0] = color_index_in_system_palette;
//                bufferedImage.getRaster().setPixel(pixel_x, pixel_y, buffered_pixel_color);
            }
        }
    }

    private void draw_tile(int tile_row, int tile_col) {
        // Determine base addresses

        //same
//        int nametable_addr = (registers.PPUCTRL & 0b11) == 0 ? 0x2000 : 0x2400;
        int nametable_addr = (registers.loopy_t.nametable_select & 0b11) == 0 ? 0x2000 : 0x2400;

        short attributetable_addr = (short) (nametable_addr + 0x3C0);
        short pattern_table_addr = (short) ((registers.PPUCTRL & 0b10000) == 0 ? 0x0000 : 0x1000);

        // Read nametable byte - this is the index of the tile in the pattern table. This index points to 16 bytes of pattern data (2 bitmap planes).
        byte patternIndex = read((short) (nametable_addr + tile_row * 32 + tile_col));

        // Read corresponding attribute table byte - this is the palette index of 4x4 tiles.
        byte attributeByte = read((short) (attributetable_addr + ((tile_row / 4) * 8) + (tile_col / 4)));

        /*
        Get the corresponding palette index from attribute byte - 2 bits per tile, the byte represents 4x4 tiles.
        Bit 0,1 - top left tile, 2,3 - top right tile, 4,5 - bottom left tile, 6,7 - bottom right tile:
        -------------
        | 0 1 | 2 3 |
        -------------
        | 4 5 | 6 7 |
        -------------
         */
        int bitOffset = (tile_row % 4 / 2) * 2 + (tile_col % 4 / 2);
        int paletteIndex = (attributeByte >> (bitOffset * 2)) & 0b11;

        // Read 16 bytes from pattern table - this will form the tile pixels, and their colors, which are chosen from the palette.
        // To avoid copying 16 bytes (for regression reasons), we can just loop over each row, and do this bitmap calculation for each row.

        short tile_base_addr = (short) (pattern_table_addr + (patternIndex & 0xFF) * 16);
        for (int pixel_row = 0; pixel_row < 8; pixel_row ++) {
            // Read 2 bitplanes (8 bits per bitplane) from pattern table
            byte tile_lsb = read((short) (tile_base_addr + pixel_row));
            byte tile_msb = read((short) (tile_base_addr + pixel_row + 8));

            for (int pixel_col = 0; pixel_col < 8; pixel_col++) {
                // Get pixel value (color) from bitplanes (the value must be between 0-3 since we add 2 bits and each can be 0 or 1)
                byte colorIndex = (byte) ((tile_lsb & 1) + (tile_msb & 1) * 2);
                tile_lsb >>= 1;
                tile_msb >>= 1;

                // Now we have the pixel value, we can get the color from the palette
                // Read pixel color from palette RAM
                byte pixelColor = read((short) (0x3F00 + paletteIndex * 4 + colorIndex));

                int color_row = pixelColor / 16;
                int color_col = pixelColor % 16;
                int pixel_x = tile_col * 8 + (7- pixel_col);
                int pixel_y = tile_row * 8 + pixel_row;

                int color_index_in_system_palette = color_row * 16 + color_col;

                buffered_pixel_color[0] = color_index_in_system_palette;
                bufferedImage.getRaster().setPixel(pixel_x, pixel_y, buffered_pixel_color);
            }
        }
    }

    /**
     * Write to PPU memory.
     * @param addr
     * @param value
     */
    public void write(short addr, byte value) {
        bus.ppuBus.ppu_write(addr, value);
    }

    /**
     * Read PPU memory.
     * @param addr
     * @return
     */
    public byte read(short addr) {
        return bus.ppuBus.ppu_read(addr);
    }

    /**
     * Get palette color from palette RAM.
     * @param palette_index Palette index (0-32)
     * @return The color index and the color
     */
    public Common.Pair<Integer, Color> get_palette(int palette_index) {
        if (palette_index < 0 || palette_index > 32) {
            throw new IllegalArgumentException("Invalid palette index: " + palette_index);
        }

        Color[] system_palette = SystemPallete.getSystemPalette();

        short palette_ram_addr = (short) (0x3F00 + palette_index);

        int color_index = read(palette_ram_addr) & 0xFF;

        int row = color_index / 16;
        int col = color_index % 16;

        return new Common.Pair<>(color_index, system_palette[row * 16 + col]);
    }

    public void addGameCanvasRepaintRunnable(Runnable runnable) {
        this.trigger_game_canvas_repaint = runnable;
    }

    public void attachBus(Bus bus) {
        this.bus = bus;
    }

    public void set_pattern_tile(int tile_index, boolean is_left_nametable, int[][] dest_pattern) {
        // Each pattern tile is 16 bytes in size. We jump by 16 bytes.
        // The tile index can be 0x0-0xFF, but the actual bytes needed are 0xFF times 16, which fits in u16.
        short addr = (short) ((tile_index & 0xFF) * 16);
        if (!is_left_nametable)
            addr += (16 * 0xFF);

        // The pattern in bit planes (each plane = 8 bytes)
        byte[] tile = new byte[16];
        System.arraycopy(bus.ppuBus.chr_rom, (addr & 0xFFFF), tile, 0, 16);

        // Set final pixels
        for (int row = 0; row < 8; row++) {
            byte bit_plane_1_byte = tile[row];
            byte bit_plane_2_byte = tile[8 + row];

            for (int col = 0; col < 8; col++) {
                boolean bit_plane_1 = Common.Bits.getBit(bit_plane_1_byte, col);
                boolean bit_plane_2 = Common.Bits.getBit(bit_plane_2_byte, col);
                int pixelValue = 0; // both are on
                if (bit_plane_1 && !bit_plane_2) {
                    pixelValue = 1; // bit in bit plane 1 is on, bit in bit plane 2 is off
                } else if (!bit_plane_1 && bit_plane_2) {
                    pixelValue = 2; // bit in bit plane 1 is off, bit in bit plane 2 is on
                } else if (bit_plane_1 && bit_plane_2) {
                    pixelValue = 3; // both are off
                }

                dest_pattern[row][7 - col] = pixelValue;
            }
        }
    }
}
