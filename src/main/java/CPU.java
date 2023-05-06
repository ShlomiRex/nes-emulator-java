import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPU {

    private final Logger logger = LoggerFactory.getLogger(CPU.class);

    private final CPURegisters registers;
    private long cycles;
    private final byte[] memory; // All addressable memory (64KB)

    private final Decoder decoder;

    public CPU(byte[] memory) {
        this.registers = new CPURegisters();
        this.memory = memory;
        this.decoder = new Decoder();

        res_interrupt();
    }

    public void clock_tick() {
        logger.debug("Tick, cycle: " + this.cycles);
        logger.debug(registers.toString());

        // Fetch instruction
        byte opcode = read_memory(registers.PC); // Read at address of Program Counter (duh!)

        // Decode
        Decoder.InstructionInfo instr_info = decoder.decode_opcode(opcode);
        Decoder.Instructions instr = instr_info.instr;
        Decoder.AddressingMode addrmode = instr_info.addrmode;
        int bytes = instr_info.bytes;
        int cycles = instr_info.cycles;
        Decoder.OopsCycle oops_cycle = instr_info.oopsCycle;
        logger.debug(instr.toString()+"\t"+addrmode+"\tBytes: "+bytes+"\tCycles: "+cycles+"\tOops cycle: "+oops_cycle);

        execute_instruction(instr, addrmode);
    }

    private byte read_memory(short addr) {
        //TODO: Add mapping here. For now I only support mapper 0.
        logger.debug("Reading memory at address: " + Common.shortToHexString(addr, true));

        // Note: 'addr' is short, which means in Java it can be negative. However we deal with unsigned numbers.
        // This is the best way to convert any signed number to unsigned, which allows accessing arrays.
        return memory[addr & 0xFFFF];
    }

    private void res_interrupt() {
        logger.debug("Reset interrupt called");

        registers.reset();

        short new_pc = read_address_from_memory((short) 0xFFFC);
        logger.debug("Jumping to interrupt address: " + Common.shortToHexString(new_pc, true));

        registers.PC = new_pc;
        cycles = 8;
    }

    private short read_address_from_memory(short addr) {
        byte lsb = read_memory(addr);
        byte msb = read_memory((short) (addr + 1));
        return (short) ((msb << 8) | lsb);
    }

    private void execute_instruction(Decoder.Instructions instr, Decoder.AddressingMode addrmode) {
        switch(instr) {
            case LDX:
            case LDY:
            case LDA:
                byte fetched_memory = fetch_memory(addrmode);
                if (instr == Decoder.Instructions.LDX)
                    registers.X = fetched_memory;
                else if (instr == Decoder.Instructions.LDY)
                    registers.Y = fetched_memory;
                else
                    registers.A = fetched_memory;
                registers.p_modify_n(fetched_memory);
                registers.p_modify_z(fetched_memory);
                break;
        }
    }

    private byte fetch_memory(Decoder.AddressingMode addrmode) {
        return 0; // TODO: Complete
    }
}
