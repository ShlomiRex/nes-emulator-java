package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.CPU;
import NES.CPU.Decoder.AssemblyInfo;
import NES.CPU.Decoder.Decoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the assembly text and the corresponding program counter.
 * This is complex structure meant for fast search and retrieval of assembly text.
 */
public class AssemblyTextStructure {
    private final HashMap<Short, AssemblyLineTextStructure> pcToAssembly;
    public AssemblyTextStructure() {
        pcToAssembly = new HashMap<>();
    }

    /**
     *
     * @param asm_line_num The line number in the assembly text pane
     * @param addr The address location of the instruction
     * @param document_offset The offset in the document that points to the start of the assembly line
     */
    public void add_assembly_line(int asm_line_num, short addr, int document_offset, int line_length) {
        pcToAssembly.put(addr, new AssemblyLineTextStructure(asm_line_num, document_offset, line_length));
    }

    public AssemblyLineTextStructure get_assembly_line(short pc) {
        return pcToAssembly.get(pc);
    }

    public record AssemblyLineTextStructure(int asm_line_num, int document_offset, int line_length) {
    }
}
