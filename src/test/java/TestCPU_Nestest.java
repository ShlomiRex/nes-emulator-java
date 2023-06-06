import NES.CPU.CPU;
import NES.CPU.Decoder;
import NES.Cartridge.ROMParser;
import NES.Common;
import NES.NES;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCPU_Nestest {

    private final String test_rom_path = "6502_programs/nestest/nestest2.nes";
    private final String test_log_path = "6502_programs/nestest/nestest.log";
    private static final Logger logger = LoggerFactory.getLogger(TestCPU_Nestest.class);

    @Test
    public void test() throws IOException, ROMParser.ParsingException {
        File file = new File(test_log_path);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        ROMParser rom_parser = new ROMParser(test_rom_path);
        NES nes = new NES(rom_parser);
        CPU cpu = nes.cpu;

        Decoder decoder = new Decoder();

        // We have special mode of operation, we ignore RESET vector. Only for this test.
        nes.cpu.registers.setPC((short) 0xC000);
        nes.cpu.registers.getP().setInterruptDisable(true); // Only for the first instruction to pass test.
        nes.cpu.registers.setS((byte) 0xFD); // Only for the first instruction to pass test.

        for (int i = 0; i < 2; i ++) {
            String line = reader.readLine();
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
                case 1 -> split_index = 6;
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
            String addr = split[split_index++];
            String str_a = split[split_index++];
            String str_x = split[split_index++];
            String str_y = split[split_index++];
            String str_p = split[split_index++];
            String str_sp = split[split_index++];
            String ppu_txt = split[split_index++];
            String ppu_1 = split[split_index++];
            String ppu_2 = split[split_index++];
            String cycles = split[split_index++];

            logger.debug(""+line);
            logger.debug(""+str_pc+" "+opcode+" "+str_oper1+" "+str_oper2);

            short pc = (short) Integer.parseInt(str_pc, 16);
            byte a = (byte) Integer.parseInt(str_a.substring(2), 16);
            byte x = (byte) Integer.parseInt(str_x.substring(2), 16);
            byte y = (byte) Integer.parseInt(str_y.substring(2), 16);
            byte p = (byte) Integer.parseInt(str_p.substring(2), 16);
            byte sp = (byte) Integer.parseInt(str_sp.substring(3), 16);


            // Assert PC before clock tick since it is changed after the tick
            assertEquals(pc & 0xFFFF, cpu.registers.getPC() & 0xFFFF);

            // Initialize the memory needed for executing the next instruction
            //init_memory_before_execution(pc, opcode, oper1, oper2);
            cpu.clock_tick();

            assertEquals(a, cpu.registers.getA());
            assertEquals(x, cpu.registers.getX());
            assertEquals(y, cpu.registers.getY());
            assertEquals(p, cpu.registers.getP().getAllFlags());
            assertEquals(sp, cpu.registers.getS());
        }
    }

//    private void init_memory_before_execution(short pc_addr, byte opcode, Byte oper1, Byte oper2) {
//        cpu_memory[pc_addr & 0xFFFF] = opcode;
//        if (oper1 != null) cpu_memory[(pc_addr & 0xFFFF) + 1] = oper1;
//        if (oper2 != null) cpu_memory[(pc_addr & 0xFFFF) + 2] = oper2;
//    }
}
