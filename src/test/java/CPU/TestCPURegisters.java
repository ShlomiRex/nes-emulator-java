package CPU;

import NES.CPU.Registers.CPURegisters;
import NES.Common;
import org.junit.Test;

import static NES.CPU.Registers.Flags.*;
import static org.junit.Assert.*;

public class TestCPURegisters {
    @Test
    public void test_p_modify_z() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.modify_z((byte) 0);
        assertEquals(cpuRegisters.getFlag(ZERO), true);

        cpuRegisters.modify_z((byte) 0xFF);
        assertEquals(cpuRegisters.getFlag(ZERO), false);

        cpuRegisters.modify_z((byte) 0);
        assertEquals(cpuRegisters.getFlag(ZERO), true);
    }

    @Test
    public void test_p_modify_n() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.modify_n((byte) 0);
        assertEquals(cpuRegisters.getFlag(NEGATIVE), false);

        cpuRegisters.modify_n((byte) 0xAA);
        assertEquals(cpuRegisters.getFlag(NEGATIVE), true);

        cpuRegisters.modify_n((byte) 0x56);
        assertEquals(cpuRegisters.getFlag(NEGATIVE), false);

        cpuRegisters.modify_n((byte) 0xFF);
        assertEquals(cpuRegisters.getFlag(NEGATIVE), true);

        cpuRegisters.modify_n((byte) 0x7F);
        assertEquals(cpuRegisters.getFlag(NEGATIVE), false);

        cpuRegisters.modify_n((byte) 0x80);
        assertEquals(cpuRegisters.getFlag(NEGATIVE), true);
    }

    @Test
    public void test_p_modify_c() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.modify_c((byte) 0, (byte) 0, (byte) 0);
        assertEquals(cpuRegisters.getFlag(CARRY), false);

        cpuRegisters.modify_c((byte) 0xF0, (byte) 0xF0, (byte) 0);
        assertEquals(cpuRegisters.getFlag(CARRY), true);

        cpuRegisters.modify_c((byte) 0xFF, (byte) 0, (byte) 0);
        assertEquals(cpuRegisters.getFlag(CARRY), false);

        cpuRegisters.modify_c((byte) 0xFF, (byte) 0, (byte) 1);
        assertEquals(cpuRegisters.getFlag(CARRY), true);

        cpuRegisters.modify_c((byte) 0, (byte) 0xFF, (byte) 1);
        assertEquals(cpuRegisters.getFlag(CARRY), true);
    }
}
