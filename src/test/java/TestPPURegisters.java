import NES.PPU.PPU;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPPURegisters {
    @Test
    public void test_PPUADDR_write_twice() {
        byte[] dummy_chr_rom = new byte[1024 * 8];
        PPU ppu = new PPU(dummy_chr_rom);
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
}
