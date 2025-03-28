package CPU;

import NES.CPU.CPU;
import NES.CPU.Decoder.Decoder;
import NES.CPU.Registers.Flags;
import NES.Cartridge.Cartridge;
import NES.Cartridge.ROMParser;
import NES.NES;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCPU_Nestest {

    private static final Logger logger = LoggerFactory.getLogger(TestCPU_Nestest.class);

    private static BufferedReader reader;
    private static NES nes;

    @BeforeClass
    public static void setUp() throws IOException, ROMParser.ParsingException {
        String test_log_path = "6502_programs/nestest/nestest.log";
        File file = new File(test_log_path);
        reader = new BufferedReader(new FileReader(file));

        String test_rom_path = "6502_programs/nestest/nestest2.nes";
        Cartridge cartridge = ROMParser.parse_rom(test_rom_path);
        nes = new NES(cartridge);

        // We have special mode of operation, we ignore RESET vector. Only for this test.
        nes.cpu.registers.PC = (short) 0xC000;
        nes.cpu.registers.setFlag(Flags.INTERRUPT, true); // Only for the first instruction to pass test.
        nes.cpu.registers.S = (byte) 0xFD; // Only for the first instruction to pass test.
    }

    @Test
    public void test() throws IOException {
        CPU cpu = nes.cpu;

        // Assert first line (before executing the line)
        String line = reader.readLine();
        logger.debug("Running test: " + 0);
        logger.debug(line);
        CPUState state = parse_nestest_log_line(line);

        // Assert CPU state
        assertEquals(state.pc, cpu.registers.PC);
        assertEquals(state.cycles, cpu.cycles);
        assertEquals(state.a, cpu.registers.A);
        assertEquals(state.x, cpu.registers.X);
        assertEquals(state.y, cpu.registers.Y);
        assertEquals(state.p, cpu.registers.P);
        assertEquals(state.sp, cpu.registers.S);

        for (int i = 1; i < 10000; i ++) {
            // Tick, execute line
            cpu.clock_tick();

            // Read next state
            logger.debug("Running test: {}", i);
            logger.debug(line);
            line = reader.readLine();

            // Parse line
            state = parse_nestest_log_line(line);

            // Assert CPU state
            assertEquals(state.pc, cpu.registers.PC);
            assertEquals(state.cycles, cpu.cycles);
            assertEquals(state.a, cpu.registers.A);
            assertEquals(state.x, cpu.registers.X);
            assertEquals(state.y, cpu.registers.Y);
            assertEquals(state.p, cpu.registers.P);
            assertEquals(state.sp, cpu.registers.S);
        }
    }

    private CPUState parse_nestest_log_line(String line) {
        int index_pc = line.indexOf(" ");
        int index_a = line.indexOf("A:");
        int index_x = line.indexOf("X:");
        int index_y = line.indexOf("Y:");
        int index_p = line.indexOf("P:");
        int index_sp = line.indexOf("SP:");
        boolean is_illegal = line.contains("*");

        String str_pc = line.substring(0, index_pc);
        String str_a = line.substring(index_a, index_a+4);
        String str_x = line.substring(index_x, index_x+4);
        String str_y = line.substring(index_y, index_y+4);
        String str_p = line.substring(index_p, index_p+4);
        String str_sp = line.substring(index_sp, index_sp+5);
        String ppu_txt = line.substring(line.indexOf("PPU:"), line.indexOf("CYC"));
        String str_cycles = line.substring(line.indexOf("CYC:"));

        short pc = (short) Integer.parseInt(str_pc, 16);
        byte a = (byte) Integer.parseInt(str_a.substring(2), 16);
        byte x = (byte) Integer.parseInt(str_x.substring(2), 16);
        byte y = (byte) Integer.parseInt(str_y.substring(2), 16);
        byte p = (byte) Integer.parseInt(str_p.substring(2), 16);
        byte sp = (byte) Integer.parseInt(str_sp.substring(3), 16);
        long cycles = Long.parseLong(str_cycles.substring(4));

        return new CPUState(pc, a, x, y, p, sp, cycles, is_illegal);
    }

    record CPUState(short pc, byte a, byte x, byte y, byte p, byte sp, long cycles, boolean is_illegal) {
    }
}
