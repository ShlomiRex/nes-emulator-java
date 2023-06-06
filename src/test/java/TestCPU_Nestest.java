import NES.CPU.AddressingMode;
import NES.CPU.CPU;
import NES.CPU.Decoder;
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
    private static Decoder decoder;
    private static NES nes;

    @BeforeClass
    public static void setUp() throws IOException, ROMParser.ParsingException {
        decoder = new Decoder();

        String test_log_path = "6502_programs/nestest/nestest.log";
        File file = new File(test_log_path);
        reader = new BufferedReader(new FileReader(file));

        String test_rom_path = "6502_programs/nestest/nestest2.nes";
        ROMParser rom_parser = new ROMParser(test_rom_path);
        nes = new NES(rom_parser);

        // We have special mode of operation, we ignore RESET vector. Only for this test.
        nes.cpu.registers.setPC((short) 0xC000);
        nes.cpu.registers.getP().setInterruptDisable(true); // Only for the first instruction to pass test.
        nes.cpu.registers.setS((byte) 0xFD); // Only for the first instruction to pass test.
    }

    @Test
    public void test() throws IOException {
        CPU cpu = nes.cpu;

        for (int i = 0; i < 10; i ++) {
            String line = reader.readLine();

            logger.debug("Running test: " + (i+1));
            logger.debug(""+line);

            // Parse line
            CPUState state = parse_nestest_log_line(line);

            // Assert before clock tick, since it changes the state of the CPU
            assertEquals(state.pc & 0xFFFF, cpu.registers.getPC() & 0xFFFF);
            assertEquals(state.cycles, cpu.cycles);
            assertEquals(state.a, cpu.registers.getA());
            assertEquals(state.x, cpu.registers.getX());
            assertEquals(state.y, cpu.registers.getY());
            assertEquals(state.p, cpu.registers.getP().getAllFlags());
            assertEquals(state.sp, cpu.registers.getS());

            // Tick
            cpu.clock_tick();
        }
    }

    private CPUState parse_nestest_log_line(String line) {
        String[] split = line.split("\\s+");

        String str_pc = split[0];
        String str_opcode = split[1];

        byte opcode = (byte) Integer.parseInt(str_opcode, 16);
        Decoder.InstructionInfo instr_info = decoder.decode_opcode(opcode);

        String str_oper1 = "  ";
        String str_oper2 = "  ";
        Byte oper1 = null;
        Byte oper2 = null;
        int split_index = 0;

        switch (instr_info.bytes) {
            case 1 -> split_index = 2;
            case 2 -> {
                split_index = 3;
                str_oper1 = split[2];
                oper1 = (byte) Integer.parseInt(str_oper1, 16);
            }
            case 3 -> {
                split_index = 4;
                str_oper1 = split[2];
                str_oper2 = split[3];
                oper1 = (byte) Integer.parseInt(str_oper1, 16);
                oper2 = (byte) Integer.parseInt(str_oper2, 16);
            }
        }

        String instr = split[split_index++];
        String addr = "";

        if (instr_info.addrmode == AddressingMode.IMPLIED) {
            //split_index++;
        } else {
            addr = split[split_index++];
        }

        String equals_to = "";
        if (instr.equals("STX")) {
            equals_to = split[split_index+1];
            split_index += 2;
        }

        String str_a = split[split_index++];
        String str_x = split[split_index++];
        String str_y = split[split_index++];
        String str_p = split[split_index++];
        String str_sp = split[split_index++];
        String ppu_txt = split[split_index++];
        String ppu_1 = split[split_index++];

        String ppu_2 = "";
        if (ppu_1.substring(ppu_1.indexOf(',')).length() > 1) {
            String[] split2 = ppu_1.split(",");
            ppu_1 = split2[0];
            ppu_2 = split2[1];
        } else {
            ppu_2 = split[split_index++];
        }

        String str_cycles = split[split_index++];

        short pc = (short) Integer.parseInt(str_pc, 16);
        byte a = (byte) Integer.parseInt(str_a.substring(2), 16);
        byte x = (byte) Integer.parseInt(str_x.substring(2), 16);
        byte y = (byte) Integer.parseInt(str_y.substring(2), 16);
        byte p = (byte) Integer.parseInt(str_p.substring(2), 16);
        byte sp = (byte) Integer.parseInt(str_sp.substring(3), 16);
        long cycles = Long.parseLong(str_cycles.substring(4));

        return new CPUState(pc, a, x, y, p, sp, cycles);
    }

    record CPUState(short pc, byte a, byte x, byte y, byte p, byte sp, long cycles) {
    }
}
