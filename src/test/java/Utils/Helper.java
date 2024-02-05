package Utils;

import NES.Assembler.Assembler;
import NES.Cartridge.Cartridge;
import NES.Cartridge.Mirroring;
import NES.Cartridge.iNESHeader;

public class Helper {
    public static Cartridge createCustomCartridge(String[] program) {
        byte[] prg_rom = Assembler.assemble(program);
        return new Cartridge(dummyiNESHeader(), prg_rom, new byte[1024 * 8], null);
    }

    public static Cartridge createDummyCartridge() {
        return new Cartridge(dummyiNESHeader(), new byte[1024 * 16], new byte[1024 * 8], null);
    }

    public static iNESHeader dummyiNESHeader() {
        return new iNESHeader(
                1,
                1,
                0,
                Mirroring.HORIZONTAL,
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
