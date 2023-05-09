import NES.CPU.Registers.CPURegisters;
import NES.Common;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCPURegisters {
    @Test
    public void test_p_modify_z() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.P.modify_z((byte) 0);
        assertEquals(cpuRegisters.P.getZero(), true);

        cpuRegisters.P.modify_z((byte) 0xFF);
        assertEquals(cpuRegisters.P.getZero(), false);

        cpuRegisters.P.modify_z((byte) 0);
        assertEquals(cpuRegisters.P.getZero(), true);
    }

    @Test
    public void test_p_modify_n() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.P.modify_n((byte) 0);
        assertEquals(cpuRegisters.P.getNegative(), false);

        cpuRegisters.P.modify_n((byte) 0xAA);
        assertEquals(cpuRegisters.P.getNegative(), true);

        cpuRegisters.P.modify_n((byte) 0x56);
        assertEquals(cpuRegisters.P.getNegative(), false);

        cpuRegisters.P.modify_n((byte) 0xFF);
        assertEquals(cpuRegisters.P.getNegative(), true);

        cpuRegisters.P.modify_n((byte) 0x7F);
        assertEquals(cpuRegisters.P.getNegative(), false);

        cpuRegisters.P.modify_n((byte) 0x80);
        assertEquals(cpuRegisters.P.getNegative(), true);
    }
}
