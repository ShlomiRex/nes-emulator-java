package NES.PPU;

public class PaletteRAM {
    private final byte[] palette_ram = new byte[32];

    public void write(short ppuaddr, byte value) {
        ppuaddr &= 0x3FFF;
        ppuaddr -= 0x3F00;
        palette_ram[ppuaddr & 0xFFFF] = value;
    }

    public byte read(int ppuaddr) {
        ppuaddr &= 0x3FFF;
        ppuaddr -= 0x3F00;
        return palette_ram[ppuaddr & 0xFFFF];
    }
}
