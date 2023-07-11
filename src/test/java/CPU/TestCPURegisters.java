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
        assertEquals(cpuregisters.getFlag(ZERO), true);

        cpuRegisters.modify_z((byte) 0xFF);
        assertEquals(cpuregisters.getFlag(ZERO), false);

        cpuRegisters.modify_z((byte) 0);
        assertEquals(cpuregisters.getFlag(ZERO), true);
    }

    @Test
    public void test_p_modify_n() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.modify_n((byte) 0);
        assertEquals(cpuregisters.getFlag(NEGATIVE), false);

        cpuRegisters.modify_n((byte) 0xAA);
        assertEquals(cpuregisters.getFlag(NEGATIVE), true);

        cpuRegisters.modify_n((byte) 0x56);
        assertEquals(cpuregisters.getFlag(NEGATIVE), false);

        cpuRegisters.modify_n((byte) 0xFF);
        assertEquals(cpuregisters.getFlag(NEGATIVE), true);

        cpuRegisters.modify_n((byte) 0x7F);
        assertEquals(cpuregisters.getFlag(NEGATIVE), false);

        cpuRegisters.modify_n((byte) 0x80);
        assertEquals(cpuregisters.getFlag(NEGATIVE), true);
    }
}
