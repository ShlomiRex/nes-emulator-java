package NES;


import NES.Bus.Bus;
import NES.CPU.CPU;
import NES.Cartridge.ROMParser;
import NES.Cartridge.iNESHeader;
import NES.PPU.PPU;

public class NES {

    public final CPU cpu;
    public PPU ppu;
    private boolean is_running;

    public final byte[] cpu_memory; // All 64KB addressable memory
    public final iNESHeader header;
    public final Bus bus;

    // We want to deal with creating the memory here, so its more manageable, and each component can take modular memory.
    public NES(ROMParser romParser) {
        iNESHeader header = romParser.getHeader();
        this.header = header;
        byte[] prg_rom = romParser.getPrg_rom();
        byte[] chr_rom = romParser.getChr_rom();

        this.cpu_memory = new byte[64 * 1024];

        //TODO: Mapper 0 only

        if (prg_rom.length == 16*1024) {
            // Mirror the PRG banks (mapper 0 only)
            // Lower bank
            System.arraycopy(prg_rom, 0, this.cpu_memory, 0x8000, 1024*16);
            // Upper bank
            System.arraycopy(prg_rom, 0, this.cpu_memory, 0xC000, 1024*16);
        } else {
            // 32KB
            System.arraycopy(prg_rom, 0, this.cpu_memory, 0x8000, 1024*32);
        }

        bus = new Bus();

        ppu = new PPU(bus, header.getMirrorType(), chr_rom);
        bus.attachPPU(ppu);

        cpu = new CPU(bus, cpu_memory);

        cpu.reset();
    }

    public void run() {
        is_running = true;
        while (is_running) {
            cpu.clock_tick();
            ppu.clock_tick();
            ppu.clock_tick();
            ppu.clock_tick();
        }
    }

    public void stop() {
        is_running = false;
    }
}
