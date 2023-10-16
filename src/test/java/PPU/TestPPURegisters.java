package PPU;

import NES.Bus.Bus;
import NES.Bus.PPUBus;
import NES.Cartridge.Cartridge;
import NES.Cartridge.Mirroring;
import NES.Cartridge.iNESHeader;
import NES.PPU.PPU;
import Utils.Helper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPPURegisters {

    private PPU ppu;
    private Bus bus;

    private final byte[] prg_rom = new byte[1024 * 32];
    private final byte[] chr_rom = new byte[1024 * 8];

    @Before
    public void setUp() {
        bus = new Bus();
        ppu = new PPU();

        iNESHeader header = new iNESHeader(
                1,
                1,
                0,
                Mirroring.HORIZONTAL,
                false,
                false,
                false,
                false,
                false,
                false,
                0,
                iNESHeader.TVSystem.NTSC,
                iNESHeader.TVSystem.NTSC,
                false,
                false);
        ppu.attachBus(bus);
        bus.attachPPUBus(new PPUBus(ppu.registers, new Cartridge(header, prg_rom, chr_rom)));
    }

    /**
     * PPUADDR is loopy_v, however when I write to PPUADDR, I write to loopy_t.
     */
//    @Test
//    public void test_PPUADDR_write() {
//        ppu.registers.writePPUADDR((byte) 0x12);
//        ppu.registers.writePPUADDR((byte) 0x34);
//        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0x1234);
//
//        ppu.registers.writePPUADDR((byte) 0x56);
//        ppu.registers.writePPUADDR((byte) 0x78);
//        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0x5678);
//
//        ppu.registers.writePPUADDR((byte) 0x9A);
//        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0x9A78);
//
//        ppu.registers.writePPUADDR((byte) 0xBC);
//        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0x9ABC);
//
//        ppu.registers.writePPUADDR((byte) 0xDE);
//        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0xDEBC);
//
//        ppu.registers.writePPUADDR((byte) 0xF0);
//        assertEquals(ppu.registers.getPPUADDR() & 0xFFFF, 0xDEF0);
//    }

    @Test
    public void test_palette_write() {
        ppu.registers.writePPUADDR((byte) 0x3F);
        ppu.registers.writePPUADDR((byte) 0x00);
        ppu.registers.writePPUDATA((byte) 0x29);

        assertEquals(ppu.read((short) 0x3F00), 0x29);
    }

    /**
     * Test all loopy writes: <a href="https://www.nesdev.org/wiki/PPU_scrolling#Summary">wiki</a>
     */
    @Test
    public void test_loopy() {
        // TODO: Run the program
        String[] program = {"LDA #$00", "STA $2000"};
        Cartridge cartridge = Helper.createCustomCartridge(program);
        assertEquals(((ppu.registers.loopy_t >> 10) & 0b11), 0); // nametable select = 0
    }
}
