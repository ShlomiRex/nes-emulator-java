package Utils;

import NES.Assembler.Assembler;
import NES.Cartridge.Cartridge;
import NES.Cartridge.Mirroring;
import NES.Cartridge.iNESHeader;

public class Helper {
    public static Cartridge createCustomCartridge(String[] program) {
        byte[] prg_rom = Assembler.assemble(program);
        return new Cartridge(createDummyiNESHeader(), prg_rom, new byte[1024 * 8], null);
    }

    public static Cartridge createDummyCartridge() {
        return new Cartridge(createDummyiNESHeader(), new byte[1024 * 16], new byte[1024 * 8], null);
    }

    public static Cartridge createDummyCartridge(iNESHeader header) {
        return new Cartridge(header, new byte[1024 * 16], new byte[1024 * 8], null);
    }

    public static iNESHeader createDummyiNESHeader() {
        return createDummyiNESHeader(Mirroring.HORIZONTAL);
    }

    public static iNESHeader createDummyiNESHeader(Mirroring mirroring) {
        return new iNESHeader(
                1,
                1,
                0,
                mirroring,
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
    }
}
