import org.junit.Test;
import static org.junit.Assert.*;

public class TestCPURegisters {
    @Test
    public void test_p_modify_z() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.p_modify_z((byte) 0);
        assertEquals(Common.Bits.getBit(cpuRegisters.P, 1), true);

        cpuRegisters.p_modify_z((byte) 0xFF);
        assertEquals(Common.Bits.getBit(cpuRegisters.P, 1), false);

        cpuRegisters.p_modify_z((byte) 0);
        assertEquals(Common.Bits.getBit(cpuRegisters.P, 1), true);
    }

    @Test
    public void test_p_modify_n() {
        CPURegisters cpuRegisters = new CPURegisters();

        cpuRegisters.p_modify_n((byte) 0);
        assertEquals(Common.Bits.getBit(cpuRegisters.P, 7), false);

        cpuRegisters.p_modify_n((byte) 0xAA);
        assertEquals(Common.Bits.getBit(cpuRegisters.P, 7), true);

        cpuRegisters.p_modify_n((byte) 0x56);
        assertEquals(Common.Bits.getBit(cpuRegisters.P, 7), false);

        cpuRegisters.p_modify_n((byte) 0xFF);
        assertEquals(Common.Bits.getBit(cpuRegisters.P, 7), true);

        cpuRegisters.p_modify_n((byte) 0x7F);
        assertEquals(Common.Bits.getBit(cpuRegisters.P, 7), false);

        cpuRegisters.p_modify_n((byte) 0x80);
        assertEquals(Common.Bits.getBit(cpuRegisters.P, 7), true);
    }
}
