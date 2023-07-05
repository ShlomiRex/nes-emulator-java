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

    private final HashMap<Short, Object> pcToAssembly;
    public AssemblyTextStructure(CPU cpu, byte[] cpu_memory) {
        pcToAssembly = new HashMap<>();

        // Starting PC
        short pc = (short) (cpu.registers.getPC() & 0xFFFF);

        AssemblyLineRecord assemblyLineRecord = Decoder.decode_assembly_line2(pc, cpu_memory);
        pcToAssembly.put(pc, assemblyLineRecord);
    }
}
