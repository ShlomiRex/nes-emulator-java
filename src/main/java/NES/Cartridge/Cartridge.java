package NES.Cartridge;

public record Cartridge(iNESHeader header, byte[] prg_rom, byte[] chr_rom) {
}
