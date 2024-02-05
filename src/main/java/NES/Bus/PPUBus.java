package NES.Bus;

import NES.Cartridge.Cartridge;
import NES.Cartridge.Mirroring;
import NES.Common;
import NES.PPU.PPURegisters;

import java.util.Optional;

public class PPUBus {

    public final PPURegisters ppuRegisters;

    /**
     * Contains 2 pattern tables, each is 4KB in size.
     * Can be null if the cartridge has CHR ROM size 0.
     * Address: 0x0000 - 0x1FFF
     */
    public final byte[] chr_rom;

    /**
     * Contains 8KB of RAM. Only initialized if the cartridge does not have CHR ROM.
     * Can be null if the cartridge has CHR ROM.
     * Address: 0x0000 - 0x1FFF
     */
    public final byte[] chr_ram;

    /**
     * If the cartridge has CHR ROM, this is true.
     * If the cartridge has CHR RAM, this is false.
     * This flag is faster check than checking if chr_rom size is 0.
     */
    private final boolean is_chr_rom;

    /**
     * Contains 4 name tables, each is 1KB in size.
     * Note: only 2 are used (taking real memory), the other 2 are mirrors (logical).
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

    private final Mirroring mirroring;


    /**
     * DMA_PAGE - The page of memory to copy from, when the DMA transfer is started.
     */
    private byte dma_page;

    /**
     * DMA_ADDR - The address of the first byte to copy, when the DMA transfer is started.
     */
    private byte dma_addr;
    /**
     * DMA_DATA - The data to write to the PPU, when the DMA transfer is started.
     */
    private byte dma_data;
    /**
     * DMA_STARTED - If the DMA transfer is in progress.
     */
    private boolean dma_started;

    public PPUBus(PPURegisters ppuRegisters, Cartridge cartridge) {
        this.ppuRegisters = ppuRegisters;
        this.chr_rom = cartridge.chr_rom();
        this.mirroring = cartridge.header().getMirrorType();
        this.chr_ram = cartridge.chr_ram();
        this.is_chr_rom = (chr_rom.length > 0);

        this.palette_ram = new byte[32];
        this.vram = new byte[1024 * 2];
    }

    public byte ppu_read(short addr) {
        try {
            addr &= (short) 0xFFFF;
            if (addr >= 0x0000 && addr <= 0x1FFF) {
                // CHR ROM / RAM for pattern tables
                return is_chr_rom? chr_rom[addr] : chr_ram[addr];
            } else if (addr >= 0x2000 && addr <= 0x2FFF) {
                // Name table
                if (mirroring == Mirroring.HORIZONTAL) {
                    if (addr >= 0x2800) {
                        addr -= 0x800;
                    }
                } else {
                    // Vertical
                    if (addr >= 0x2800 && addr <= 0x2BFF)
                        addr -= 0x400;
                    else if (addr >= 0x2C00)
                        addr -= 0x800;
                }
                return vram[addr - 0x2000];
            } else if (addr >= 0x3000 && addr <= 0x3EFF) {
                // Mirrors of 0x2000-0x2EFF
                return ppu_read((short) (addr - 0x1000));
            } else if (addr >= 0x3F00 && addr <= 0x3FFF) {
                // Palette RAM

                // 0x3F04, 0x3F08, 0x3F0C are mirrors of 0x3F00
                if (addr == 0x3F04 || addr == 0x3F08 || addr == 0x3F0C) {
                    addr = 0x3F00;
                }

                // 0x3F10, 0x3F14, 0x3F18, 0x3F1C are mirrors of $3F00/$3F04/$3F08/$3F0C
                if (addr == 0x3F10 || addr == 0x3F14 || addr == 0x3F18 || addr == 0x3F1C) {
                    addr -= 0x10;
                }

                return palette_ram[addr - 0x3F00];
            } else {
                throw new RuntimeException("Invalid PPU memory address: " + Common.shortToHex(addr, true));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading from PPU memory address: " + Common.shortToHex(addr, true), e);
        }
    }

    public void ppu_write(short addr, byte value) {
        if (addr >= 0x0000 && addr <= 0x1FFF) {
            // CHR ROM / pattern table
            throw new RuntimeException("Cannot write to CHR ROM");
        } else if ((addr >= 0x2000 && addr <= 0x2FFF) ||
                (addr >= 0x3000 && addr <= 0x3EFF)) {
            // Name table (second if - mirrors of 0x2000-0x2EFF)

            // If mirror of 0x2000-0x2EFF
            if (addr >= 0x3000)
                addr -= 0x1000;

            int nametable_id = 0;
            if (addr >= 0x2400 && addr <= 0x27FF)
                nametable_id = 1;
            else if (addr >= 0x2800 && addr <= 0x2BFF)
                nametable_id = 2;
            else if (addr >= 0x2C00)
                nametable_id = 3;

            if (mirroring == Mirroring.HORIZONTAL) {
                if (nametable_id == 1 || nametable_id == 3)
                    addr -= 0x400;
            } else {
                // Vertical
                if (nametable_id == 2 || nametable_id == 3)
                    addr -= 0x800;
            }
            vram[((addr - 0x2000) % 0x400)] = value;
            //logger.debug("Writing to name table at index: " + ((addr - 0x2000) % 0x400));
        } else if (addr >= 0x3F00 && addr <= 0x3FFF) {
            // Palette RAM

            // 0x3F04, 0x3F08, 0x3F0C are mirrors of 0x3F00
            if (addr == 0x3F04 || addr == 0x3F08 || addr == 0x3F0C) {
                addr = 0x3F00;
            }

            // 0x3F10, 0x3F14, 0x3F18, 0x3F1C are mirrors of $3F00/$3F04/$3F08/$3F0C
            if (addr == 0x3F10 || addr == 0x3F14 || addr == 0x3F18 || addr == 0x3F1C) {
                addr -= 0x10;
            }

            palette_ram[(addr - 0x3F00) % 32] = value;
        } else {
            throw new RuntimeException("Invalid PPU write memory address: " + Common.shortToHex(addr, true));
        }
    }
}
