import NES.CPU.CPU;
import NES.CPU.Registers.StatusFlags;
import NES.Common;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Using the following git repository: <a href="https://github.com/TomHarte/ProcessorTests">...</a>
 * We test on each opcode that the output of the CPU matches the output of the JSON test.
 * The git repository must be cloned into the 'test_resources/' folder which is in the root folder of the project.
 */
@DisplayNameGeneration(TestCPU.ReplaceCamelCase.class)
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

    private static Stream<Arguments> testCases() {
        return Stream.of(
//                Arguments.of((byte) 0x8D),
//                Arguments.of((byte) 0x84),
//                Arguments.of((byte) 0xA9),
//                Arguments.of((byte) 0xA5),
//                Arguments.of((byte) 0xAD),
                Arguments.of((byte) 0xB5)
        );
    }


    @ParameterizedTest(name = "Test Opcode: {0}")
    @MethodSource("testCases")
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
        String opcode_hex = Common.byteToHexString((byte) opcode, false);
        Path path = Paths.get("test_resources/ProcessorTests/nes6502", "v1", opcode_hex + ".json");
        logger.debug("Reading test file: " + path);
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

            logger.debug("Setting memory: [" + address +
                    " ("+Common.shortToHexString(address.shortValue(), true)+")] = " +
                    value + " ("+Common.byteToHexString(value.byteValue(), true)+")");

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

    static class ReplaceCamelCase extends DisplayNameGenerator.Standard {
        @Override
        public String generateDisplayNameForClass(Class<?> testClass) {
            return replaceCamelCase(super.generateDisplayNameForClass(testClass));
        }

        @Override
        public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
            return replaceCamelCase(super.generateDisplayNameForNestedClass(nestedClass));
        }

        @Override
        public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
            return this.replaceCamelCase(testMethod.getName()) +
                    DisplayNameGenerator.parameterTypesAsString(testMethod);
        }

        String replaceCamelCase(String camelCase) {
            StringBuilder result = new StringBuilder();
            result.append(camelCase.charAt(0));
            for (int i=1; i<camelCase.length(); i++) {
                if (Character.isUpperCase(camelCase.charAt(i))) {
                    result.append(' ');
                    result.append(Character.toLowerCase(camelCase.charAt(i)));
                } else {
                    result.append(camelCase.charAt(i));
                }
            }
            return result.toString();
        }
    }
}
