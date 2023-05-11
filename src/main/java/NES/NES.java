package NES;


import NES.CPU.CPU;
import NES.Cartridge.ROMParser;
import NES.PPU.PPU;

public class NES {

    private ROMParser romParser;
    public final CPU cpu;
    public PPU ppu;

    public final byte[] cpu_memory; // All 64KB addressable memory

    // We want to deal with creating the memory here, so its more manageable, and each component can take modular memory.
    public NES(ROMParser romParser) {
        this.romParser = romParser;
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

        // PPU pattern tables
        byte[] pattern_tables = new byte[1024 * 8];
        System.arraycopy(chr_rom, 0, pattern_tables, 0, 1024 * 8);

        ppu = new PPU(pattern_tables);
        cpu = new CPU(cpu_memory, ppu.registers);
    }
}
