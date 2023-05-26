package NES.CPU;

import NES.CPU.Registers.CPURegisters;
import NES.Common;
import NES.PPU.PPURegisters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPU {

    private final Logger logger = LoggerFactory.getLogger(CPU.class);

    public final CPURegisters registers;
    public long cycles;
    private final byte[] memory; // All addressable memory (64KB)

    private final Decoder decoder;

    private final PPURegisters ppuRegisters;

    public CPU(byte[] cpu_memory, PPURegisters ppuRegisters) {
        if (cpu_memory.length != 1024 * 64)
            throw new RuntimeException("Unexpected CPU memory address space size");

        this.memory = cpu_memory;
        this.ppuRegisters = ppuRegisters;

        this.registers = new CPURegisters();
        this.decoder = new Decoder();

        res_interrupt();
    }

    public void clock_tick() {
        logger.debug("Tick, cycle: " + this.cycles);
        logger.debug(registers.toString());

        // Fetch
        byte opcode = read_memory(registers.getPC()); // Read at address of Program Counter (duh!)

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
            registers.setPC((short) (registers.getPC() + bytes));

        this.cycles += cycles;

        //TODO: Check OOPS cycle.

        // Check NMI interrupt
        if (ppuRegisters.isNmiEnabled()) {
            logger.debug("NMI interrupt called");
            ppuRegisters.setNmiEnabled(false); // After reading the NMI flag ($2002) , it is cleared.
            nmi_interrupt();
        }
    }

    private byte read_memory(short addr) {
        //TODO: Add mapping here. For now I only support mapper 0.

        byte res;

        // Map certain addresses to PPU if needed
        switch (addr) {
            case 0x2002 -> {
                res = ppuRegisters.readStatus();
            }
            case 0x2000 -> {
                res = ppuRegisters.getCtrl();
            }
            default -> {
                // Note: 'addr' is short, which means in Java it can be negative. However we deal with unsigned numbers.
                // This is the best way to convert any signed number to unsigned, which allows accessing arrays.
                res = memory[addr & 0xFFFF];
            }
        }
        logger.debug("Read memory: [" +
                Common.shortToHexString(addr, true) + "] = " +
                Common.byteToHexString(res, true));
        return res;
    }

    private void res_interrupt() {
        logger.debug("Reset interrupt called");

        registers.reset();

        short new_pc = read_address_from_memory((short) 0xFFFC);
        logger.debug("Jumping to interrupt address: " + Common.shortToHexString(new_pc, true));

        registers.setPC(new_pc);
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
        byte result;

        switch(instr) {
            case LDX:
            case LDY:
            case LDA:
                fetched_memory = fetch_instruction_memory(addrmode);
                if (instr == Decoder.Instructions.LDX)
                    registers.setX(fetched_memory);
                else if (instr == Decoder.Instructions.LDY)
                    registers.setY(fetched_memory);
                else
                    registers.setA(fetched_memory);
                registers.getP().modify_n(fetched_memory);
                registers.getP().modify_z(fetched_memory);
                break;
            case PHA:
                push_stack(registers.getA());
                break;
            case NOP:
                // No operation
                break;
            case PLA:
                fetched_memory = fetch_instruction_memory(addrmode);
                registers.setA(fetched_memory);

                registers.getP().modify_n(fetched_memory);
                registers.getP().modify_z(fetched_memory);
                break;
            case SEC:
                registers.getP().setCarry(true);
                break;
            case CLC:
                registers.getP().setCarry(false);
                break;
            case SEI:
                registers.getP().setInterruptDisable(true);
                break;
            case CLI:
                registers.getP().setInterruptDisable(false);
                break;
            case SED:
                registers.getP().setDecimal(true);
                break;
            case CLD:
                registers.getP().setDecimal(false);
                break;
            case CLV:
                registers.getP().setOverflow(false);
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
                    write_memory(addr, registers.getX());
                else if (instr == Decoder.Instructions.STY)
                    write_memory(addr, registers.getY());
                else
                    write_memory(addr, registers.getA());
                break;
            case INX:
                registers.setA((byte) (registers.getA() + 1));
                registers.getP().modify_n(registers.getX());
                registers.getP().modify_z(registers.getX());
                break;
            case INY:
                registers.setY((byte) (registers.getY() + 1));
                registers.getP().modify_n(registers.getY());
                registers.getP().modify_z(registers.getY());
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
                registers.getP().modify_n(fetched_memory);
                registers.getP().modify_z(fetched_memory);
                break;
            case JMP:
                addr = fetch_instruction_address(addrmode);
                registers.setPC(addr);
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
                addr = fetch_instruction_address(addrmode);
                registers.setPC(addr);
                break;
            case CMP:
                exec_cmp(addrmode, registers.getA());
                break;
            case CPX:
                exec_cmp(addrmode, registers.getX());
                break;
            case CPY:
                exec_cmp(addrmode, registers.getY());
                break;
            case TAX:
                registers.setX(registers.getA());
                registers.getP().modify_n(registers.getX());
                registers.getP().modify_z(registers.getX());
                break;
            case TAY:
                registers.setY(registers.getA());
                registers.getP().modify_n(registers.getY());
                registers.getP().modify_z(registers.getY());
                break;
            case TSX:
                registers.setX(registers.getS());
                registers.getP().modify_n(registers.getX());
                registers.getP().modify_z(registers.getX());
                break;
            case TXA:
                registers.setA(registers.getX());
                registers.getP().modify_n(registers.getA());
                registers.getP().modify_z(registers.getA());
                break;
            case TXS:
                registers.setS(registers.getX());
                // We don't modify N or Z bits
                break;
            case TYA:
                registers.setA(registers.getY());
                registers.getP().modify_n(registers.getA());
                registers.getP().modify_z(registers.getA());
                break;
            case AND:
                fetched_memory = fetch_instruction_memory(addrmode);

                registers.setA((byte) (registers.getA() & fetched_memory));
                registers.getP().modify_n(registers.getA());
                registers.getP().modify_z(registers.getA());
                break;
            case ASL:
            case LSR:
                fetched_memory = fetch_instruction_memory(addrmode);
                result = fetched_memory;
                if (instr == Decoder.Instructions.ASL) {
                    result <<= 1;
                } else {
                    result >>= 1;
                }
                // Determine if shift overflowed (if yes, then set carry)
                // If last bit is 1, and we left shift, then that bit is the carry.
                boolean new_carry = Common.Bits.getBit(fetched_memory, 7);

                // Now we need to know where to put the result. Register or memory?
                if (addrmode == Decoder.AddressingMode.ACCUMULATOR) {
                    registers.setA(result);
                } else {
                    addr = fetch_instruction_address(addrmode);
                    write_memory(addr, result);
                }

                registers.getP().modify_n(result);
                registers.getP().modify_z(result);
                registers.getP().setCarry(new_carry);
                break;
            case BIT:
                // Test Bits in Memory with Accumulator
                fetched_memory = fetch_instruction_memory(addrmode);
                result = (byte) (registers.getA() & fetched_memory);
                boolean bit7 = Common.Bits.getBit(fetched_memory, 7);
                boolean bit6 = Common.Bits.getBit(fetched_memory, 6);

                registers.getP().setNegative(bit7);
                registers.getP().setOverflow(bit6);
                registers.getP().modify_z(result);
                break;
            case BMI:
            case BPL:
            case BNE:
            case BVC:
            case BVS:
            case BEQ:
            case BCS:
            case BCC:
                if (
                        (instr == Decoder.Instructions.BMI && registers.getP().getNegative()     == true)    ||
                        (instr == Decoder.Instructions.BPL && registers.getP().getNegative()     == false)   ||
                        (instr == Decoder.Instructions.BNE && registers.getP().getZero()         == false)   ||
                        (instr == Decoder.Instructions.BVC && registers.getP().getOverflow()     == false)   ||
                        (instr == Decoder.Instructions.BVS && registers.getP().getOverflow()     == true)    ||
                        (instr == Decoder.Instructions.BEQ && registers.getP().getZero()         == true)    ||
                        (instr == Decoder.Instructions.BCS && registers.getP().getCarry()        == true)    ||
                        (instr == Decoder.Instructions.BCC && registers.getP().getCarry()        == false)) {
                    byte offset = read_memory((short) (registers.getPC() + 1));
                    registers.setPC((short) (registers.getPC() + offset));
                }
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
                byte res = read_memory((short) (registers.getPC() +1));
                logger.debug("Fetched immediate: "+Common.byteToHexString(res, true));
                return res;
            }
            case ACCUMULATOR -> {
                byte res = registers.getA();
                logger.debug("Fetched accumulator: "+Common.byteToHexString(res, true));
                return res;
            }
            case ABSOLUTE -> {
                short addr = read_address_from_memory((short) (registers.getPC() + 1));
                byte res = read_memory(addr);
                logger.debug("Fetched absolute: "+Common.byteToHexString(res, true));
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
        short pc_short = (short) (registers.getPC() + 1);
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
        write_memory((short)(0x100 + registers.getS()), data);
    }

    /**
     * Push PC onto stack, adding offset to PC.
     * @param offset
     */
    private void push_pc(short offset) {
        byte pc_msb = (byte) ((registers.getPC() + offset) >> 8);
        byte pc_lsb = (byte) (registers.getPC() + offset);
        push_stack(pc_msb); // store high
        push_stack(pc_lsb); // store low
        registers.setPC((short) (registers.getPC() + offset + offset));
    }

    /**
     * Execute cmp instruction
     */
    private void exec_cmp(Decoder.AddressingMode addrmode, byte register) {
		/*
		Link: http://www.6502.org/tutorials/compare_instructions.html
		Compare Results | N | Z | C
		---------------------------
		A, X, or Y < M  | * | 0 | 0
		A, X, or Y = M  | 0 | 1 | 1
		A, X, or Y > M  | * | 0 | 1

		*The N flag will be bit 7 of A, X, or Y - Memory
		*/
        byte fetched_memory = fetch_instruction_memory(addrmode);
        byte sub = (byte) (register - fetched_memory);
        boolean last_bit = Common.Bits.getBit(sub, 7);

        boolean new_n = false, new_z = false, new_c = false;

        if (register < fetched_memory) {
            new_n = last_bit;
        } else if (register == fetched_memory) {
            new_z = new_c = true;
        } else {
            new_n = last_bit;
            new_c = true;
        }

        registers.getP().setNegative(new_n);
        registers.getP().setZero(new_z);
        registers.getP().setCarry(new_c);
    }

    private void nmi_interrupt() {
        // TODO: Complete
    }
}
