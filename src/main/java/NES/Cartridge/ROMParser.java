package NES.Cartridge;

import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class ROMParser {

    private static final Logger logger = LoggerFactory.getLogger(ROMParser.class);

    public static class ParsingException extends Exception {
        public ParsingException(String message) {
            super(message);
        }
    }

    public static Cartridge parse_rom(String path) throws IOException, ParsingException {
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        logger.info("Reading ROM: " + file.getAbsolutePath());

        iNESHeader header = parseHeader(fileInputStream);
        byte[] prg_rom = parse_prg_rom(header, fileInputStream);
        byte[] chr_rom = parse_chr_rom(header, fileInputStream);
        byte[] chr_ram = null;
        if (header.chr_rom_size == 0) {
            chr_ram = new byte[8 * 1024];
            logger.info("CHR RAM size: " + chr_ram.length / 1024 + "KB");
        }

        // Here we read the rest of the file, and check that we read all the bytes.
        // We should have read all the bytes before, in parseHeader, parse_prg_rom and parse_chr_rom.
        byte[] bytes_left = fileInputStream.readAllBytes();
        if (bytes_left.length > 0)
            throw new ParsingException("Expected that I read all the bytes, however there are " +
                    bytes_left.length + " bytes left");

        return new Cartridge(header, prg_rom, chr_rom, chr_ram);
    }

    private static iNESHeader parseHeader(FileInputStream fileInputStream) throws ParsingException, IOException {
        byte[] header_bytes = fileInputStream.readNBytes(16);
        logger.debug("Read 16 header bytes");

        // Check magic bytes
        byte[] magic_bytes = Arrays.copyOfRange(header_bytes, 0, 4);
        System.arraycopy(header_bytes, 0, magic_bytes, 0, 4);
        if (!new String(magic_bytes).equals("NES\u001A")) {
            throw new ParsingException("Invalid magic bytes");
        }

        int prg_rom_size = header_bytes[4];
        int chr_rom_size = header_bytes[5];

        // Flags 6 parsing
        // -----------------
        byte flags6 = header_bytes[6];
        Mirroring mirrorType = ((flags6 & 1) == 1) ?
                (Mirroring.VERTICAL) :
                (Mirroring.HORIZONTAL);
        boolean battery_prg_ram = ((flags6 >> 1) & 1) == 1;
        boolean trainer = ((flags6 >> 2) & 1) == 1;
        boolean ignore_mirroring_control = ((flags6 >> 3) & 1) == 1;
        byte lsb_mapper = (byte) (flags6 >> 4);

        // Flags 7 parsing
        // -----------------
        byte flags7 = header_bytes[7];
        // VS unitsystem
        boolean vs_unit_system = ((flags7 & 1)) == 1;
        // PlayChoice-10 (8KB of Hint Screen data stored after CHR data)
        boolean play_choise_10 = ((flags7 >> 1) & 1) == 1;
        // NES.NES 2.0 format
        boolean nes2_format = ((flags7 >> 2) & 0b0000_0011) == 2;
        byte msb_mapper = (byte) (flags7 & 0b1111_0000);

        // Flags 8 parsing
        // -----------------
        byte flags8 = header_bytes[8];
        int prg_ram_size = flags8;

        // Flags 9 parsing
        // -----------------
        byte flags9 = header_bytes[9];
        // TV system (0: NTSC; 1: PAL)
        iNESHeader.TVSystem flags9_tv_system = (((flags9 & 1)) == 1) ?
                iNESHeader.TVSystem.PAL :
                iNESHeader.TVSystem.NTSC;
        if ((flags9 >> 1) != 0) {
            throw new ParsingException("Flags 9 reserve bits are not set to zero");
        }

        // Flags 10 parsing
        // -----------------
        byte flags10 = header_bytes[10];
        iNESHeader.TVSystem flags10_tv_system;
        {
            int first_2_bits = flags10 & 0b0000_0011;
            if (first_2_bits == 0)
                flags10_tv_system = iNESHeader.TVSystem.NTSC;
            else if (first_2_bits == 2)
                flags10_tv_system = iNESHeader.TVSystem.PAL;
            else
                flags10_tv_system = iNESHeader.TVSystem.DUAL;
        }
        // PRG RAM (0: present, 1: not present)
        boolean prg_ram_not_present = (flags10 >> 4) == 1;
        // Board bus conflicts (0: Board has no bus conflicts; 1: Board has bus conflicts)
        boolean bus_conflicts = (flags10 >> 5) == 1;


        int mapper = msb_mapper | lsb_mapper;
//        if (mapper != 0) {
//            throw new ParsingException("The emulator supports only maper 0, currently. ROM Mapper: " + mapper);
//        }

        // Create header object and print
        iNESHeader iNESHeader = new iNESHeader(prg_rom_size, chr_rom_size, mapper,
                mirrorType, battery_prg_ram, trainer, ignore_mirroring_control,
                vs_unit_system, play_choise_10, nes2_format, prg_ram_size, flags9_tv_system,
                flags10_tv_system, prg_ram_not_present, bus_conflicts);
        logger.debug(iNESHeader.toString());
        return iNESHeader;
    }

    private static byte[] parse_prg_rom(iNESHeader header, FileInputStream fileInputStream) throws IOException, ParsingException {
        int prg_rom_size_bytes = 1024 * 16 * header.prg_rom_size;

        byte[] prg_rom = fileInputStream.readNBytes(prg_rom_size_bytes);
        logger.debug("Read " + prg_rom.length + " bytes of PRG ROM");

        if (prg_rom.length != 16*1024 && prg_rom.length != 32*1024) {
            throw new ParsingException("Expected PRG ROM of size 16 or 32KB, currently only supporting mapper 0");
        }
        logger.info("PRG ROM size: " + prg_rom.length / 1024 + "KB");

        byte[] first_16_bytes = Arrays.copyOfRange(prg_rom, 0, 16);
        logger.info("First 16 bytes of PRG ROM: " +
                Common.bytesToHex(first_16_bytes, Common.BytesToHexStringFormat.ARRAY, false));

        byte[] last_16_bytes = Arrays.copyOfRange(prg_rom, prg_rom.length - 16, prg_rom.length);
        logger.info("Last 16 bytes of PRG ROM: " +
                Common.bytesToHex(last_16_bytes, Common.BytesToHexStringFormat.ARRAY, false));

        return prg_rom;
    }

    private static byte[] parse_chr_rom(iNESHeader header, FileInputStream fileInputStream) throws ParsingException, IOException {
        int chr_rom_bytes = 1024 * 8 * header.chr_rom_size;
        logger.debug("CHR ROM size: " + chr_rom_bytes/1024 + "KB");

//        if (chr_rom_bytes != 8*1024) {
//            throw new ParsingException("Expected CHR ROM of size 8KB in mapper 0.");
//        }

        byte[] chr_rom = fileInputStream.readNBytes(chr_rom_bytes);
        logger.debug("Read " + chr_rom_bytes + " bytes of CHR ROM");
        return chr_rom;
    }
}
