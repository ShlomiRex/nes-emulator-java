package CPU;

import NES.CPU.Registers.CPURegisters;
import NES.Common;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCPURegisters {
    @Test
    public void test_p_modify_z() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.modify_z((byte) 0);
        assertEquals(cpuRegisters.getZero(), true);

        cpuRegisters.modify_z((byte) 0xFF);
        assertEquals(cpuRegisters.getZero(), false);

        cpuRegisters.modify_z((byte) 0);
        assertEquals(cpuRegisters.getZero(), true);
    }

    @Test
    public void test_p_modify_n() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.modify_n((byte) 0);
        assertEquals(cpuRegisters.getNegative(), false);

        cpuRegisters.modify_n((byte) 0xAA);
        assertEquals(cpuRegisters.getNegative(), true);

        cpuRegisters.modify_n((byte) 0x56);
        assertEquals(cpuRegisters.getNegative(), false);

        cpuRegisters.modify_n((byte) 0xFF);
        assertEquals(cpuRegisters.getNegative(), true);

        cpuRegisters.modify_n((byte) 0x7F);
        assertEquals(cpuRegisters.getNegative(), false);

        cpuRegisters.modify_n((byte) 0x80);
        assertEquals(cpuRegisters.getNegative(), true);
    }
}
