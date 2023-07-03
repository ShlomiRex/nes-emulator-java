package NES.PPU;

import NES.Cartridge.iNESHeader;

public class VRAM {

    private final byte[] vram;

    public VRAM() {
        this.vram = new byte[1024 * 2];
    }

    public byte read(int address) {
        address &= 0x3FFF;
        return vram[address];
    }

    public void write(short address, byte value) {
        address &= 0x3FFF;
        vram[address] = value;
    }
}
