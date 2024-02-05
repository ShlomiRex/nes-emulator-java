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
     * PPU cycles of current scanline only.
     * Reset to zero after 341 cycles.
     */
    public int cycle;

    public long total_cycles;

    /**
     * PPU scanlines. Reset to zero after 262 scanlines.
     */
    public int scanline;

    /**
     * PPU frames. Reset to zero after 60 frames.
     */
    public int frame;

    private int last_frame;

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

    /**
     * Even/odd frame flag.
     * True: if frame is even.
     * False: if frame is odd.
     */
    private boolean is_even = true;

    /**
     * True: if PPUMASK bit 3 or 4 is set.
     * False: else.
     */
    private boolean is_rendering_enabled;

    /**
     * Background tile to be used for the current 8 pixels.
     */
    private byte bg_next_tile_id;

    /**
     * Attribute table byte for the next 8 pixels.
     */
    private byte bg_next_tile_attrib;

    private short bg_shifter_pattern_lo, bg_shifter_pattern_hi;

    private short bg_shifter_attrib_lo, bg_shifter_attrib_hi;

    private boolean spriteZeroHitPossible, spriteZeroBeingRendered;

    private byte bg_next_tile_lsb, bg_next_tile_msb;

    private class OAEntry {
        public byte y;
        public byte tile_index;
        public byte attr;
        public byte x;
    }

    private final OAEntry[] spriteScanline = new OAEntry[8];

    private byte sprite_count;

    private final byte[] sprite_shifter_pattern_lo = new byte[8];
    private final byte[] sprite_shifter_pattern_hi = new byte[8];

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

        bg_shifter_pattern_lo = 0;
        bg_shifter_pattern_hi = 0;

        bg_shifter_attrib_lo = 0;
        bg_shifter_attrib_hi = 0;

        bg_next_tile_lsb = 0;
        bg_next_tile_msb = 0;

        for (int i = 0; i < 8; i++) {
            spriteScanline[i] = new OAEntry();
        }
    }

    /**
     * Clock tick for PPU.
     * Heavily inspired by the <a href="https://www.nesdev.org/w/images/default/4/4f/Ppu.svg">timing diagram</a>
     */
    public void clock_tick() {
        // End of scanline.
        // Reset cycle and increment scanline
        if (cycle > 340) {
            cycle = 0;
            scanline ++;
            //logger.debug("End of scanline, cycle: {}, scanline: {}, frame: {}", cycle, scanline, frame);
            return;
        }

        // End of frame.
        // Reset scanline back to 0 after 261 scanlines
        if (scanline == 262) {
            scanline = 0;
            frame ++;
            is_even = !is_even;
            //logger.debug("End of frame, cycle: {}, scanline: {}, frame: {}", cycle, scanline, frame);
            return;
        }

        // End of 60 frames.
        // Clamp frame between 0 and 60
        if (frame == 60) {
            frame = 0;
            return;
        }

        if (frame != last_frame) {
            //logger.debug("Frame: {}", frame);
            last_frame = frame;
        }

        if (frame == 10 && scanline == 109 + 8 && cycle > 124 + 5) {
            //logger.debug("After letter E reached");
            int a = 3; // dummy breakpoint

            int nametable_addr = 0x2000;
            debug_log_nametable(nametable_addr);
        }

        if (scanline == 0 && cycle == 0) {
            // "Odd frame" cycle skip
            cycle ++;
            return;
        }

        is_rendering_enabled = Common.Bits.getBit(registers.PPUMASK, 3) // Show background
                || Common.Bits.getBit(registers.PPUMASK, 4); // Show sprites

        // Scanline 0-239 (including): Visible scanlines
        // Heavily inspired by the PPU timing diagram
        if (scanline >= 0 && scanline < 240) {
            if (cycle <= 256) {
                update_shifters();

                switch(cycle % 8) {
                    case 1 -> load_nt();
                    case 3 -> load_at();
                    case 5 -> load_bg_lsbits();
                    case 7 -> load_bg_msbits();
                }
            }

            if (cycle != 0 && (cycle % 8 == 0))
                incHorizontalScroll();

            if (cycle == 256)
                incVerticalScroll();

            if (cycle == 257) {
                load_BG_shifters();
                transferHorizontalScroll();
            }

            // Read next scanline NT byte
            // TODO: Uncomment
            if (cycle == 338 || cycle == 340) {
                bg_next_tile_id = read((short) (0x2000 | (registers.loopy_v & 0x0FFF)));
            }

            // Foreground rendering after end of drawing scanline
            if (cycle == 257) {
                // Clear latches
                for (int i = 0; i < 8; i++) {
                    spriteScanline[i].attr = 0;
                    spriteScanline[i].tile_index = 0;
                    spriteScanline[i].x = 0;
                    spriteScanline[i].y = 0;

                    sprite_shifter_pattern_lo[i] = 0;
                    sprite_shifter_pattern_hi[i] = 0;
                }
                sprite_count = 0;
                spriteZeroHitPossible = false;

                // Evaluate which sprites are visible in the next scanline
                byte nOAMEntry = 0;

                while (nOAMEntry < 64 && sprite_count < 9) {
                    int diff = scanline - (oam[nOAMEntry * 4] & 0xFF);

                    int sprite_size = Common.Bits.getBit(registers.PPUCTRL, 5) ? 16 : 8;
                    if (diff >= 0 && diff < sprite_size) {
                        if (sprite_count < 8) {
                            if (nOAMEntry == 0)
                                spriteZeroHitPossible = true;

                            spriteScanline[sprite_count].y = oam[nOAMEntry * 4];
                            spriteScanline[sprite_count].tile_index = oam[nOAMEntry * 4 + 1];
                            spriteScanline[sprite_count].attr = oam[nOAMEntry * 4 + 2];
                            spriteScanline[sprite_count].x = oam[nOAMEntry * 4 + 3];

                            sprite_count ++;
                        }
                    }
                    nOAMEntry ++;
                }

                // Sprite overflow
                registers.PPUSTATUS = Common.Bits.setBit(registers.PPUSTATUS, 5, (sprite_count > 8));
            }

            // Last cycle of scanline
            if (cycle == 340) {
                for (int i = 0; i < sprite_count; i++) {
                    byte sprite_pattern_bits_lo, sprite_pattern_bits_hi;
                    short sprite_pattern_addr_lo, sprite_pattern_addr_hi;

                    // Sprite size (0: 8x8 pixels; 1: 8x16 pixels)
                    if (!Common.Bits.getBit(registers.PPUCTRL, 5)) {
                        // 8x8 Sprite mode

                        // If sprite NOT flipped vertically
                        if (!Common.Bits.getBit(spriteScanline[i].attr, 7)) {
                            // Sprite pattern table address for 8x8 sprites (0: $0000; 1: $1000; ignored in 8x16 mode)
                            sprite_pattern_addr_lo = (short) (Common.Bits.getBit(registers.PPUCTRL, 3) ? 0x1000 : 0);
                            sprite_pattern_addr_lo |= (short) (spriteScanline[i].tile_index << 4);
                            sprite_pattern_addr_lo |= (short) (scanline - spriteScanline[i].y);
                        } else {
                            // Sprite is flipped vertically

                            // Sprite pattern table address for 8x8 sprites (0: $0000; 1: $1000; ignored in 8x16 mode)
                            sprite_pattern_addr_lo = (short) (Common.Bits.getBit(registers.PPUCTRL, 3) ? 0x1000 : 0);
                            sprite_pattern_addr_lo |= (short) (spriteScanline[i].tile_index << 4);
                            sprite_pattern_addr_lo |= (short) (7 - (scanline - spriteScanline[i].y) );
                        }

                    } else {
                        // 8x16 Sprite mode

                        // If sprite NOT flipped vertically
                        if (!Common.Bits.getBit(spriteScanline[i].attr, 7)) {
                            if (scanline - spriteScanline[i].y < 8) {
                                // Read top half tile

                                sprite_pattern_addr_lo = (short) ((spriteScanline[i].tile_index & 0x01) << 12); // Pattern Table address (0: $0000; 1: $1000)
                                sprite_pattern_addr_lo |= (short) ((spriteScanline[i].tile_index & 0xFE) << 4); // Tile index * 16
                                sprite_pattern_addr_lo |= (short) ((scanline - spriteScanline[i].y) & 0x07); // Fine Y offset
                            } else {
                                // Read bottom half tile

                                sprite_pattern_addr_lo = (short) ((spriteScanline[i].tile_index & 0x01) << 12); // Pattern Table address (0: $0000; 1: $1000)
                                sprite_pattern_addr_lo |= (short) (((spriteScanline[i].tile_index & 0xFE) + 1) << 4); // Tile index * 16
                                sprite_pattern_addr_lo |= (short) ((scanline - spriteScanline[i].y) & 0x07); // Fine Y offset
                            }
                        } else {
                            // Sprite is flipped vertically

                            if (scanline - spriteScanline[i].y < 8) {
                                // Read top half tile

                                sprite_pattern_addr_lo = (short) ((spriteScanline[i].tile_index & 0x01) << 12); // Pattern Table address (0: $0000; 1: $1000)
                                sprite_pattern_addr_lo |= (short) (((spriteScanline[i].tile_index & 0xFE) + 1) << 4); // Tile index * 16
                                sprite_pattern_addr_lo |= (short) (7 - (scanline - spriteScanline[i].y) & 0x07); // Fine Y offset
                            } else {
                                // Read bottom half tile

                                sprite_pattern_addr_lo = (short) ((spriteScanline[i].tile_index & 0x01) << 12); // Pattern Table address (0: $0000; 1: $1000)
                                sprite_pattern_addr_lo |= (short) ((spriteScanline[i].tile_index & 0xFE) << 4); // Tile index * 16
                                sprite_pattern_addr_lo |= (short) (7 - (scanline - spriteScanline[i].y) & 0x07); // Fine Y offset
                            }
                        }
                    }

                    sprite_pattern_addr_hi = (short) (sprite_pattern_addr_lo + 8);

//                    sprite_pattern_bits_lo = read(sprite_pattern_addr_lo);
//                    sprite_pattern_bits_hi = read(sprite_pattern_addr_hi);

                    // If flipped horizontally, we need to flip the pattern bits
                    if (Common.Bits.getBit(spriteScanline[i].attr, 6)) {
//                        sprite_pattern_bits_lo = Common.Bits.reverseByte(sprite_pattern_bits_lo);
//                        sprite_pattern_bits_hi = Common.Bits.reverseByte(sprite_pattern_bits_hi);
                    }

//                    sprite_shifter_pattern_lo[i] = sprite_pattern_bits_lo;
//                    sprite_shifter_pattern_hi[i] = sprite_pattern_bits_hi;
                }
            }
        }

        else if (scanline == 241) {
            if (cycle == 1) {
                // VBlank start

                //logger.debug("VBlank start");

                registers.PPUSTATUS = Common.Bits.setBit(registers.PPUSTATUS, 7, true);

                /*
                 * Bit 7 of PPUCTRL: Generate an NMI at the start of the vertical blanking interval (0: off; 1: on)
                 * The PPUCTRL controls the NMI line.
                 */
                if (Common.Bits.getBit(registers.getPPUCTRL(), 7)) {
                    bus.nmi_line = true;
                    //logger.debug("Generating NMI interrupt");
                }

                // Repaint the game canvas
                if (trigger_game_canvas_repaint != null)
                    trigger_game_canvas_repaint.run();
            }
        }

        // Scanline 261: Pre render line
        else if (scanline == 261) {
            if (cycle == 1) {
                registers.PPUSTATUS = Common.Bits.setBit(registers.PPUSTATUS, 7, false); // Clear Vblank
                registers.PPUSTATUS = Common.Bits.setBit(registers.PPUSTATUS, 5, false); // Clear sprite overflow
                registers.PPUSTATUS = Common.Bits.setBit(registers.PPUSTATUS, 6, false); // Clear sprite 0 hit
            }

            // vert(v) = vert(t)
            // for each tick of cycle 280-304
            else if (cycle >= 280 && cycle <= 304) {
                transferVerticalScroll();
            }
        }

        draw_frame();

        cycle ++;
    }

    private void debug_log_nametable(int nametable_addr_start) {
        for (int i = 0; i < 32 * 30; i++) {
            int addr = nametable_addr_start + i;
            byte tile = read((short) addr);
            if (tile != 0)
                logger.debug("[{}] = {}", Common.shortToHex((short) addr, true), tile);
        }
    }

    private void update_shifters() {
        // Show background
        if (Common.Bits.getBit(registers.PPUMASK, 3)) {
            bg_shifter_pattern_lo <<= 1;
            bg_shifter_pattern_hi <<= 1;

            bg_shifter_attrib_lo <<= 1;
            bg_shifter_attrib_hi <<= 1;
        }

        // Show sprites
        if (Common.Bits.getBit(registers.PPUMASK, 4) && cycle >= 1 && cycle < 258) {
            for (int i = 0; i < sprite_count; i++) {
                if (spriteScanline[i].x > 0) {
                    spriteScanline[i].x --;
                } else {
                    sprite_shifter_pattern_lo[i] <<= 1;
                    sprite_shifter_pattern_hi[i] <<= 1;
                }
            }
        }
    }

    private void load_BG_shifters() {
        bg_shifter_pattern_lo = (short) ((bg_shifter_pattern_lo & 0xFF00) | bg_next_tile_lsb);
        bg_shifter_pattern_hi = (short) ((bg_shifter_pattern_hi & 0xFF00) | bg_next_tile_msb);

        bg_shifter_pattern_lo = (short) ((bg_shifter_pattern_lo & 0x00FF) | ((bg_next_tile_attrib & 0xFF) << 8));
        bg_shifter_pattern_hi = (short) ((bg_shifter_pattern_hi & 0x00FF) | ((bg_next_tile_attrib & 0xFF) << 8));
    }

    private int get_color_from_palette(byte palette, byte pixel) {
//        if (palette != 0 || pixel != 0)
//            logger.debug("palette: {}, pixel: {}", palette, pixel);
        byte color_index = read((short) (0x3F00 + (palette << 2) + pixel));
        int color_row = color_index / 16;
        int color_col = color_index % 16;

        return color_row * 16 + color_col;
    }

//    /**
//     * Called from within JavaSwing GUI thread.
//     * @param g
//     * @param width The container width
//     * @param height The container height
//     */
//    public void draw_frame(Graphics g, int width, int height) {
//        //logger.debug("Drawing frame");
//
//        // Draw backgrounds
//        for (int tile_row = 0; tile_row < 30; tile_row++) {
//            for (int tile_col = 0; tile_col < 32; tile_col++) {
//                draw_tile(tile_row, tile_col);
//            }
//        }
//
//        // Draw sprites
//        boolean bank = Common.Bits.getBit(registers.PPUCTRL, 3);
//        boolean sprite_size = Common.Bits.getBit(registers.PPUCTRL, 5);
//        for (int sprite_index = 0; sprite_index < 64; sprite_index++) {
//            draw_sprite(sprite_index, bank, sprite_size);
//        }
//
//        // Flush graphics using the buffered image
//        g.drawImage(bufferedImage, 0, 0, width, height, null);
//    }
//
    public void flush_graphics(Graphics g, int width, int height) {
        // Flush graphics using the buffered image
        g.drawImage(bufferedImage, 0, 0, width, height, null);
    }
//
//    /**
//     *
//     * @param sprite_index Index of sprite in OAM (0-63)
//     * @param bank (0: $0000; 1: $1000; ignored in 8x16 mode)
//     * @param sprite_size (0: 8x8; 1: 8x16)
//     */
//    private void draw_sprite(int sprite_index, boolean bank, boolean sprite_size) {
//        // Each sprite is 8x8 pixels and is 4 bytes in size.
//        int sprite_y = oam[sprite_index * 4] & 0xFF;
//        int tile_index = oam[sprite_index * 4 + 1] & 0xFF;
//        byte attributes = oam[sprite_index * 4 + 2];
//        int sprite_x = oam[sprite_index * 4 + 3] & 0xFF;
//
////        // Check offscreen - don't render this sprite if offscreen
////        if (sprite_y >= 239)
////            return;
//
//        // Attributes - bit 2,3,4 ignored
//        int palette_id = attributes & 0b11;
//        // TODO: Finish sprite priority
//        boolean priority = Common.Bits.getBit(attributes, 5); // (0: in front of background; 1: behind background)
//        boolean flip_horizontal = Common.Bits.getBit(attributes, 6);
//        boolean flip_vertical = Common.Bits.getBit(attributes, 7);
//
//        short tile_base_addr = (short) ((bank? 0x1000 : 0) + tile_index * 16);
//        for (int pixel_row = 0; pixel_row < 8; pixel_row++) {
//            // Read 2 bitplanes (8 bits per bitplane) from pattern table
//            byte tile_lsb = read((short) (tile_base_addr + pixel_row));
//            byte tile_msb = read((short) (tile_base_addr + pixel_row + 8));
//
//            for (int pixel_col = 0; pixel_col < 8; pixel_col++) {
//                // Get pixel value (color) from bitplanes (the value must be between 0-3 since we add 2 bits and each can be 0 or 1)
//                byte colorIndex = (byte) ((tile_lsb & 1) + (tile_msb & 1) * 2);
//
//                tile_lsb >>= 1;
//                tile_msb >>= 1;
//
//                // Do not draw the pixel - else, it will overwrite the background
//                if (colorIndex == 0)
//                    continue;
//
//                // We read from palettes 4 through 7 (sprite palettes). 0x3F00 is base palette address.
//                // We skip the first 4 palettes (each palette is 4 bytes).
//                // And then we select which sprite palette to use using the attribute palette_id.
//                // Then we select the color from the palette by using the colorIndex.
//                byte pixelColor = read((short) (0x3F00 + 4*4 + palette_id * 4 + colorIndex));
//
//                int color_row = pixelColor / 16;
//                int color_col = pixelColor % 16;
//
//                int color_index_in_system_palette = color_row * 16 + color_col;
//
//                buffered_pixel_color[0] = color_index_in_system_palette;
//
//                int pixel_x;
//                int pixel_y;
//
//                if (flip_horizontal)
//                    pixel_x = sprite_x + pixel_col;
//                else
//                    pixel_x = sprite_x + (7- pixel_col);
//
//                if (flip_vertical)
//                    pixel_y = sprite_y + (7-pixel_row);
//                else
//                    pixel_y = sprite_y + pixel_row;
//
//                // TODO: Not sure if to put this check here
//                if (pixel_x > 255 || pixel_y > 239)
//                    continue;
//
//                bufferedImage.getRaster().setPixel(pixel_x, pixel_y, buffered_pixel_color);
//            }
//        }
//    }
//
//    private void draw_tile(int tile_row, int tile_col) {
//        // Determine base addresses
//
//        //same
////        int nametable_addr = (registers.PPUCTRL & 0b11) == 0 ? 0x2000 : 0x2400;
//        int nametable_addr = (registers.loopy_t & 0b11) == 0 ? 0x2000 : 0x2400;
//
//        short attributetable_addr = (short) (nametable_addr + 0x3C0);
//        short pattern_table_addr = (short) ((registers.PPUCTRL & 0b10000) == 0 ? 0x0000 : 0x1000);
//
//        // Read nametable byte - this is the index of the tile in the pattern table. This index points to 16 bytes of pattern data (2 bitmap planes).
//        byte patternIndex = read((short) (0x2000 | (registers.loopy_v & 0x0FFF)));
//
//        // Read corresponding attribute table byte - this is the palette index of 4x4 tiles.
//        byte attributeByte = read((short) (attributetable_addr + ((tile_row / 4) * 8) + (tile_col / 4)));
//
//        /*
//        Get the corresponding palette index from attribute byte - 2 bits per tile, the byte represents 4x4 tiles.
//        Bit 0,1 - top left tile, 2,3 - top right tile, 4,5 - bottom left tile, 6,7 - bottom right tile:
//        -------------
//        | 0 1 | 2 3 |
//        -------------
//        | 4 5 | 6 7 |
//        -------------
//         */
//        int bitOffset = (tile_row % 4 / 2) * 2 + (tile_col % 4 / 2);
//        int paletteIndex = (attributeByte >> (bitOffset * 2)) & 0b11;
//
//        // Read 16 bytes from pattern table - this will form the tile pixels, and their colors, which are chosen from the palette.
//        // To avoid copying 16 bytes (for regression reasons), we can just loop over each row, and do this bitmap calculation for each row.
//
//        short tile_base_addr = (short) (pattern_table_addr + (patternIndex & 0xFF) * 16);
//        for (int pixel_row = 0; pixel_row < 8; pixel_row ++) {
//            // Read 2 bitplanes (8 bits per bitplane) from pattern table
//            byte tile_lsb = read((short) (tile_base_addr + pixel_row));
//            byte tile_msb = read((short) (tile_base_addr + pixel_row + 8));
//
//            for (int pixel_col = 0; pixel_col < 8; pixel_col++) {
//                // Get pixel value (color) from bitplanes (the value must be between 0-3 since we add 2 bits and each can be 0 or 1)
//                byte colorIndex = (byte) ((tile_lsb & 1) + (tile_msb & 1) * 2);
//                tile_lsb >>= 1;
//                tile_msb >>= 1;
//
//                // Now we have the pixel value, we can get the color from the palette
//                // Read pixel color from palette RAM
//                byte pixelColor = read((short) (0x3F00 + paletteIndex * 4 + colorIndex));
//
//                int color_row = pixelColor / 16;
//                int color_col = pixelColor % 16;
//                int pixel_x = tile_col * 8 + (7- pixel_col);
//                int pixel_y = tile_row * 8 + pixel_row;
//
//                int color_index_in_system_palette = color_row * 16 + color_col;
//
//                buffered_pixel_color[0] = color_index_in_system_palette;
//                bufferedImage.getRaster().setPixel(pixel_x, pixel_y, buffered_pixel_color);
//            }
//        }
//    }

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

    public void set_pattern_tile(int tile_index, boolean is_left_pattern_table, int[][] dest_pattern) {
        // Each pattern tile is 16 bytes in size. We jump by 16 bytes.
        // The tile index can be 0x0-0xFF, but the actual bytes needed are 0xFF times 16, which fits in u16.
        short addr = (short) ((tile_index & 0xFF) * 16);
        if (!is_left_pattern_table)
            addr += (16 * 0xFF);

        // The pattern in bit planes (each plane = 8 bytes)
        byte[] tile = new byte[16];
        if (bus.ppuBus.chr_rom.length != 0)
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

    /**
     * Used in debugger only. Quickly draws nametable.
     */
    public void debugger_draw_nametable(Graphics g, int tableIndex, int canvas_width, int canvas_height) {
        int pixel_width = (canvas_width / 32) / 8;
        int pixel_height = (canvas_height / 30) / 8;
        for (int tile_row = 0; tile_row < 30; tile_row++) {
            for (int tile_col = 0; tile_col < 32; tile_col++) {
                debugger_draw_tile(g, tile_row, tile_col, tableIndex, pixel_width, pixel_height);
            }
        }
    }

    /**
     * Y Scroll is composed of 2 components: fine Y and coarse Y.
     * This is why its quite complex to just increment Y scroll.
     * First we increment fine Y and if it wraps around, we increment coarse Y.
     * We also deal with wrapping around the nametable.
     */
    private void incVerticalScroll() {
        // If rendering is enabled (show background or sprites)
        if (is_rendering_enabled) {
            // Increment coarse Y
            int coarse_y = (registers.loopy_v & 0b11111_00000) >> 5;

            // Check wrapping: https://www.nesdev.org/wiki/PPU_scrolling#Wrapping_around
            if ((registers.loopy_v & 0x7000) == 0x7000) // if fine Y < 7
            {
                registers.loopy_v += 0x1000; // increment fine Y
            } else {
                registers.loopy_v &= ~0x7000; // fine Y = 0
                int y = (registers.loopy_v & 0x03E0) >> 5; // let y = coarse Y
                if (y == 29) {
                    y = 0; // coarse Y = 0
                    registers.loopy_v ^= 0x0800; // switch vertical nametable
                } else if (y == 31) {
                    y = 0; // coarse Y = 0, nametable not switched
                } else {
                    y += 1; // increment coarse Y
                }
                registers.loopy_v = (short) ((registers.loopy_v & ~0x03E0) | (y << 5));     // put coarse Y back into v
            }

            coarse_y += 1;
            if (coarse_y == 0b11111) {
                // TODO: Check for wrapping. https://www.nesdev.org/wiki/PPU_scrolling#Wrapping_around
                //coarse_y = 0;
                // Switch vertical nametable
                //registers.loopy_v = (short) (registers.loopy_v ^ 0b10000_0000000000);
            }
            registers.loopy_v = (short) ((registers.loopy_v & 0b11111_00000_11111) | (coarse_y << 5));
        }
    }

    private void incHorizontalScroll() {
        // If rendering is enabled (show background or sprites)
        if (is_rendering_enabled) {
            // Increment coarse X
            int coarse_x = registers.loopy_v & 0b11111;

            // Check wrapping: https://www.nesdev.org/wiki/PPU_scrolling#Wrapping_around
            if (coarse_x == 31) {
                coarse_x = 0; // coarse X = 0
                registers.loopy_v ^= 0b00010_0000000000; // switch horizontal nametable
            } else {
                coarse_x += 1; // increment coarse X
            }
            registers.loopy_v = (short) ((registers.loopy_v & ~0b11111) | coarse_x); // put coarse X back into v
        }
    }

    /**
     * Used in debugger only. Quickly draws a tile.
     */
    private void debugger_draw_tile(Graphics g, int tile_row, int tile_col, int tableIndex, int pixel_width, int pixel_height) {
        int nametable_addr = 0x2000 + (tableIndex * 0x400);

        //TODO: Fix this
        if (tableIndex > 1)
            return;
        //int nametable_addr = (registers.loopy_t.nametable_select & 0b11) == 0 ? 0x2000 : 0x2400;

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

//                buffered_pixel_color[0] = color_index_in_system_palette;
//                bufferedImage.getRaster().setPixel(pixel_x, pixel_y, buffered_pixel_color);

                g.setColor(Bus.SYSTEM_PALETTE[color_index_in_system_palette]);
                g.drawRect(pixel_x, pixel_y, pixel_width, pixel_height);
            }
        }
    }


    /**
     * If rendering is enabled, the PPU copies all bits related to horizontal position from t to v:
     * v: ....A.. ...BCDEF <- t: ....A.. ...BCDEF
     */
    private void transferHorizontalScroll() {
        if (is_rendering_enabled) {
            // At dot 257 of each scanline
            // v: ....A.. ...BCDEF <- t: ....A.. ...BCDEF

            short mask = (short) (0b00100_00000_11111);

            registers.loopy_v &= (short) ~mask;
            registers.loopy_v |= (short) (registers.loopy_t & mask);
        }
    }

    /**
     * During dots 280 to 304 of the pre-render scanline (end of vblank)
     *
     * If rendering is enabled, at the end of vblank,
     * shortly after the horizontal bits are copied from t to v at dot 257,
     * the PPU will repeatedly copy the vertical bits from t to v from dots 280 to 304,
     * completing the full initialization of v from t:
     *
     * v: GHIA.BC DEF..... <- t: GHIA.BC DEF.....
     */
    private void transferVerticalScroll() {
        if (is_rendering_enabled) {
            // v: GHIA.BC DEF..... <- t: GHIA.BC DEF.....

            short mask = (short) (0b11110_11111_00000);

            registers.loopy_v &= (short) ~mask;
            registers.loopy_v |= (short) (registers.loopy_t & mask);
        }
    }

    /**
     * Load nametable byte (see PPU timing)
     */
    private void load_nt() {
        load_BG_shifters();
        bg_next_tile_id = read((short) (0x2000 | (registers.loopy_v & 0x0FFF)));
    }

    /**
     * Load attribute table byte (see PPU timing)
     */
    private void load_at() {
        bg_next_tile_attrib = read((short) (0x23C0                 // first nametable + attribute table offset
                | (registers.loopy_v & 0x0C00)                     // nametable X,Y
                | ((registers.loopy_v >> 4) & 0x38)                // coarse y
                | ((registers.loopy_v >> 2) & 0x07)));             // coarse X

        // Coarse Y second bit
        if (Common.Bits.getBit(registers.loopy_v, 6))
            bg_next_tile_attrib >>= 4;

        // Coarse X second bit
        if (Common.Bits.getBit(registers.loopy_v, 1))
            bg_next_tile_attrib >>= 2;

        bg_next_tile_attrib &= 0x03;
    }

    private void load_bg_lsbits() {
        short addr = 0;

        // Background pattern table address (0: $0000; 1: $1000)
        if (Common.Bits.getBit(registers.PPUCTRL, 4))
            addr += 0x1000;

        addr += (short) (bg_next_tile_id & 0xFF << 4);
        addr += (short) ((registers.loopy_v & 0b111_00_00000_00000) >> 12);

        bg_next_tile_lsb = read(addr);
    }

    private void load_bg_msbits() {
        short addr = 0;

        // Background pattern table address (0: $0000; 1: $1000)
        if (Common.Bits.getBit(registers.PPUCTRL, 4))
            addr += 0x1000;

        addr += (short) ((bg_next_tile_id & 0xFF) << 4);
        addr += (short) ((registers.loopy_v & 0b111_00_00000_00000) >> 12);
        addr += 8; // 8 plane offset

        bg_next_tile_msb = read(addr);
    }

    private void draw_frame() {
        byte bg_pixel = 0;
        byte bg_palette = 0;

        // If show background
        if (Common.Bits.getBit(registers.PPUMASK, 3)) {
            short bit_mux = (short) (0x8000 >> registers.fine_x_scroll);

            byte p0_pixel = (byte) ((bg_shifter_pattern_lo & bit_mux) > 0 ? 1 : 0);
            byte p1_pixel = (byte) ((bg_shifter_pattern_hi & bit_mux) > 0 ? 1 : 0);

            bg_pixel = (byte) ((p1_pixel << 1) | p0_pixel);

            byte bg_pal0 = (byte) ((bg_shifter_attrib_lo & bit_mux) > 0 ? 1 : 0);
            byte bg_pal1 = (byte) ((bg_shifter_attrib_hi & bit_mux) > 0 ? 1 : 0);

            bg_palette = (byte) ((bg_pal1 << 1) | bg_pal0);
        }

        byte fg_pixel = 0;
        byte fg_palette = 0;
        byte fg_priority = 0;

        // If show foreground (sprites)
        if (Common.Bits.getBit(registers.PPUMASK, 4)) {
            spriteZeroBeingRendered = false;

            for (int i = 0; i < sprite_count; i++) {
                // Scanline cycle has "collided" with sprite, shifters taking over
                if (spriteScanline[i].x == 0) {
                    byte fg_pixel_lo = (byte) ((sprite_shifter_pattern_lo[i] & 0x80) > 0 ? 1 : 0);
                    byte fg_pixel_hi = (byte) ((sprite_shifter_pattern_hi[i] & 0x80) > 0 ? 1 : 0);
                    fg_pixel = (byte) ((fg_pixel_hi << 1) | fg_pixel_lo);

                    fg_palette = (byte) ((byte) (spriteScanline[i].attr & 0x03) + 0x04);
                    fg_priority = (byte) (((spriteScanline[i].attr & 0x20) == 0) ? 1 : 0);

                    // If not transparent
                    if (fg_pixel != 0) {
                        if (i == 0) {
                            spriteZeroBeingRendered = true;
                        }

                        break;
                    }
                }
            }
        }

        byte pixel = 0;
        byte palette = 0;

        if (bg_pixel == 0 && fg_pixel > 0) {
            pixel = fg_pixel;
            palette = fg_palette;
        } else if (bg_pixel > 0 && fg_pixel == 0) {
            pixel = bg_pixel;
            palette = bg_palette;
        } else if (bg_pixel > 0 && fg_pixel > 0) {
            if (fg_priority == 1) {
                pixel = fg_pixel;
                palette = fg_palette;
            } else {
                pixel = bg_pixel;
                palette = bg_palette;
            }

            if (spriteZeroHitPossible && spriteZeroBeingRendered) {
                if (is_rendering_enabled) {
                    boolean render_background_left = Common.Bits.getBit(registers.PPUMASK, 1);
                    boolean render_sprites_left = Common.Bits.getBit(registers.PPUMASK, 2);
                    if (!(render_background_left || render_sprites_left)) {
                        if (cycle >= 9 && cycle < 258) {
                            // Sprite zero hit
                            registers.PPUSTATUS = Common.Bits.setBit(registers.PPUSTATUS, 6, true);
                        }
                    } else {
                        if (cycle >= 1 && cycle < 258) {
                            // Sprite zero hit
                            registers.PPUSTATUS = Common.Bits.setBit(registers.PPUSTATUS, 6, true);
                        }
                    }
                }
            }
        }

        if (cycle > 0 && cycle <= 256 && scanline >= 0 && scanline < 240) {
            // Now we have a final pixel colour and palette
            //logger.debug("Scanline: {}, \tRendering cycle: {}, \tpixel: {}, \tpalette: {}", scanline, cycle, pixel, palette);
            buffered_pixel_color[0] = get_color_from_palette(palette, pixel);
            bufferedImage.getRaster().setPixel(cycle - 1, scanline, buffered_pixel_color);
            trigger_game_canvas_repaint.run(); // TODO: Remove. Only for debugging, check each pixel render correctly.
        }

    }
}
