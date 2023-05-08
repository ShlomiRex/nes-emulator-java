package NES;


import NES.CPU.CPU;

public class NES {

    private ROMParser romParser;
    public CPU cpu;

    private byte[] cpu_memory; // All 64KB addressable memory

    public NES(ROMParser romParser) {
        this.romParser = romParser;

        this.cpu_memory = new byte[64 * 1024];
        // Mirror the PRG banks (mapper 0 only)
        // Lower bank
        System.arraycopy(romParser.getPrg_rom(), 0, this.cpu_memory, 0x8000, 1024*16);
        // Upper bank
        System.arraycopy(romParser.getPrg_rom(), 0, this.cpu_memory, 0xC000, 1024*16);

        cpu = new CPU(cpu_memory);
    }
}
