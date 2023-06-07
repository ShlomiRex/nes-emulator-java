import NES.CPU.CPU;
import NES.CPU.Decoder;
import NES.CPU.Registers.StatusFlags;
import NES.Common;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Using the following git repository: <a href="https://github.com/TomHarte/ProcessorTests">...</a>
 * We test on each opcode that the output of the CPU matches the output of the JSON test.
 * The git repository must be cloned into the 'test_resources/' folder which is in the root folder of the project.
 */
public class TestCPU {
    private static final Logger logger = LoggerFactory.getLogger(TestCPU.class);

    private static CPU cpu;
    private static byte[] cpu_memory;

    @BeforeAll
    public static void setUp() {
        Path path = Paths.get("test_resources/ProcessorTests");
        boolean processor_tests_repo_exist = Files.exists(path);
        if (!processor_tests_repo_exist)
            logger.error("ProcessorTests repo not found (https://github.com/TomHarte/ProcessorTests), skipping tests");
        assumeTrue(processor_tests_repo_exist);

        cpu_memory = new byte[64 * 1024];
        cpu = new CPU(cpu_memory, null);
        cpu.set_debugger_record_memory(true);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("test_cases_by_type_of_instruction")
    public void test_by_type_of_instruction(String test_name, byte opcode) throws IOException {
        logger.debug("Running test: " + test_name + " " + Common.byteToHex(opcode, true));
        cpu_tests_by_opcode(opcode);
    }

    @Test
    public void custom_test() throws IOException {
//        // All LDA tests
//        test_by_type_of_instruction("LDA", (byte) 0xA9);
//        test_by_type_of_instruction("LDA", (byte) 0xA5);
//        test_by_type_of_instruction("LDA", (byte) 0xB5);
//        test_by_type_of_instruction("LDA", (byte) 0xAD);
//        test_by_type_of_instruction("LDA", (byte) 0xBD);
//        test_by_type_of_instruction("LDA", (byte) 0xB9);
//        test_by_type_of_instruction("LDA", (byte) 0xA1);
//        test_by_type_of_instruction("LDA", (byte) 0xB1);
//
//        // All ADC tests
//        test_by_type_of_instruction("ADC", (byte) 0x69);
//        test_by_type_of_instruction("ADC", (byte) 0x65);
//        test_by_type_of_instruction("ADC", (byte) 0x75);
//        test_by_type_of_instruction("ADC", (byte) 0x6D);
//        test_by_type_of_instruction("ADC", (byte) 0x7D);
//        test_by_type_of_instruction("ADC", (byte) 0x79);
//        test_by_type_of_instruction("ADC", (byte) 0x61);
//        test_by_type_of_instruction("ADC", (byte) 0x71);
//
//        // All CLI tests
//        test_by_type_of_instruction("CLI", (byte) 0x58);
//
//        // All STA tests
//        test_by_type_of_instruction("STA", (byte) 0x85);
//        test_by_type_of_instruction("STA", (byte) 0x95);
//        test_by_type_of_instruction("STA", (byte) 0x8D);
//        test_by_type_of_instruction("STA", (byte) 0x9D);
//        test_by_type_of_instruction("STA", (byte) 0x99);
//        test_by_type_of_instruction("STA", (byte) 0x81);
//        test_by_type_of_instruction("STA", (byte) 0x91);
//
//        // All TAX tests
//        test_by_type_of_instruction("TAX", (byte) 0xAA);
//
//        // All CPX tests
//        test_by_type_of_instruction("CPX", (byte) 0xE0);
//        test_by_type_of_instruction("CPX", (byte) 0xE4);
//        test_by_type_of_instruction("CPX", (byte) 0xEC);
//
//        // All CLV tests
//        test_by_type_of_instruction("CLV", (byte) 0xB8);
//
//        // All TAY tests
//        test_by_type_of_instruction("TAY", (byte) 0xA8);
//
//        // All CPY tests
//        test_by_type_of_instruction("CPY", (byte) 0xC0);
//        test_by_type_of_instruction("CPY", (byte) 0xC4);
//        test_by_type_of_instruction("CPY", (byte) 0xCC);
//
//        // All STX tests
//        test_by_type_of_instruction("STX", (byte) 0x86);
//        test_by_type_of_instruction("STX", (byte) 0x96);
//        test_by_type_of_instruction("STX", (byte) 0x8E);
//
//        // All STY tests
//        test_by_type_of_instruction("STY", (byte) 0x84);
//        test_by_type_of_instruction("STY", (byte) 0x94);
//        test_by_type_of_instruction("STY", (byte) 0x8C);
//
//        // all BCC tests
//        test_by_type_of_instruction("BCC", (byte) 0x90);
//
//        // all CMP tests
//        test_by_type_of_instruction("CMP", (byte) 0xC9);
//        test_by_type_of_instruction("CMP", (byte) 0xC5);
//        test_by_type_of_instruction("CMP", (byte) 0xD5);
//        test_by_type_of_instruction("CMP", (byte) 0xCD);
//        test_by_type_of_instruction("CMP", (byte) 0xDD);
//        test_by_type_of_instruction("CMP", (byte) 0xD9);
//        test_by_type_of_instruction("CMP", (byte) 0xC1);
//        test_by_type_of_instruction("CMP", (byte) 0xD1);
//
//        // All JSR tests
//        test_by_type_of_instruction("JSR", (byte) 0x20);
//
//        // All LSR tests
//        test_by_type_of_instruction("LSR", (byte) 0x46);
//        test_by_type_of_instruction("LSR", (byte) 0x4A);
//        test_by_type_of_instruction("LSR", (byte) 0x4E);
//        test_by_type_of_instruction("LSR", (byte) 0x56);
//        test_by_type_of_instruction("LSR", (byte) 0x5E);
//
//        // All ROL tests
//        test_by_type_of_instruction("ROL", (byte) 0x26);
//        test_by_type_of_instruction("ROL", (byte) 0x2A);
//        test_by_type_of_instruction("ROL", (byte) 0x2E);
//        test_by_type_of_instruction("ROL", (byte) 0x36);
//        test_by_type_of_instruction("ROL", (byte) 0x3E);
//
//        // All NOP tests
        // TODO: Fix this, need to pass NOP tests!
//        test_by_type_of_instruction("NOP", (byte) 0x7C);
//        test_by_type_of_instruction("NOP", (byte) 0xEA);
//
//        // All SEC tests
//        test_by_type_of_instruction("SEC", (byte) 0x38);
//
//        // All BCS tests
//        test_by_type_of_instruction("BCS", (byte) 0xB0);
//
//        // All SED tests
//        test_by_type_of_instruction("SED", (byte) 0xF8);
//
//        // All ROR tests
//        test_by_type_of_instruction("ROR", (byte) 0x66);
//        test_by_type_of_instruction("ROR", (byte) 0x6A);
//        test_by_type_of_instruction("ROR", (byte) 0x6E);
//        test_by_type_of_instruction("ROR", (byte) 0x76);
//        test_by_type_of_instruction("ROR", (byte) 0x7E);
//
//        // All SEI tests
//        test_by_type_of_instruction("SEI", (byte) 0x78);
//
//        // All AND tests
//        test_by_type_of_instruction("AND", (byte) 0x29);
//        test_by_type_of_instruction("AND", (byte) 0x25);
//        test_by_type_of_instruction("AND", (byte) 0x35);
//        test_by_type_of_instruction("AND", (byte) 0x2D);
//        test_by_type_of_instruction("AND", (byte) 0x3D);
//        test_by_type_of_instruction("AND", (byte) 0x39);
//        test_by_type_of_instruction("AND", (byte) 0x21);
//        test_by_type_of_instruction("AND", (byte) 0x31);
//
//        // All TSX tests
//        test_by_type_of_instruction("TSX", (byte) 0xBA);
//
//        // All TXA tests
//        test_by_type_of_instruction("TXA", (byte) 0x8A);
//
//        // All ORA tests
//        test_by_type_of_instruction("ORA", (byte) 0x09);
//        test_by_type_of_instruction("ORA", (byte) 0x05);
//        test_by_type_of_instruction("ORA", (byte) 0x15);
//        test_by_type_of_instruction("ORA", (byte) 0x0D);
//        test_by_type_of_instruction("ORA", (byte) 0x1D);
//        test_by_type_of_instruction("ORA", (byte) 0x19);
//        test_by_type_of_instruction("ORA", (byte) 0x01);
//        test_by_type_of_instruction("ORA", (byte) 0x11);
//
//        // All BPL tests
//        test_by_type_of_instruction("BPL", (byte) 0x10);
//
//        // All INC tests
//        test_by_type_of_instruction("INC", (byte) 0xE6);
//        test_by_type_of_instruction("INC", (byte) 0xF6);
//        test_by_type_of_instruction("INC", (byte) 0xEE);
//        test_by_type_of_instruction("INC", (byte) 0xFE);
//
//        // All PLA tests
//        test_by_type_of_instruction("PLA", (byte) 0x68);
//
//        // All RTI tests
        // TODO: Need to fix RTI tests!
        test_by_type_of_instruction("RTI", (byte) 0x40);
//
//        // All PHA tests
//        test_by_type_of_instruction("PHA", (byte) 0x48);
//
//      // All LDX tests
//        test_by_type_of_instruction("LDX", (byte) 0xA2);
//        test_by_type_of_instruction("LDX", (byte) 0xA6);
//        test_by_type_of_instruction("LDX", (byte) 0xB6);
//        test_by_type_of_instruction("LDX", (byte) 0xAE);
//        test_by_type_of_instruction("LDX", (byte) 0xBE);
//
//        // All BEQ tests
//        test_by_type_of_instruction("BEQ", (byte) 0xF0);
//
//        // All RTS tests
//        test_by_type_of_instruction("RTS", (byte) 0x60);
//
//        // All JMP tests
//        test_by_type_of_instruction("JMP", (byte) 0x4C);
//        test_by_type_of_instruction("JMP", (byte) 0x6C);
//
//        // All PHA tests
//        test_by_type_of_instruction("PHA", (byte) 0x48);
//
//        // All PHP tests
//        test_by_type_of_instruction("PHP", (byte) 0x08);
//
//        // All PLA tests
//        test_by_type_of_instruction("PLA", (byte) 0x68);
//
//        // All PLP tests
//        test_by_type_of_instruction("PLP", (byte) 0x28);
//
//        // All SBC tests
//        test_by_type_of_instruction("SBC", (byte) 0xE9);
//        test_by_type_of_instruction("SBC", (byte) 0xE5);
//        test_by_type_of_instruction("SBC", (byte) 0xF5);
//        test_by_type_of_instruction("SBC", (byte) 0xED);
//        test_by_type_of_instruction("SBC", (byte) 0xFD);
//        test_by_type_of_instruction("SBC", (byte) 0xF9);
//        test_by_type_of_instruction("SBC", (byte) 0xE1);
//        test_by_type_of_instruction("SBC", (byte) 0xF1);
//
//        // All ASL tests
//        test_by_type_of_instruction("ASL", (byte) 0x0A);
//        test_by_type_of_instruction("ASL", (byte) 0x06);
//        test_by_type_of_instruction("ASL", (byte) 0x16);
//        test_by_type_of_instruction("ASL", (byte) 0x0E);
//        test_by_type_of_instruction("ASL", (byte) 0x1E);


        // custom




    }

    private static Stream<Arguments> test_cases_by_type_of_instruction() {
        HashMap<String, List<Byte>> instr_by_type = get_instructions_by_type();
        List<Arguments> args = new ArrayList<>();

        for (String instr_type : instr_by_type.keySet()) {
            for (Byte opcode : instr_by_type.get(instr_type)) {
                String test_name = "Test: " + instr_type + " opcode: " + Common.byteToHex(opcode, true);
                args.add(Arguments.of(test_name, opcode));
            }
        }

        return args.stream();
    }

    public static HashMap<String, List<Byte>> get_instructions_by_type() {
        HashMap<String, List<Byte>> instr_by_type = new HashMap<>();

        for(int opcode = 0; opcode < 255; opcode++) {
            Decoder.InstructionInfo instr_info = Decoder.instructions_table[opcode & 0xFF];
            if (instr_info == null)
                continue;
            if (instr_by_type.get(instr_info.instr.toString()) == null) {
                List<Byte> opcodes = new ArrayList<>();
                opcodes.add((byte) opcode);
                instr_by_type.put(instr_info.instr.toString(), opcodes);
            } else {
                instr_by_type.get(instr_info.instr.toString()).add((byte) opcode);
            }
        }

        return instr_by_type;
    }

    public void cpu_tests_by_opcode(byte opcode) throws IOException {
        JSONArray test = read_test(opcode);
        
        for (int i = 0; i < test.length(); i++) {
            JSONObject test_obj = test.getJSONObject(i);
            String test_name = (String) test_obj.get("name");
            logger.debug("Running test: " + (i+1) +"/"+test.length() + ", test name: " + test_name);

            JSONObject initial = (JSONObject) test_obj.get("initial");
            init_cpu(cpu, cpu_memory, initial);

            cpu.clock_tick(); // Single tick

            JSONObject final_test = (JSONObject) test_obj.get("final");
            JSONArray cycles = (JSONArray) test_obj.get("cycles");
            assert_final(cpu, cpu_memory, final_test, cycles);
        }
    }

    private JSONArray read_test(int opcode) throws IOException {
        String opcode_hex = Common.byteToHex((byte) opcode, false);
        Path path = Paths.get("test_resources/ProcessorTests/nes6502", "v1", opcode_hex + ".json");
        //logger.debug("Reading test file: " + path);
        String jsonContent = new String(Files.readAllBytes(path));
        return new JSONArray(jsonContent);
    }

    private void init_cpu(CPU cpu, byte[] cpu_memory, JSONObject init) {
        // Clear memory access records for next test
        cpu.clear_debugger_memory_records();

        Integer pc = (Integer) init.get("pc");
        Integer a = (Integer) init.get("a");
        Integer x = (Integer) init.get("x");
        Integer y = (Integer) init.get("y");
        Integer p = (Integer) init.get("p");
        Integer s = (Integer) init.get("s");
        JSONArray ram = (JSONArray) init.get("ram");

        // Init CPU registers
        cpu.registers.setPC(pc.shortValue());
        cpu.registers.setA(a.byteValue());
        cpu.registers.setX(x.byteValue());
        cpu.registers.setY(y.byteValue());
        cpu.registers.setP(new StatusFlags((byte) p.intValue()));
        cpu.registers.setS(s.byteValue());

        // Init the RAM
        for (int i = 0; i < ram.length(); i++) {
            JSONArray obj = (JSONArray) ram.get(i);

            Integer address = (Integer) obj.get(0);
            Integer value = (Integer) obj.get(1);

//            logger.debug("Setting memory: [" + address +
//                    " ("+Common.shortToHex(address.shortValue(), true)+")] = " +
//                    value + " ("+Common.byteToHex(value.byteValue(), true)+")");

            cpu_memory[address] = value.byteValue();
        }
    }

    private void assert_final(CPU cpu, byte[] cpu_memory, JSONObject final_result, JSONArray cycles) {
        Integer pc = (Integer) final_result.get("pc");
        Integer a = (Integer) final_result.get("a");
        Integer x = (Integer) final_result.get("x");
        Integer y = (Integer) final_result.get("y");
        Integer p = (Integer) final_result.get("p");
        Integer s = (Integer) final_result.get("s");
        JSONArray ram = (JSONArray) final_result.get("ram");

        // Test CPU flags
        assertEquals(pc.shortValue(), cpu.registers.getPC());
        assertEquals(a.byteValue(), cpu.registers.getA());
        assertEquals(x.byteValue(), cpu.registers.getX());
        assertEquals(y.byteValue(), cpu.registers.getY());
        assertEquals(p.byteValue(), cpu.registers.getP().getAllFlags());
        assertEquals(s.byteValue(), cpu.registers.getS());

        // Test ram
        for (int i = 0; i < ram.length(); i++) {
            JSONArray obj = (JSONArray) ram.get(i);

            Integer address = (Integer) obj.get(0);
            Integer value = (Integer) obj.get(1);

            assertEquals(value.byteValue(), cpu_memory[address]);
        }

        // Test cycles (Note: order of memory access records is important).
        // This shows that my emulator is cycle accurate.
        List<CPU.MemoryAccessRecord> records = cpu.get_debugger_memory_records();
        assertEquals(cycles.length(), records.size());
        for (int i = 0; i < cycles.length(); i++) {
            JSONArray cycle_record = (JSONArray) cycles.get(i);
            CPU.MemoryAccessRecord cpu_record = records.get(i);

            Integer cycle_address = (Integer) cycle_record.get(0);
            Integer cycle_value = (Integer) cycle_record.get(1);
            String cycle_type = (String) cycle_record.get(2);

            assertEquals(cycle_address.shortValue(), cpu_record.addr());
            assertEquals(cycle_value.byteValue(), cpu_record.value());
            assertEquals(cycle_type.equals("read"), cpu_record.is_read());
        }
    }
}
