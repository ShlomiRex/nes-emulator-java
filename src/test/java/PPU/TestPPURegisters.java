package PPU;

import NES.PPU.PPU;
import NES.PPU.PaletteRAM;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPPURegisters {

    private PPU ppu;
    private PaletteRAM palette_ram;
    private byte[] chr_rom = new byte[1024 * 8];

    @Before
    public void setUp() {
        palette_ram = new PaletteRAM();
        ppu = new PPU(chr_rom, palette_ram);
    }

    @Test
    public void test_PPUADDR_write() {
        ppu.registers.writePPUADDR((byte) 0x12);
        ppu.registers.writePPUADDR((byte) 0x34);
        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0x1234);

        ppu.registers.writePPUADDR((byte) 0x56);
        ppu.registers.writePPUADDR((byte) 0x78);
        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0x5678);

        ppu.registers.writePPUADDR((byte) 0x9A);
        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0x9A78);

        ppu.registers.writePPUADDR((byte) 0xBC);
        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0x9ABC);

        ppu.registers.writePPUADDR((byte) 0xDE);
        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0xDEBC);

        ppu.registers.writePPUADDR((byte) 0xF0);
        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0xDEF0);
    }

    @Test
    public void test_PPUDATA_write() {
        // TODO: Implement
    }

    @Test
    public void test_palette_write() {
        ppu.registers.writePPUADDR((byte) 0x3F);
        ppu.registers.writePPUADDR((byte) 0x00);
        ppu.registers.writePPUDATA((byte) 0x29);

        assertEquals(palette_ram.read(0x3F00), 0x29);
    }
}
