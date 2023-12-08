package NES.Cartridge;

import java.util.Optional;

public record Cartridge(iNESHeader header,
                        byte[] prg_rom,
                        byte[] chr_rom,
                        byte[] chr_ram) {
}
