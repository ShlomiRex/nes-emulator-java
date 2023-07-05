package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.CPU;
import NES.CPU.Decoder.AssemblyInfo;
import NES.CPU.Decoder.Decoder;

import java.util.HashMap;

/**
 * Stores the assembly text and the corresponding program counter.
 * This is complex structure meant for fast search and retrieval of assembly text.
 */
public class AssemblyTextStructure {

    private final HashMap<Short, AssemblyLineRecord> pcToAssembly;
    public AssemblyTextStructure(CPU cpu, byte[] cpu_memory) {
        pcToAssembly = new HashMap<>();

        // Starting PC, we can start from 0 if we want to disassemble all the ROM
        short pc = (short) (cpu.registers.getPC() & 0xFFFF);

        for (int i = 0; i < 10; i++) {
            AssemblyLineRecord assemblyLineRecord = Decoder.decode_assembly_line2(pc, cpu_memory);
            pcToAssembly.put(pc, assemblyLineRecord);
            pc += assemblyLineRecord.bytes();
        }
    }

    public AssemblyLineRecord getAssemblyLineRecord(short pc) {
        return pcToAssembly.get(pc);
    }
}
