package NES.UI.Debugger.AssemblyDebugger;

import java.util.HashMap;

/**
 * Stores the assembly text and the corresponding program counter.
 * This is complex structure meant for fast search and retrieval of assembly text.
 */
public class AssemblyTextStructure {

    private final HashMap<Short, Object> pcToAssembly;
    public AssemblyTextStructure() {
        pcToAssembly = new HashMap<>();
    }
}
