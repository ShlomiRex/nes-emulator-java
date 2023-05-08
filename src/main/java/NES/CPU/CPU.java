package NES.CPU;

import NES.CPU.Registers.CPURegisters;
import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPU {

    private final Logger logger = LoggerFactory.getLogger(CPU.class);

    public final CPURegisters registers;
    public long cycles;
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

        // Fetch
        byte opcode = read_memory(registers.PC); // Read at address of Program Counter (duh!)

        // Decode
        Decoder.InstructionInfo instr_info = decoder.decode_opcode(opcode);
        Decoder.Instructions instr = instr_info.instr;
        Decoder.AddressingMode addrmode = instr_info.addrmode;
        int bytes = instr_info.bytes;
        int cycles = instr_info.cycles;
        Decoder.OopsCycle oops_cycle = instr_info.oopsCycle;
        logger.debug(instr.toString()+"\t"+addrmode+"\tBytes: "+bytes+"\tCycles: "+cycles+"\tOops cycle: "+oops_cycle);

        // Execute
        execute_instruction(instr, addrmode);

        // Increment PC
        if (instr != Decoder.Instructions.JMP && instr != Decoder.Instructions.JSR)
            registers.PC += bytes;

        this.cycles += cycles;

        //TODO: Check OOPS cycle.
    }

    private byte read_memory(short addr) {
        //TODO: Add mapping here. For now I only support mapper 0.

        // Note: 'addr' is short, which means in Java it can be negative. However we deal with unsigned numbers.
        // This is the best way to convert any signed number to unsigned, which allows accessing arrays.
        byte res = memory[addr & 0xFFFF];
        logger.debug("Reading memory: [" +
                Common.shortToHexString(addr, true) + "] = " +
                Common.byteToHexString(res, true));
        return res;
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
        byte fetched_memory;
        short addr;

        switch(instr) {
            case LDX:
            case LDY:
            case LDA:
                fetched_memory = fetch_instruction_memory(addrmode);
                if (instr == Decoder.Instructions.LDX)
                    registers.X = fetched_memory;
                else if (instr == Decoder.Instructions.LDY)
                    registers.Y = fetched_memory;
                else
                    registers.A = fetched_memory;
                registers.p_modify_n(fetched_memory);
                registers.p_modify_z(fetched_memory);
                break;
            case PHA:
                push_stack(registers.A);
                break;
            case NOP:
                // No operation
                break;
            case PLA:
                fetched_memory = fetch_instruction_memory(addrmode);
                registers.A = fetched_memory;

                registers.p_modify_n(fetched_memory);
                registers.p_modify_z(fetched_memory);
                break;
            case SEC:
                registers.P.setCarry(true);
                break;
            case CLC:
                registers.P.setCarry(false);
                break;
            case SEI:
                registers.P.setInterruptDisable(true);
                break;
            case CLI:
                registers.P.setInterruptDisable(false);
                break;
            case SED:
                registers.P.setDecimal(true);
                break;
            case CLD:
                registers.P.setDecimal(false);
                break;
            case CLV:
                registers.P.setOverflow(false);
                break;
            case ADC:
                // TODO: 08-May-23
                throw new RuntimeException("Not implemented yet");
                //break;
            case STX:
            case STY:
            case STA:
                addr = fetch_instruction_address(addrmode);
                if (instr == Decoder.Instructions.STX)
                    write_memory(addr, registers.X);
                else if (instr == Decoder.Instructions.STY)
                    write_memory(addr, registers.Y);
                else
                    write_memory(addr, registers.A);
                break;
            case INX:
                registers.X += 1;
                registers.p_modify_n(registers.X);
                registers.p_modify_z(registers.X);
                break;
            case INY:
                registers.Y += 1;
                registers.p_modify_n(registers.Y);
                registers.p_modify_z(registers.Y);
                break;
            case INC:
            case DEC:
                fetched_memory = fetch_instruction_memory(addrmode);
                if (instr == Decoder.Instructions.INC)
                    fetched_memory += 1;
                else
                    fetched_memory -= 1;
                addr = fetch_instruction_address(addrmode);
                write_memory(addr, fetched_memory);
                registers.p_modify_n(fetched_memory);
                registers.p_modify_z(fetched_memory);
                break;
            case JMP:
                addr = fetch_instruction_address(addrmode);
                registers.PC = addr;
                break;
            case JSR:
                // Jump to New Location Saving Return Address

                // push (PC+2),
                // (PC+1) -> PCL
                // (PC+2) -> PCH

                // What order of bytes to push?
                // After a lot of googling: https://stackoverflow.com/a/63886154
                // Basically push the PC like so: "...You need to push the high byte first, and then the low byte."

                // Push PC onto stack (return address)
                // NOTE: I push the 3rd byte of the instruction (PC + 2). Why not PC+3 (next instruction)?
                // Idk, but its important to emulate this exactly, because some games use this feature.

                push_pc((short) 2);

                break;
            default:
                throw new RuntimeException("Not implemented yet");
        }
    }

    /**
     * Fetch memory required by the instruction. All load instructions use this.
     * @param addrmode
     * @return
     */
    private byte fetch_instruction_memory(Decoder.AddressingMode addrmode) {
        switch (addrmode) {
            case IMPLIED -> throw new RuntimeException("Instruction with implied addressing mode should never ask to fetch memory.");
            case IMMEDIATE -> {
                byte res = read_memory((short) (registers.PC +1));
                logger.debug("Fetched immediate: "+Common.byteToHexString(res, true));
                return res;
            }
            case ACCUMULATOR -> {
                byte res = registers.A;
                logger.debug("Fetched accumulator: "+Common.byteToHexString(res, true));
                return res;
            }
            default -> throw new RuntimeException("Not implemented yet");
        }
    }

    /**
     * Extract the address from instruction. All store instructions use this.
     * @param addrmode
     * @return
     */
    private short fetch_instruction_address(Decoder.AddressingMode addrmode) {
        short pc_short = (short) (registers.PC + 1);
        switch(addrmode) {
            case IMMEDIATE -> {
                byte res = read_memory(pc_short);
                logger.debug("Fetched immediate address: "+Common.shortToHexString(res, true));
                return res;
            }
            case ABSOLUTE -> {
                return read_address_from_memory(pc_short);
            }
            case ZEROPAGE -> {
                return read_memory(pc_short);
            }
            case INDIRECT -> {
                short indirect_addr = read_address_from_memory(pc_short);
                return read_address_from_memory(indirect_addr);
            }
        }
        throw new RuntimeException("Not implemented yet");
    }

    private void write_memory(short addr, byte value) {
        logger.debug("Writing memory: ["+Common.shortToHexString(addr, true)+"] = "+value);
        memory[addr] = value;
    }

    private void push_stack(byte data) {
        write_memory((short)(0x100 + registers.S), data);
    }

    /// Push PC onto stack, adding offset to PC.
    private void push_pc(short offset) {
        byte pc_msb = (byte) ((registers.PC += offset) >> 8);
        byte pc_lsb = (byte) (registers.PC += offset);
        push_stack(pc_msb); // store high
        push_stack(pc_lsb); // store low
    }
}
