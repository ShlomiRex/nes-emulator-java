package NES;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class ROMParser {

    private final Logger logger = LoggerFactory.getLogger(ROMParser.class);

    private final iNESHeader header;
    private final byte[] prg_rom; // Size: 16KB exactly, because I only intend to support mapper 0 only. No banks needed
    private final byte[] chr_rom; // Size: 8KB exactly, like above.

    class ParsingException extends Exception {
        public ParsingException(String message) {
            super(message);
        }
    }

    public ROMParser(String path) throws IOException, ParsingException {
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        header = parseHeader(fileInputStream);
        prg_rom = parse_prg_rom(fileInputStream);
        chr_rom = parse_chr_rom(fileInputStream);

        byte[] bytes_left = fileInputStream.readAllBytes();
        if (bytes_left.length > 0)
            throw new ParsingException("Expected that I read all the bytes, however there are " +
                    bytes_left.length +" bytes left");
    }

    private iNESHeader parseHeader(FileInputStream fileInputStream) throws ParsingException, IOException {
        byte[] header_bytes = fileInputStream.readNBytes(16);

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
        iNESHeader.Mirroring mirrorType = ((flags6 & 1) == 1) ?
                (iNESHeader.Mirroring.VERTICAL) :
                (iNESHeader.Mirroring.HORIZONTAL);
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
        if (mapper != 0) {
            throw new ParsingException("The emulator supports only maper 0, currently.");
        }

        // Create header object and print
        iNESHeader iNESHeader = new iNESHeader(prg_rom_size, chr_rom_size, mapper,
                mirrorType, battery_prg_ram, trainer, ignore_mirroring_control,
                vs_unit_system, play_choise_10, nes2_format, prg_ram_size, flags9_tv_system,
                flags10_tv_system, prg_ram_not_present, bus_conflicts);
        logger.debug(iNESHeader.toString());
        return iNESHeader;
    }

    private byte[] parse_prg_rom(FileInputStream fileInputStream) throws IOException, ParsingException {
        int prg_rom_size_bytes = 1024 * 16 * this.header.prg_rom_size;

        byte[] prg_rom = fileInputStream.readNBytes(prg_rom_size_bytes);
        if (prg_rom.length != 16*1024) {
            throw new ParsingException("Expected PRG ROM of size 16KB, currently only supporting mapper 0");
        }
        logger.info("PRG ROM size: " + prg_rom.length / 1024 + "KB");

        byte[] first_16_bytes = Arrays.copyOfRange(prg_rom, 0, 16);
        logger.info("First 16 bytes of PRG ROM: " +
                Common.bytesToHexString(first_16_bytes, Common.BytesToHexStringFormat.ARRAY, false));

        byte[] last_16_bytes = Arrays.copyOfRange(prg_rom, prg_rom.length - 16, prg_rom.length);
        logger.info("Last 16 bytes of PRG ROM: " +
                Common.bytesToHexString(last_16_bytes, Common.BytesToHexStringFormat.ARRAY, false));

        return prg_rom;
    }

    private byte[] parse_chr_rom(FileInputStream fileInputStream) throws ParsingException, IOException {
        int chr_rom_bytes = 1024 * 8 * header.chr_rom_size;
        logger.debug("CHR ROM size: " + chr_rom_bytes/1024 + "KB");
        if (chr_rom_bytes != 8*1024) {
            throw new ParsingException("Expected CHR ROM of size 8KB in mapper 0.");
        }

        byte[] chr_rom = fileInputStream.readNBytes(8 * 1024);
        return chr_rom;
    }

    public byte[] getPrg_rom() {
        return prg_rom;
    }

    public iNESHeader getHeader() {
        return header;
    }

    public byte[] getChr_rom() {
        return chr_rom;
    }
}
