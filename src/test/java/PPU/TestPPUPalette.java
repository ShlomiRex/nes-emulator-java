package PPU;

import NES.Cartridge.Cartridge;
import NES.NES;
import Utils.Helper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestPPUPalette {
    @Test
    public void test_palette_write() {
        Cartridge cartridge = Helper.createDummyCartridge();
        NES nes = new NES(cartridge);

        // 0x3F04 is mirror of 0x3F00
        nes.ppu.write((short) 0x3F04, (byte) 0x10);
        assertEquals(0x10, nes.ppu.read((short) 0x3F04));
        assertEquals(0x10, nes.ppu.read((short) 0x3F00));

        // 0x3F08 is mirror of 0x3F00
        nes.ppu.write((short) 0x3F08, (byte) 0x20);
        assertEquals(0x20, nes.ppu.read((short) 0x3F08));
        assertEquals(0x20, nes.ppu.read((short) 0x3F00));

        // 0x3F0C is mirror of 0x3F00
        nes.ppu.write((short) 0x3F0C, (byte) 0x30);
        assertEquals(0x30, nes.ppu.read((short) 0x3F0C));
        assertEquals(0x30, nes.ppu.read((short) 0x3F00));
    }
}
