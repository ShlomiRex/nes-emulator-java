package PPU;

import NES.Cartridge.Cartridge;
import NES.NES;
import Utils.Helper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestPPURegisters {
    @Test
    public void test_palette_write() {
        Cartridge cartridge = Helper.createDummyCartridge();
        NES nes = new NES(cartridge);

        nes.ppu.registers.writePPUADDR((byte) 0x3F);
        nes.ppu.registers.writePPUADDR((byte) 0x00);
        nes.ppu.registers.writePPUDATA((byte) 0x29);

        assertEquals(nes.ppu.read((short) 0x3F00), 0x29);
    }

    /**
     * Test all loopy writes: <a href="https://www.nesdev.org/wiki/PPU_scrolling#Summary">wiki</a>
     */
    @Test
    public void test_loopy_2000_write() {
        // TODO: Assembler needed. For now I just manually set the program bytes.
        String[] program = {"LDA #$00", "STA $2000"};
        Cartridge cartridge = Helper.createCustomCartridge(program);

        NES nes = new NES(cartridge);

        // set all bits to 1 so we can see if indeed the bits are set correctly after execution
        nes.ppu.registers.loopy_t = ~0;
        assertEquals(((nes.ppu.registers.loopy_t >> 10) & 0b11), 0b11); // nametable select

        // Without forcing PC, I need to set reset interrupt routine. This way its easier
        nes.cpu.registers.PC = (short) 0x8000;

        // LDA #$00
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8000, (byte) 0xA9);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8001, (byte) 0x00);

        // STA $2000
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8002, (byte) 0x8D);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8003, (byte) 0x00);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8004, (byte) 0x20);

        nes.cpu.clock_tick();
        nes.cpu.clock_tick();

        assertEquals(((nes.ppu.registers.loopy_t >> 10) & 0b11), 0); // nametable select = 0
    }

    /**
     * Test all loopy writes: <a href="https://www.nesdev.org/wiki/PPU_scrolling#Summary">wiki</a>
     */
    @Test
    public void test_loopy_2002_read() {
        // TODO: Assembler needed. For now I just manually set the program bytes.
        String[] program = {"LDA $2002"};
        Cartridge cartridge = Helper.createCustomCartridge(program);

        NES nes = new NES(cartridge);

        nes.cpu.registers.PC = (short) 0x8000;

        // LDA $2002
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8000, (byte) 0xAD);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8001, (byte) 0x02);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8002, (byte) 0x20);

        // W is reset
        nes.cpu.clock_tick();
        assertEquals(((nes.ppu.registers.loopy_t >> 10) & 0b11), 0);
        assertFalse(nes.ppu.registers.w);

        // W is reset again
        nes.cpu.registers.PC = (short) 0x8000;
        nes.ppu.registers.w = true;
        nes.cpu.clock_tick();
        assertFalse(nes.ppu.registers.w);
    }

    @Test
    public void test_loopy_2005_write_1() {
        // TODO: Assembler needed. For now I just manually set the program bytes.
        String[] program = {"LDA #$7D", "STA $2005"};
        Cartridge cartridge = Helper.createCustomCartridge(program);

        NES nes = new NES(cartridge);

        nes.cpu.registers.PC = (short) 0x8000;

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8000, (byte) 0xA9);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8001, (byte) 0x7D);

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8002, (byte) 0x8D);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8003, (byte) 0x05);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8004, (byte) 0x20);

        nes.cpu.clock_tick();
        nes.cpu.clock_tick();

        assertEquals((nes.ppu.registers.loopy_t & 0b11111), 0b01111);
        assertTrue(nes.ppu.registers.w);
        assertEquals(nes.ppu.registers.fine_x_scroll, 0b101);
    }

    @Test
    public void test_loopy_2005_write_2() {
        // TODO: Assembler needed. For now I just manually set the program bytes.
        String[] program = {"LDA #$5E", "STA $2005"};
        Cartridge cartridge = Helper.createCustomCartridge(program);

        NES nes = new NES(cartridge);

        nes.cpu.registers.PC = (short) 0x8000;
        nes.ppu.registers.loopy_t = 0b11100_01010_01111; //nametable select = 0, YYYYY = 01111
        nes.ppu.registers.fine_x_scroll = 0b101;
        nes.ppu.registers.w = true;

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8000, (byte) 0xA9);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8001, (byte) 0x5E);

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8002, (byte) 0x8D);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8003, (byte) 0x05);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8004, (byte) 0x20);

        nes.cpu.clock_tick();
        nes.cpu.clock_tick();

        assertEquals(nes.ppu.registers.loopy_t, 0b11000_01011_01111);
        assertFalse(nes.ppu.registers.w);
        assertEquals(nes.ppu.registers.fine_x_scroll, 0b101);
    }

    @Test
    public void test_loopy_2006_write_1() {
        // TODO: Assembler needed. For now I just manually set the program bytes.
        String[] program = {"LDA #$3D", "STA $2006"};
        Cartridge cartridge = Helper.createCustomCartridge(program);

        NES nes = new NES(cartridge);

        nes.cpu.registers.PC = (short) 0x8000;
        nes.ppu.registers.loopy_t = 0b110000101101111;
        nes.ppu.registers.fine_x_scroll = 0b101;
        nes.ppu.registers.w = false;

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8000, (byte) 0xA9);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8001, (byte) 0x3D);

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8002, (byte) 0x8D);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8003, (byte) 0x06);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8004, (byte) 0x20);

        nes.cpu.clock_tick();
        nes.cpu.clock_tick();

        assertEquals(nes.ppu.registers.loopy_t, 0b011110101101111);
        assertTrue(nes.ppu.registers.w);
        assertEquals(nes.ppu.registers.fine_x_scroll, 0b101);
    }

    @Test
    public void test_loopy_2006_write_2() {
        // $2006 (PPUADDR) second write (w is 1)

        /*
        t: ....... ABCDEFGH <- d: ABCDEFGH
        v: <...all bits...> <- t: <...all bits...>
        w:                  <- 0
         */

        Cartridge cartridge = Helper.createDummyCartridge();
        NES nes = new NES(cartridge);

        nes.cpu.registers.PC = (short) 0x8000;
        nes.ppu.registers.loopy_t = 0b011_11_01011_01111;
        nes.ppu.registers.fine_x_scroll = 0b101;
        nes.ppu.registers.w = true;

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8000, (byte) 0xA9); // LDA #$F0 (1111 0000)
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8001, (byte) 0xF0);

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8002, (byte) 0x8D); // STA $2006
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8003, (byte) 0x06);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8004, (byte) 0x20);

        nes.cpu.clock_tick();
        nes.cpu.clock_tick();



        assertEquals(0b011_11_01111_10000, nes.ppu.registers.loopy_t);
        assertFalse(nes.ppu.registers.w);
        assertEquals(0b101, nes.ppu.registers.fine_x_scroll);
        assertEquals(0b011_11_01111_10000, nes.ppu.registers.loopy_v);

        Helper.assertFirst15BitsEqual(nes.ppu.registers.loopy_t, nes.ppu.registers.loopy_v);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * https://www.nesdev.org/wiki/PPU_scrolling#Details
     */
    @Test
    public void test_split_xy_scroll_1() {
        // TODO: Assembler needed. For now I just manually set the program bytes.
        String[] program = {"LDA #$04", "STA $2006"};
        Cartridge cartridge = Helper.createCustomCartridge(program);

        NES nes = new NES(cartridge);

        nes.cpu.registers.PC = (short) 0x8000;
        nes.ppu.registers.loopy_t = 0;
        nes.ppu.registers.loopy_v = 0;
        nes.ppu.registers.fine_x_scroll = 0;
        nes.ppu.registers.w = false;

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8000, (byte) 0xA9);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8001, (byte) 0x04);

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8002, (byte) 0x8D);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8003, (byte) 0x06);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8004, (byte) 0x20);

        nes.cpu.clock_tick();
        nes.cpu.clock_tick();

        assertEquals((nes.ppu.registers.loopy_t >> 8) & 0b1111111, 0b0000100);
        assertTrue(nes.ppu.registers.w);
    }

    @Test
    public void test_split_xy_scroll_2() {
        // TODO: Assembler needed. For now I just manually set the program bytes.
        String[] program = {"LDA #$3E", "STA $2005"};
        Cartridge cartridge = Helper.createCustomCartridge(program);

        NES nes = new NES(cartridge);

        nes.cpu.registers.PC = (short) 0x8000;
        nes.ppu.registers.loopy_t = 0b000010000000000;
        nes.ppu.registers.loopy_v = 0;
        nes.ppu.registers.fine_x_scroll = 0;
        nes.ppu.registers.w = true;

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8000, (byte) 0xA9);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8001, (byte) 0x3E);

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8002, (byte) 0x8D);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8003, (byte) 0x05);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8004, (byte) 0x20);

        nes.cpu.clock_tick();
        nes.cpu.clock_tick();

        assertEquals((nes.ppu.registers.loopy_t >> 5) & 0b1111111111, 0b1100100111);
        assertFalse(nes.ppu.registers.w);
    }

    @Test
    public void test_split_xy_3() {
        // TODO: Assembler needed. For now I just manually set the program bytes.
        String[] program = {"LDA #$7D", "STA $2005"};
        Cartridge cartridge = Helper.createCustomCartridge(program);

        NES nes = new NES(cartridge);

        nes.cpu.registers.PC = (short) 0x8000;
        nes.ppu.registers.loopy_t = 0b110010011100000;
        nes.ppu.registers.loopy_v = 0;
        nes.ppu.registers.fine_x_scroll = 0;
        nes.ppu.registers.w = false;

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8000, (byte) 0xA9);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8001, (byte) 0x7D);

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8002, (byte) 0x8D);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8003, (byte) 0x05);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8004, (byte) 0x20);

        nes.cpu.clock_tick();
        nes.cpu.clock_tick();

        assertEquals(nes.ppu.registers.loopy_t, 0b110010011101111);
        assertTrue(nes.ppu.registers.w);
        assertEquals(nes.ppu.registers.fine_x_scroll, 0b101);
    }

    @Test
    public void test_split_xy_4() {
        Cartridge cartridge = Helper.createDummyCartridge();
        NES nes = new NES(cartridge);

        nes.cpu.registers.PC = (short) 0x8000;
        nes.ppu.registers.loopy_t = 0b110010011101111;
        nes.ppu.registers.loopy_v = 0;
        nes.ppu.registers.fine_x_scroll = 0b101;
        nes.ppu.registers.w = true;

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8000, (byte) 0xA9);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8001, (byte) 0xEF);

        nes.cpu.bus.cpuBus.cpu_write((short) 0x8002, (byte) 0x8D);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8003, (byte) 0x06);
        nes.cpu.bus.cpuBus.cpu_write((short) 0x8004, (byte) 0x20);

        nes.cpu.clock_tick();
        nes.cpu.clock_tick();

        assertEquals(nes.ppu.registers.loopy_t, 0b110010011101111);
        assertEquals(nes.ppu.registers.loopy_v, 0b110010011101111);
        assertFalse(nes.ppu.registers.w);
        assertEquals(nes.ppu.registers.fine_x_scroll, 0b101);
    }
}
