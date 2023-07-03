package CPU;

import NES.CPU.Registers.CPURegisters;
import NES.Common;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCPURegisters {
    @Test
    public void test_p_modify_z() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.getP().modify_z((byte) 0);
        assertEquals(cpuRegisters.getP().getZero(), true);

        cpuRegisters.getP().modify_z((byte) 0xFF);
        assertEquals(cpuRegisters.getP().getZero(), false);

        cpuRegisters.getP().modify_z((byte) 0);
        assertEquals(cpuRegisters.getP().getZero(), true);
    }

    @Test
    public void test_p_modify_n() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.getP().modify_n((byte) 0);
        assertEquals(cpuRegisters.getP().getNegative(), false);

        cpuRegisters.getP().modify_n((byte) 0xAA);
        assertEquals(cpuRegisters.getP().getNegative(), true);

        cpuRegisters.getP().modify_n((byte) 0x56);
        assertEquals(cpuRegisters.getP().getNegative(), false);

        cpuRegisters.getP().modify_n((byte) 0xFF);
        assertEquals(cpuRegisters.getP().getNegative(), true);

        cpuRegisters.getP().modify_n((byte) 0x7F);
        assertEquals(cpuRegisters.getP().getNegative(), false);

        cpuRegisters.getP().modify_n((byte) 0x80);
        assertEquals(cpuRegisters.getP().getNegative(), true);
    }
}
