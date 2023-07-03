package NES.PPU;

public class CHR_ROM {
    private final byte[] chr_rom;

    public CHR_ROM(byte[] chr_rom) {
        this.chr_rom = chr_rom;
    }

    public byte read(int address) {
        address &= 0x1FFF;
        return chr_rom[address];
    }
}
