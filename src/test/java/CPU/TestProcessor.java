package CPU;

import NES.Bus.Bus;
import NES.Bus.CPUBus;
import NES.Bus.MemoryAccessRecord;
import NES.CPU.CPU;
import NES.CPU.Decoder.Decoder;
import NES.Common;
import NES.PPU.PPU;
import Utils.CPUState;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests taken from:
 * https://github.com/SingleStepTests/ProcessorTests/tree/main/6502
 */
public class TestProcessor {

    private final Logger logger = LoggerFactory.getLogger(TestProcessor.class);

//    @Test
//    public void test_ProcessorTests() throws IOException {
//        String dir = "6502_programs/ProcessorTests/6502/v1";
//        for (int i = 0; i < 255; i++) {
//            String filename = Common.byteToHex((byte) i, false) + ".json";
//            String path = String.valueOf(Path.of(dir, filename));
//            test_json(path);
//        }
//    }

    private static final String TEST_DIR_NAME = Path.of("ProcessorTests", "6502", "v1").toString();
    private static final String TEST_DIR = Path.of("src", "test", "resources", TEST_DIR_NAME).toString();

    // Initialize ram, cpu, bus only one time per test case
    private static Bus bus;
    private static CPU cpu;
    private static CPUBus cpuBus;
    private static byte[] ram = new byte[64 * 1024]; // The repository states the CPU has full-access to all 64kb of RAM

    @BeforeAll
    static void check_resources() {
        // Check directory exists
        Assertions.assertTrue(new File(TEST_DIR).exists());

        // Create the CPU and attach the bus with RAM, record memory accesses
        bus = new Bus();
        cpu = new CPU(bus, ram);
        cpuBus = new CPUBus(bus, true, true, ram);
        bus.attachCPUBus(cpuBus);
    }

    private static Stream<String> filter_jsons(boolean only_legal_implemented) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < 255; i++) {
            if (only_legal_implemented) {
                // Filter out illegal or unimplemented opcodes
                boolean exists = (Decoder.instructions_table[i] != null);
                if (!exists) {
                    continue;
                }
                // Exists, filter out illegals
                if (Decoder.instructions_table[i].is_illegal) {
                    continue;
                }
            }
            String filename = Common.byteToHex((byte) i, false) + ".json";
            String path = String.valueOf(Path.of(TEST_DIR, filename));
            result.add(path);
        }
        return result.stream();
    }

    static Stream<String> currentImplementedLegalOpcodesTestCases() {
        return filter_jsons(true);
    }

    static Stream<String> allOpcodesTestCases() {
        return filter_jsons(false);
    }

    private void test_json(String json_path) throws IOException {
//        logger.info("Testing: " + json_path);
        String json = Files.readString(Path.of(json_path));
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            JSONObject initial_json = jsonObject.getJSONObject("initial");
            String name = jsonObject.getString("name");
            JSONObject final_json = jsonObject.getJSONObject("final");
            JSONArray cycles_json = jsonObject.getJSONArray("cycles");

            logger.info("Running test: "+ i + " (" + name + ")");
            logger.debug(jsonObject.toString());
//            logger.debug("Initial json: " + initial_json);

            // Get initial state
            int p = initial_json.getInt("p");
            int a = initial_json.getInt("a");
            int pc = initial_json.getInt("pc");
            int s = initial_json.getInt("s");
            int x = initial_json.getInt("x");
            int y = initial_json.getInt("y");
            JSONArray initial_ram = initial_json.getJSONArray("ram");

//            {
//                CPUState cpuState = new CPUState(
//                        (short) pc, (byte) a, (byte) x, (byte) y, (byte) p, (byte) s);
//
//                logger.debug("Initial CPU state: " + cpuState);
//            }

            // Set initial RAM
            for (int j = 0; j < initial_ram.length(); j++) {
                JSONArray ram_j = initial_ram.getJSONArray(j);
                int addr = ram_j.getInt(0);
                int value = ram_j.getInt(1);
                ram[addr] = (byte) value;
            }

            // Set initial CPU state
            cpu.registers.PC = (short) (pc & 0xFFFF);
            cpu.registers.A = (byte) (a & 0xFF);
            cpu.registers.X = (byte) (x & 0xFF);
            cpu.registers.Y = (byte) (y & 0xFF);
            cpu.registers.P = (byte) (p & 0xFF);
            cpu.registers.S = (byte) (s & 0xFF);

            // Clock tick
            cpu.clock_tick();

            // Check the final state
            assertEquals(final_json.getInt("pc"), cpu.registers.PC & 0xFFFF);
            assertEquals(final_json.getInt("a"), cpu.registers.A & 0xFF);
            assertEquals(final_json.getInt("x"), cpu.registers.X & 0xFF);
            assertEquals(final_json.getInt("y"), cpu.registers.Y & 0xFF);
            assertEquals(final_json.getInt("p"), cpu.registers.P & 0xFF);
            assertEquals(final_json.getInt("s"), cpu.registers.S & 0xFF);

            // Check cycles, read write memory
            assertEquals(cycles_json.length(), cpuBus.recorded_memory.size());

            for (int j = 0; j < cycles_json.length(); j++) {
                MemoryAccessRecord memoryAccessRecord = cpuBus.recorded_memory.get(j);
                JSONArray cycle_json_array = cycles_json.getJSONArray(j);

                assertEquals(cycle_json_array.get(0), memoryAccessRecord.addr() & 0xFFFF);
                assertEquals(cycle_json_array.get(1), memoryAccessRecord.value() & 0xFF);
                boolean is_read = cycle_json_array.get(2).equals("read");
                assertEquals(is_read, memoryAccessRecord.is_read());
            }

            // Clear memory access records for next test case
            cpuBus.recorded_memory.clear();
        }
    }

    @ParameterizedTest
    @MethodSource("currentImplementedLegalOpcodesTestCases")
    void test_only_legal_implemented_opcodes(String json_path) throws IOException {
        test_json(json_path);
    }

    @ParameterizedTest
    @MethodSource("allOpcodesTestCases")
    void test_all_opcodes(String json_path) throws IOException {
        test_json(json_path);
    }

    @Test
    @Ignore
    // TODO: Delete this
    public void custom_test() throws IOException {
        String test_file_name = "69.json";
        String json_path = Path.of(TEST_DIR, test_file_name).toString();
        test_json(json_path);
    }
}
