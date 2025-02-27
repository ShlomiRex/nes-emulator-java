package Assembler;

import CPU.TestCPU_Nestest;
import NES.Assembler.Assembler;
import NES.CPU.Decoder.Decoder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class AssemblerTest {
    private static final Logger logger = LoggerFactory.getLogger(AssemblerTest.class);

    @Test
    public void test_operand() {
        String[] asm = new String[]{"LDA #00"};
        byte[] output = Assembler.assemble(asm);
        byte[] expected = new byte[]{(byte) 0xA9, 0x00};
        assertArrayEquals(output, expected);
    }
}
