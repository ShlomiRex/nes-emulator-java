import NES.CPU.CPU;
import NES.CPU.Registers.StatusFlags;
import NES.Common;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.json.JSONObject;

/**
 * Using the following git repository: https://github.com/TomHarte/ProcessorTests
 * We test on each opcode that the output of the CPU matches the output of the JSON test.
 */
public class TestCPU {
    private static Logger logger = LoggerFactory.getLogger(TestCPU.class);

    private static boolean processor_tests_repo_exist = false;

    private static CPU cpu;
    private static byte[] cpu_memory;


    @BeforeClass
    public static void setUp() {
        Path path = Paths.get("test_resources/ProcessorTests");
        processor_tests_repo_exist = Files.exists(path);

        cpu_memory = new byte[64 * 1024];
        cpu = new CPU(cpu_memory, null);
        cpu.set_debugger_record_memory(true);
    }

    @Before
    public void before() {
        if (!processor_tests_repo_exist)
            logger.error("ProcessorTests repo not found (https://github.com/TomHarte/ProcessorTests), skipping tests");
        assumeTrue(processor_tests_repo_exist);
    }

//    @Test
//    public void test_opcode_SEI() throws IOException {
//        byte[] cpu_memory = new byte[64 * 1024];
//        CPU cpu = new CPU(cpu_memory, null);
//
//        int opcode = 0x78;
//
//        JSONArray test = read_test(opcode);
//
//        for (int i = 0; i < test.length(); i++) {
//            JSONObject test_obj = test.getJSONObject(i);
//
//            JSONObject initial = (JSONObject) test_obj.get("initial");
//            init_cpu(cpu, cpu_memory, initial);
//
//            cpu.clock_tick(); // Single tick
//
//            JSONObject final_test = (JSONObject) test_obj.get("final");
//            assert_final(cpu, cpu_memory, final_test);
//        }
//    }

    @Test
    public void test_opcode_STA_absolute() throws IOException {
        int opcode = 0x8D;
        run_test(opcode, cpu, cpu_memory);
    }

    private JSONArray read_test(int opcode) throws IOException {
        String opcode_hex = Common.byteToHexString((byte) opcode, false);
        Path path = Paths.get("test_resources/ProcessorTests/nes6502", "v1", opcode_hex + ".json");
        String jsonContent = new String(Files.readAllBytes(path));
        return new JSONArray(jsonContent);
    }

    private void init_cpu(CPU cpu, byte[] cpu_memory, JSONObject init) {
        cpu.clear_debugger_memory_records();

        Integer pc = (Integer) init.get("pc");
        Integer a = (Integer) init.get("a");
        Integer x = (Integer) init.get("x");
        Integer y = (Integer) init.get("y");
        Integer p = (Integer) init.get("p");
        Integer s = (Integer) init.get("s");
        JSONArray ram = (JSONArray) init.get("ram");

        cpu.registers.setPC(pc.shortValue());
        cpu.registers.setA(a.byteValue());
        cpu.registers.setX(x.byteValue());
        cpu.registers.setY(y.byteValue());
        cpu.registers.setP(new StatusFlags((byte) p.intValue()));
        cpu.registers.setS(s.byteValue());

        for (int i = 0; i < ram.length(); i++) {
            JSONArray obj = (JSONArray) ram.get(i);

            Integer address = (Integer) obj.get(0);
            Integer value = (Integer) obj.get(1);

            cpu_memory[address] = value.byteValue();
        }
    }

    private void run_test(int opcode, CPU cpu, byte[] cpu_memory) throws IOException {
        JSONArray test = read_test(opcode);

        for (int i = 0; i < test.length(); i++) {
            JSONObject test_obj = test.getJSONObject(i);

            JSONObject initial = (JSONObject) test_obj.get("initial");
            init_cpu(cpu, cpu_memory, initial);

            cpu.clock_tick(); // Single tick

            JSONObject final_test = (JSONObject) test_obj.get("final");
            JSONArray cycles = (JSONArray) test_obj.get("cycles");
            assert_final(cpu, cpu_memory, final_test, cycles);
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
        assertEquals(cpu.registers.getPC(), pc.shortValue());
        assertEquals(cpu.registers.getA(), a.byteValue());
        assertEquals(cpu.registers.getX(), x.byteValue());
        assertEquals(cpu.registers.getY(), y.byteValue());
        assertEquals(cpu.registers.getP().getAllFlags(), p.byteValue());
        assertEquals(cpu.registers.getS(), s.byteValue());

        // Test ram
        for (int i = 0; i < ram.length(); i++) {
            JSONArray obj = (JSONArray) ram.get(i);

            Integer address = (Integer) obj.get(0);
            Integer value = (Integer) obj.get(1);

            assertEquals(cpu_memory[address], value.byteValue());
        }

        // Test cycles
        List<CPU.MemoryAccessRecord> records = cpu.get_debugger_memory_records();
        assertEquals(records.size(), cycles.length());

        for (int i = 0; i < cycles.length(); i++) {
            JSONArray cycle_record = (JSONArray) cycles.get(i);

            Integer address = (Integer) cycle_record.get(0);
            Integer value = (Integer) cycle_record.get(1);
            String type = (String) cycle_record.get(2);

            AtomicBoolean found_record = new AtomicBoolean(false);
            records.forEach(record -> {
                if (record.addr() == address.shortValue()) {
                    assertEquals(record.value(), value.byteValue());
                    boolean is_read = type.equals("read");
                    assertEquals(record.is_read(), is_read);

                    found_record.set(true);
                }
            });
            assertTrue(found_record.get());
        }
    }
}
