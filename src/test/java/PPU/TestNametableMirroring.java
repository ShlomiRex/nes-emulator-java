package PPU;

import NES.Cartridge.Cartridge;
import NES.Cartridge.Mirroring;
import NES.Cartridge.iNESHeader;
import NES.NES;
import Utils.Helper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the mirroring (reads, writes) of the nametables in PPU
 */
public class TestNametableMirroring {
    @Test
    public void test_horizontal_mirroring() {
        iNESHeader header = Helper.createDummyiNESHeader(Mirroring.HORIZONTAL);
        Cartridge cartridge = Helper.createDummyCartridge(header);
        NES nes = new NES(cartridge);

        // First byte in nametable 0
        nes.ppu.write((short) 0x2000, (byte) 0x01);
        assertEquals(0x01, nes.ppu.read((short) 0x2000));
        assertEquals(0x01, nes.ppu.read((short) 0x2800));

        // First byte in nametable 1
        nes.ppu.write((short) 0x2400, (byte) 0x02);
        assertEquals(0x02, nes.ppu.read((short) 0x2400));
        assertEquals(0x02, nes.ppu.read((short) 0x2C00));

        // First byte in nametable 2
        nes.ppu.write((short) 0x2800, (byte) 0x03);
        assertEquals(0x03, nes.ppu.read((short) 0x2800));
        assertEquals(0x03, nes.ppu.read((short) 0x2000));

        // First byte in nametable 3
        nes.ppu.write((short) 0x2C00, (byte) 0x04);
        assertEquals(0x04, nes.ppu.read((short) 0x2C00));
        assertEquals(0x04, nes.ppu.read((short) 0x2400));

        // Last byte in nametable 0
        nes.ppu.write((short) 0x23FF, (byte) 0x05);
        assertEquals(0x05, nes.ppu.read((short) 0x23FF));
        assertEquals(0x05, nes.ppu.read((short) 0x2BFF));

        // Last byte in nametable 1
        nes.ppu.write((short) 0x27FF, (byte) 0x06);
        assertEquals(0x06, nes.ppu.read((short) 0x27FF));
        assertEquals(0x06, nes.ppu.read((short) 0x2FFF));

        // Last byte in nametable 2
        nes.ppu.write((short) 0x2BFF, (byte) 0x07);
        assertEquals(0x07, nes.ppu.read((short) 0x2BFF));
        assertEquals(0x07, nes.ppu.read((short) 0x23FF));

        // Last byte in nametable 3
        nes.ppu.write((short) 0x2FFF, (byte) 0x08);
        assertEquals(0x08, nes.ppu.read((short) 0x2FFF));
        assertEquals(0x08, nes.ppu.read((short) 0x27FF));
    }

    @Test
    public void test_vertical_mirroring() {
        iNESHeader header = Helper.createDummyiNESHeader(Mirroring.VERTICAL);
        Cartridge cartridge = Helper.createDummyCartridge(header);
        NES nes = new NES(cartridge);

        // First byte in nametable 0
        nes.ppu.write((short) 0x2000, (byte) 0x01);
        assertEquals(0x01, nes.ppu.read((short) 0x2000));
        assertEquals(0x01, nes.ppu.read((short) 0x2400));

        // First byte in nametable 1
        nes.ppu.write((short) 0x2400, (byte) 0x02);
        assertEquals(0x02, nes.ppu.read((short) 0x2400));
        assertEquals(0x02, nes.ppu.read((short) 0x2000));

        // First byte in nametable 2
        nes.ppu.write((short) 0x2800, (byte) 0x03);
        assertEquals(0x03, nes.ppu.read((short) 0x2800));
        assertEquals(0x03, nes.ppu.read((short) 0x2C00));

        // First byte in nametable 3
        nes.ppu.write((short) 0x2C00, (byte) 0x04);
        assertEquals(0x04, nes.ppu.read((short) 0x2C00));
        assertEquals(0x04, nes.ppu.read((short) 0x2800));

        // Last byte in nametable 0
        nes.ppu.write((short) 0x23FF, (byte) 0x05);
        assertEquals(0x05, nes.ppu.read((short) 0x23FF));
        assertEquals(0x05, nes.ppu.read((short) 0x27FF));

        // Last byte in nametable 1
        nes.ppu.write((short) 0x27FF, (byte) 0x06);
        assertEquals(0x06, nes.ppu.read((short) 0x27FF));
        assertEquals(0x06, nes.ppu.read((short) 0x23FF));

        // Last byte in nametable 2
        nes.ppu.write((short) 0x2BFF, (byte) 0x07);
        assertEquals(0x07, nes.ppu.read((short) 0x2BFF));
        assertEquals(0x07, nes.ppu.read((short) 0x2FFF));

        // Last byte in nametable 3
        nes.ppu.write((short) 0x2FFF, (byte) 0x08);
        assertEquals(0x08, nes.ppu.read((short) 0x2FFF));
        assertEquals(0x08, nes.ppu.read((short) 0x2BFF));
    }
}
