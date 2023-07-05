package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.CPU;
import NES.CPU.Decoder.AssemblyInfo;
import NES.CPU.Decoder.Decoder;
import NES.Common;

import javax.swing.*;
import javax.swing.text.StyledDocument;

public class AssemblyStyledDocument {

    private final StyledDocument styledDocument;
    private final CPU cpu;
    public AssemblyStyledDocument(JTextPane assembly_text_pane, CPU cpu, byte[] cpu_memory) {
        this.styledDocument = assembly_text_pane.getStyledDocument();
        this.cpu = cpu;

        // Initialized assembly text structure
        AssemblyTextStructure assemblyTextStructure = new AssemblyTextStructure(cpu, cpu_memory);

        // Starting PC
//        short pc = (short) (cpu.registers.getPC() & 0xFFFF);

        // Initialized the styled document
//        for (int assembly_line_num = 0; assembly_line_num < 350; assembly_line_num++) {
//            AssemblyInfo info = Decoder.decode_assembly_line(cpu_memory, pc);
//        }

    }
}
