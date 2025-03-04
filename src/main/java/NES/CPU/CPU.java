package NES.CPU;

import NES.Bus.Bus;
import NES.CPU.Decoder.Decoder;
import NES.CPU.Decoder.InstructionInfo;
import NES.CPU.Registers.CPURegisters;
import NES.CPU.Registers.Flags;
import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static NES.CPU.Registers.Flags.*;

public class CPU {

    private final Logger logger = LoggerFactory.getLogger(CPU.class);

    public final CPURegisters registers;
    public long cycles;
    public long instructions = 0; // counter number of instructions executed
    private byte fetched_data; // Set in addressing modes, used afterwards.
    private short fetched_addr; // Set in addressing modes, used afterwards.
    public final Bus bus;

    /**
     *
     * @param bus
     * @param cpu_memory
     */
    public CPU(Bus bus, byte[] cpu_memory) {
        if (cpu_memory.length != 1024 * 64)
            throw new RuntimeException("Unexpected CPU memory address space size");

        this.bus = bus;

        this.registers = new CPURegisters();
    }

    public void clock_tick() {
//        logger.debug("CPU Tick, cycle: " + this.cycles);
//        logger.debug(registers.toString());

        // We can't ignore the NMI interrupt which is called when PPU VBlank starts.
        if (bus.nmi_line) {
            bus.nmi_line = false;
            nmi_interrupt();
            return;
        }

        // Fetch
        byte opcode = read_memory(registers.PC); // Read at address of Program Counter (duh!)
        registers.PC ++;

        // Decode
        InstructionInfo instr_info = Decoder.decode_opcode(opcode);
        if (instr_info == null)
            throw new RuntimeException("Unknown opcode: " + Common.byteToHex(opcode, true));

        Instructions instr = instr_info.instr;
        AddressingMode addrmode = instr_info.addrmode;

        // Debug info
//        {
//            int bytes = instr_info.bytes;
//            int cycles = instr_info.cycles;
//            Decoder.OopsCycle oops_cycle = instr_info.oopsCycle;
//            logger.debug(
//                    instr.toString() + "(" + Common.byteToHex(opcode, true) + ")\t"
//                            + addrmode + "\tBytes: "
//                            + bytes + "\tCycles: "
//                            + cycles + "\tOops cycle: "
//                            + oops_cycle);
//        }
        logger.debug("CPU Tick, PC: {}, OP: {} ({}), AddrMode: {}",
                Common.shortToHex(registers.PC, true), instr.toString(),
                Common.byteToHex(opcode, true), addrmode);

        // Execute
        execute_instruction(instr, addrmode);

        //TODO: Check OOPS cycle.

        instructions ++;

//        logger.debug(registers.toString());
//        logger.debug("End of tick");
    }

    private byte read_memory(short addr) {
        cycles ++;
        return bus.cpuBus.cpu_read(addr);
    }

    private void write_memory(short addr, byte value) {
        cycles ++;
        bus.cpuBus.cpu_write(addr, value);
    }

    public void reset() {
        logger.debug("Reset interrupt called");

        registers.reset();

        //logger.debug("Jumping to interrupt address: " + Common.shortToHex(new_pc, true));

        registers.PC = read_address_from_memory((short) 0xFFFC);
        cycles = 7; // TODO: Is it 7 or 8 cycles?
    }

    private short read_address_from_memory(short addr) {
        byte lsb = read_memory(addr);
        byte msb = read_memory((short) (addr + 1));
        return Common.makeShort(lsb, msb);
    }

    private void execute_instruction(Instructions instr, AddressingMode addrmode) {
        // This help me to make the CPU cycle accurate:
        // http://www.atarihq.com/danb/files/64doc.txt

        boolean is_instructions_accessing_the_stack = true;
        // Instructions accessing the stack.
        switch(instr) {
            case BRK:
                // read next instruction byte (and throw it away), increment PC
                read_memory(registers.PC);
                registers.PC ++;

                // push PCH on stack, decrement S
                push_stack((byte) (registers.PC >> 8));

                // push PCL on stack, decrement S
                push_stack((byte) (registers.PC & 0xFF));

                // push P on stack (with B flag set), decrement S
                push_stack((byte) (registers.P | 0b00010000));

                // fetch PCL
                byte pcl = read_memory((short) 0xFFFE);

                // fetch PCH
                byte pch = read_memory((short) 0xFFFF);

                registers.PC = Common.makeShort(pcl, pch);

                // Set interrupt disable flag (bit 2 of status register)
                registers.setFlag(INTERRUPT, true);

                break;
            case RTI:
                // read next instruction byte (and throw it away)
                read_memory(registers.PC); // dummy read

                read_memory(Common.makeShort(registers.S, (byte) 0x01)); // dummy read

                // pull P from stack, increment S
                byte p = pop_stack();

                // pull PCL from stack, increment S
                byte pcl2 = pop_stack();

                // pull PCH from stack
                byte pch2 = pop_stack();

                registers.PC = Common.makeShort(pcl2, pch2);

                p &= 0b11101111; // Clear bits 4
                p |= 0b00100000; // Set bits 5
                registers.P = p;

                break;
            case RTS:
                // read next instruction byte (and throw it away)
                read_memory(registers.PC);

                // increment S
                read_memory(Common.makeShort(registers.S, (byte) 0x01)); // dummy read

                // pull PCL from stack, increment S
                pcl = pop_stack();

                // pull PCH from stack
                pch = pop_stack();

                // increment PC
                registers.PC = Common.makeShort(pcl, pch);
                read_memory(registers.PC); // dummy read
                registers.PC ++;
                break;
            case PHA:
            case PHP:
                // read next instruction byte (and throw it away)
                read_memory(registers.PC);
                //registers.PC ++;

                if (instr == Instructions.PHP) {
                    byte new_p = registers.P;
                    new_p |= 0b00110000; // Set B and U flags
                    push_stack(new_p);

                } else {
                    push_stack(registers.A);
                }

                break;
            case PLA:
            case PLP:
                // read next instruction byte (and throw it away)
                read_memory(registers.PC);
                //registers.PC ++;

                // increment S
                read_memory(Common.makeShort(registers.S, (byte) 0x01));
                //registers.setS((byte) (registers.S + 1));

                // pull register from stack
                byte reg = pop_stack();

                if (instr == Instructions.PLA) {
                    registers.A = reg;
                    registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.A, 7));
                    registers.setFlag(ZERO, registers.A == 0);
                }
                else {
                    registers.P = reg;
                    registers.setFlag(BREAK, false);
                    registers.setFlag(UNUSED, true);
                }

                break;
            case JSR:
                // fetch low address byte, increment PC
                byte addr_low = read_memory(registers.PC);
                registers.PC ++;

                // internal operation (predecrement S?)
                read_memory(Common.makeShort(registers.S, (byte) 0x01));

                // push PCH on stack, decrement S
                push_stack((byte) ((registers.PC) >> 8));

                // push PCL on stack, decrement S
                push_stack((byte) ((registers.PC) & 0xFF));

                // copy low address byte to PCL, fetch high address byte to PCH
                byte pc_high = read_memory(registers.PC);
                registers.PC = Common.makeShort(addr_low, pc_high);
                break;
            default:
                is_instructions_accessing_the_stack = false;
                break;
        }

        if (is_instructions_accessing_the_stack)
            return;

        // Fetch / Set in any addressing mode
        switch (addrmode) {
            // Accumulator or implied addressing
            case ACCUMULATOR:
            case IMPLIED:
                accumulator_or_implied_addressing(instr);
                break;
            case IMMEDIATE:
                immediate_addressing();
                break;
            case ABSOLUTE:
                absolute_addressing(instr);
                break;
            // Zero page addressing
            case ZEROPAGE:
                zeropage_addressing(instr);
                break;
            // Zero page indexed addressing
            case ZEROPAGE_X:
            case ZEROPAGE_Y:
                zeropage_indexed_addressing(instr, addrmode);
                break;
            // Absolute indexed addressing
            case ABSOLUTE_X:
            case ABSOLUTE_Y:
                absolute_indexed_addressing(instr, addrmode);
                break;
            // Relative addressing (BCC, BCS, BNE, BEQ, BPL, BMI, BVC, BVS)
            case RELATIVE:
                relative_addressing(instr);
                break;
            // Indexed indirect addressing
            case INDIRECT_X:
                indexed_indirect_addressing(instr);
                break;
            // Indirect indexed addressing
            case INDIRECT_Y:
                indirect_indexed_addressing(instr, addrmode);
                break;
            case ABSOLUTE_INDIRECT:
                indirect_addressing();
                break;
            default:
                throw new RuntimeException("Addressing mode not implemented: " + addrmode);
        }

        // Execute
        switch (instr) {
            case LDA:
                exec_lda();
                break;
            case ADC:
                exec_adc();
                break;
            case CLI:
                registers.setFlag(INTERRUPT, false);
                break;
            case STA:
                // TODO: Problem writing to 0x2006 on STA when A = 0xF0
                write_memory(fetched_addr, registers.A);
                break;
            case TAX:
                exec_tax();
                break;
            case CPX:
                exec_cmp(registers.X);
                break;
            case CLV:
                registers.setFlag(OVERFLOW, false);
                break;
            case TAY:
                exec_tay();
                break;
            case CPY:
                exec_cmp(registers.Y);
                break;
            case STX:
                write_memory(fetched_addr, registers.X);
                break;
            case STY:
                write_memory(fetched_addr, registers.Y);
                break;
            case BCC:
            case NOP:
            case BCS:
            case BPL:
            case JMP:
            case BEQ:
            case BNE:
            case BVS:
            case BVC:
            case BMI:
                // do nothing, sometimes the addressing mode already done what we needed.
                break;
            case LSR:
                exec_lsr(addrmode == AddressingMode.ACCUMULATOR);
                break;
            case CMP:
                exec_cmp(registers.A);
                break;
            case ROL:
                exec_rol_or_ror(addrmode == AddressingMode.ACCUMULATOR, true);
                break;
            case SEC:
                registers.setFlag(CARRY, true);
                break;
            case SED:
                registers.setFlag(DECIMAL, true);
                break;
            case ROR:
                exec_rol_or_ror(addrmode == AddressingMode.ACCUMULATOR, false);
                break;
            case SEI:
                registers.setFlag(INTERRUPT, true);
                break;
            case AND:
                exec_and();
                break;
            case TSX:
                exec_tsx();
                break;
            case TXA:
                exec_txa();
                break;
            case ORA:
                exec_ora();
                break;
            case INC:
                exec_inc_or_dec(true);
                break;
            case LDX:
                exec_ldx();
                break;
            case CLC:
                registers.setFlag(CARRY, false);
                break;
            case BIT:
                exec_bit();
                break;
            case CLD:
                registers.setFlag(DECIMAL, false);
                break;
            case EOR:
                exec_eor();
                break;
            case LDY:
                exec_ldy();
                break;
            case SBC:
                exec_sbc();
                break;
            case INY:
                exec_iny();
                break;
            case INX:
                exec_inx();
                break;
            case DEY:
                exec_dey();
                break;
            case DEX:
                exec_dex();
                break;
            case TYA:
                exec_tya();
                break;
            case TXS:
                registers.S = registers.X;
                break;
            case ASL:
                exec_asl(addrmode == AddressingMode.ACCUMULATOR);
                break;
            case DEC:
                exec_inc_or_dec(false);
                break;
            case LAX:
                exec_lax();
                break;
            case SAX:
                exec_sax();
                break;
            case DCP:
                exec_dcp();
                break;
            case ISB:
                exec_isb();
                break;
            case JAM:
                exec_jam();
                break;
            case RRA:
                exec_rra();
                break;
            case SLO:
                exec_slo(addrmode == AddressingMode.ACCUMULATOR);
                break;
            default:
                throw new RuntimeException("Instruction not implemented: " + instr);
        }

    }

    /**
     * Only for JMP
     */
    private void indirect_addressing() {
        // fetch pointer address low, increment PC
        byte pointer_addr_low = read_memory(registers.PC);
        registers.PC ++;

        // fetch pointer address high, increment PC
        byte pointer_addr_high = read_memory(registers.PC);
        registers.PC ++;

        // fetch low address to latch
        byte latch_low = read_memory(Common.makeShort(pointer_addr_low, pointer_addr_high));

        // fetch PCH, copy latch to PCL
        byte latch_high = read_memory(Common.makeShort((byte) (pointer_addr_low + 1), pointer_addr_high));
        registers.PC = Common.makeShort(latch_low, latch_high);
    }

    /**
     * X-indexed indirect addressing
     * @param instr
     */
    private void indexed_indirect_addressing(Instructions instr) {
        switch(instr) {
            // Read instructions (LDA, ORA, EOR, AND, ADC, CMP, SBC, LAX)
            case LDA:
            case ORA:
            case EOR:
            case AND:
            case ADC:
            case CMP:
            case SBC:
            case LAX:
                // fetch pointer address, increment PC
                byte pointer_addr = read_memory(registers.PC);
                registers.PC ++;

                // read from the address, add X to it
                short addr = (short) (pointer_addr & 0xFF);
                byte effective_addr = read_memory((short) (pointer_addr & 0xFF));
                addr += registers.X;

                // fetch effective address low
                byte effective_addr_low = read_memory((short) (addr & 0xFF));

                // fetch effective address high
                byte effective_addr_high = read_memory((short) ((addr + 1) & 0xFF));

                // read from effective address
                fetched_data = read_memory(Common.makeShort(effective_addr_low, effective_addr_high));
                break;
            // Read-Modify-Write instructions (SLO, SRE, RLA, RRA, ISB, DCP)
            case SLO:
//            case SRE:
//            case RLA:
//            case RRA:
            case ISB:
            case DCP:
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SRE, RLA, RRA

                // fetch pointer address, increment PC
                pointer_addr = read_memory(registers.PC);
                registers.PC ++;

                // read from the address, add X to it
                addr = (short) (pointer_addr & 0xFF);
                effective_addr = read_memory((short) (pointer_addr & 0xFF));
                addr += registers.X;

                // fetch effective address low
                effective_addr_low = read_memory((short) (addr & 0xFF));

                // fetch effective address high
                effective_addr_high = read_memory((short) ((addr + 1) & 0xFF));

                // read from effective address
                fetched_data = read_memory(Common.makeShort(effective_addr_low, effective_addr_high));

                fetched_addr = Common.makeShort(effective_addr_low, effective_addr_high);

                // write the value back to effective address, and do the operation on it
                write_memory(fetched_addr, fetched_data);

                // This is done in the instruction implementations.
//                // write the new value to effective address
//                write_memory(Common.makeShort(effective_addr_low, effective_addr_high), fetched_data);


                break;
            // Write instructions (STA, SAX)
            case STA:
            case SAX:
                // fetch pointer address, increment PC
                pointer_addr = read_memory(registers.PC);
                registers.PC ++;

                // read from the address, add X to it
                addr = (short) (pointer_addr & 0xFF);
                effective_addr = read_memory((short) (pointer_addr & 0xFF));
                addr += registers.X;

                // fetch effective address low
                effective_addr_low = read_memory((short) (addr & 0xFF));

                // fetch effective address high
                effective_addr_high = read_memory((short) ((addr + 1) & 0xFF));

                // write to effective address
                fetched_addr = Common.makeShort(effective_addr_low, effective_addr_high);
                break;
            default:
                throw new RuntimeException("Instruction not implemented: " + instr);
        }
    }

    private void indirect_indexed_addressing(Instructions instr, AddressingMode addrmode) {
        byte register;
        if (addrmode == AddressingMode.INDIRECT_X)
            register = registers.X;
        else
            register = registers.Y;

        switch(instr) {
            // Read instructions (LDA, EOR, AND, ORA, ADC, SBC, CMP)
            case LDA:
            case EOR:
            case AND:
            case ORA:
            case ADC:
            case SBC:
            case CMP:
            case LAX:
                // fetch pointer address, increment PC
                byte pointer_addr = read_memory(registers.PC);
                registers.PC ++;

                // fetch effective address low
                byte effective_addr_low = read_memory((short) (pointer_addr & 0xFF));

                // fetch effective address high, add Y to low byte of effective address
                byte effective_addr_high = read_memory((short) ((pointer_addr +1) & 0xFF));
                byte new_effective_addr_low = (byte) (effective_addr_low + register);

                // read from effective address, fix high byte of effective address
                short effective_addr = Common.makeShort(new_effective_addr_low, effective_addr_high);
                fetched_data = read_memory(effective_addr);

                // Check page crossing
                if (Common.isAdditionCarry(effective_addr_low, register)) {
                    effective_addr += 0x100;

                    // read from effective address
                    fetched_data = read_memory(effective_addr);

                    // This cycle will be executed only if the effective address was invalid during cycle #5, i.e. page boundary was crossed.
                    //cycles ++;
                }


                break;
            // Read-Modify-Write instructions (SLO, SRE, RLA, RRA, ISB, DCP)
            case SLO:
            case SRE:
            case RLA:
            case RRA:
            case ISB:
            case DCP:
                // fetch pointer address, increment PC
                pointer_addr = read_memory(registers.PC);
                registers.PC ++;

                // fetch effective address low
                effective_addr_low = read_memory((short) (pointer_addr & 0xFF));

                // fetch effective address high, add Y to low byte of effective address
                effective_addr_high = read_memory((short) ((pointer_addr +1) & 0xFF));
                new_effective_addr_low = (byte) (effective_addr_low + register);

                // read from effective address, fix high byte of effective address
                effective_addr = Common.makeShort(new_effective_addr_low, effective_addr_high);
                fetched_data = read_memory(effective_addr);

                // Check page crossing
                if (Common.isAdditionCarry(effective_addr_low, register)) {
                    effective_addr += 0x100;

                    // This cycle will be executed only if the effective address was invalid during cycle #5, i.e. page boundary was crossed.
                    //cycles ++;
                }

                // read from effective address
                fetched_data = read_memory(effective_addr); // TODO: Problem here

                fetched_addr = effective_addr;

                // write the value back to effective address, and do the operation on it
                write_memory(effective_addr, fetched_data);

                // This is done in the instruction implementations.

                // write the new value to effective address
                break;
            // Write instructions (STA, SHA)
            case STA:
            case SHA:

                // fetch pointer address, increment PC
                pointer_addr = read_memory(registers.PC);
                registers.PC ++;

                // fetch effective address low
                effective_addr_low = read_memory((short) (pointer_addr & 0xFF));

                // fetch effective address high, add Y to low byte of effective address
                effective_addr_high = read_memory((short) ((pointer_addr +1) & 0xFF));
                new_effective_addr_low = (byte) (effective_addr_low + register);

                // read from effective address, fix high byte of effective address
                effective_addr = Common.makeShort(new_effective_addr_low, effective_addr_high);
                read_memory(effective_addr);

                if (Common.isAdditionCarry(effective_addr_low, register))
                    effective_addr += 0x100;

                fetched_addr = effective_addr;
                break;
            default:
                throw new RuntimeException("Instruction not implemented: " + instr);
        }
    }

    private void absolute_indexed_addressing(Instructions instr, AddressingMode addrmode) {
        byte register;
        if (addrmode == AddressingMode.ABSOLUTE_X)
            register = registers.X;
        else
            register = registers.Y;

        switch(instr) {
            // Read instructions (LDA, LDX, LDY, EOR, AND, ORA, ADC, SBC, CMP, BIT, LAX, LAE, SHS, NOP)
            case LDA:
            case LDX:
            case LDY:
            case EOR:
            case AND:
            case ORA:
            case ADC:
            case SBC:
            case CMP:
            case BIT:
            case NOP:
            case LAX:
            case LAE:
            case SHS:
                // fetch low byte of address, increment PC
                byte low_byte = read_memory(registers.PC);
                registers.PC ++;

                // fetch high byte of address, add index register to low address byte, increment PC
                byte high_byte = read_memory(registers.PC);
                byte new_low_byte = (byte) (low_byte + register);
                registers.PC ++;

                // read from effective address, fix the high byte of effective address
                short effective_addr = Common.makeShort(new_low_byte, high_byte);
                fetched_data = read_memory(effective_addr);

                // Check page boundary crossing
                if (Common.isAdditionCarry(low_byte, register)) {
                    effective_addr += 0x100;

                    // re-read from effective address
                    fetched_data = read_memory(effective_addr);
                }
                break;
            // Read-Modify-Write instructions (ASL, LSR, ROL, ROR, INC, DEC, SLO, SRE, RLA, RRA, ISB, DCP)
            case ASL:
            case LSR:
            case ROL:
            case ROR:
            case INC:
            case DEC:
            case DCP:
            case ISB:
            case RRA:
            case RLA:
            case SRE:
            case SLO:
                // fetch low byte of address, increment PC
                low_byte = read_memory(registers.PC);
                registers.PC ++;

                // fetch high byte of address, add index register X to low address byte, increment PC
                high_byte = read_memory(registers.PC);
                new_low_byte = (byte) (low_byte + register);
                registers.PC ++;

                // read from effective address, fix the high byte of effective address
                effective_addr = Common.makeShort(new_low_byte, high_byte);
                fetched_data = read_memory(effective_addr);
                if (Common.isAdditionCarry(low_byte, register))
                    effective_addr += 0x100;

                // re-read from effective address
                fetched_data = read_memory(effective_addr);

                // write the value back to effective address, and do the operation on it
                write_memory(effective_addr, fetched_data);

                // write the new value to effective address
                // Note: we store address, and after the addressing mode is finished, we execute in different place:
                fetched_addr = effective_addr;

                break;
            // Write instructions (STA, STX, STY, SHA, SHX, SHY)
            case STA:
            case STX:
            case STY:
            case SHA:
            case SHX:
            case SHY:
                // fetch low byte of address, increment PC
                low_byte = read_memory(registers.PC);
                registers.PC ++;

                // fetch high byte of address, add index register to low address byte, increment PC
                high_byte = read_memory(registers.PC);
                new_low_byte = (byte) (low_byte + register);
                registers.PC ++;

                // read from effective address, fix the high byte of effective address
                effective_addr = Common.makeShort(new_low_byte, high_byte);
                fetched_data = read_memory(effective_addr);

                if (Common.isAdditionCarry(low_byte, register)) {
                    effective_addr += 0x100;
                }

                // write to effective address
                // Note: we store address, and after the addressing mode is finished, we execute in different place
                fetched_addr = effective_addr;

                break;
            default:
                throw new IllegalArgumentException("Invalid absolute addressing, instruction: " + instr);
        }
    }

    private void accumulator_or_implied_addressing(Instructions instr) {
        // read next instruction byte (and throw it away)
        read_memory(registers.PC);
    }

    private void immediate_addressing() {
        // fetch value, increment PC
        byte value = read_memory(registers.PC);
        registers.PC ++;

        fetched_data = value;
    }

    private void zeropage_indexed_addressing(Instructions instr, AddressingMode addrmode) {
        switch(instr) {
            // Read instructions (LDA, LDX, LDY, EOR, AND, ORA, ADC, SBC, CMP, BIT, LAX, NOP)
            case LDA:
            case LDX:
            case LDY:
            case EOR:
            case AND:
            case ORA:
            case ADC:
            case SBC:
            case CMP:
            case BIT:
            case NOP:
            case LAX:
                // fetch address, increment PC
                byte addr_low = read_memory(registers.PC);
                registers.PC ++;

                // read from address, add index register to it
                read_memory((short) (addr_low & 0xFF));
                byte register;
                // TODO: Move register outside of switch-case this should be at top of function
                if (addrmode == AddressingMode.ZEROPAGE_X)
                    register = registers.X;
                else
                    register = registers.Y;
                addr_low += register;

                // read from effective address
                short effective_addr = Common.makeShort(addr_low, (byte) 0x00);
                fetched_data = read_memory(effective_addr);
                break;
            // Read-Modify-Write instructions (ASL, LSR, ROL, ROR, INC, DEC, SLO, SRE, RLA, RRA, ISB, DCP)
            case ASL:
            case LSR:
            case ROL:
            case ROR:
            case INC:
            case DEC:
            case DCP:
            case ISB:
            case SLO:
            case SRE:
            case RLA:
            case RRA:
                // fetch address, increment PC
                addr_low = read_memory(registers.PC);
                registers.PC ++;

                // read from address, add index register X to it
                read_memory((short) (addr_low & 0xFF));
                addr_low += registers.X;

                // read from effective address
                effective_addr = Common.makeShort(addr_low, (byte) 0x00);
                fetched_data = read_memory(effective_addr);

                // write the value back to effective address, and do the operation on it
                write_memory(effective_addr, fetched_data);

                // write the new value to effective address
                // Note: we store address, and after the addressing mode is finished, we execute in different place
                fetched_addr = effective_addr;
                break;
            // Write instructions (STA, STX, STY, SAX)
            case STA:
            case STX:
            case STY:
            case SAX:
                // fetch address, increment PC
                addr_low = read_memory(registers.PC);
                registers.PC ++;

                // read from address, add index register to it
                read_memory((short) (addr_low & 0xFF));
                if (addrmode == AddressingMode.ZEROPAGE_X)
                    register = registers.X;
                else
                    register = registers.Y;
                addr_low += register;

                // write to effective address
                // Note: we store address, and after the addressing mode is finished, we execute in different place
                fetched_addr = Common.makeShort(addr_low, (byte) 0x00);
                break;
            default:
                throw new IllegalArgumentException("Invalid instruction: " + instr);
        }
    }

    private void zeropage_addressing(Instructions instr) {
        byte addr_low;
        short effective_addr;

        switch(instr) {
            // Read instructions (LDA, LDX, LDY, EOR, AND, ORA, ADC, SBC, CMP, BIT, LAX, NOP)
            case LDA:
            case LDX:
            case LDY:
            case EOR:
            case AND:
            case ORA:
            case ADC:
            case SBC:
            case CMP:
            case BIT:
            case NOP:
            case CPX: // Added CPX because of CMP, it was not mentioned in the documentation (http://www.atarihq.com/danb/files/64doc.txt)
            case CPY: // Same for CPY
            case LAX:
                // fetch address, increment PC
                addr_low = read_memory(registers.PC);
                registers.PC ++;

                // read from effective address
                effective_addr = Common.makeShort(addr_low, (byte) 0x00);
                fetched_data = read_memory(effective_addr);
                break;
            // Read-Modify-Write instructions (ASL, LSR, ROL, ROR, INC, DEC, SLO, SRE, RLA, RRA, ISB, DCP)
            case ASL:
            case LSR:
            case ROL:
            case ROR:
            case INC:
            case DEC:
            case DCP:
            case ISB:
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SLO, SRE, RLA, RRA

                // fetch address, increment PC
                addr_low = read_memory(registers.PC);
                registers.PC ++;

                // read from effective address
                effective_addr = Common.makeShort(addr_low, (byte) 0x00);
                fetched_data = read_memory(effective_addr);

                // write the value back to effective address, and do the operation on it
                write_memory(effective_addr, fetched_data);

                // Note: here we do the operation on the fetched data. We only store effective address and each
                // instruction will do what is meant to do (not in addressing mode, but in the execute function of the CPU
                this.fetched_addr = effective_addr;
                break;
            // Write instructions (STA, STX, STY, SAX)
            case STA:
            case STX:
            case STY:
            case SAX:
                // fetch address, increment PC
                addr_low = read_memory(registers.PC);
                registers.PC ++;

                // write register to effective address
                // Note: we store address, and after the addressing mode is finished, we execute in different place
                fetched_addr = Common.makeShort(addr_low, (byte) 0x00);
                break;
            default:
                throw new RuntimeException("Instruction not implemented: " + instr);
        }
    }

    private void absolute_addressing(Instructions instruction) {
        byte addr_low, addr_high;

       switch(instruction) {
           // JMP
           case JMP:
               // fetch low address byte, increment PC
               addr_low = read_memory(registers.PC);
               registers.PC ++;

               // copy low address byte to PCL, fetch high address byte to PCH
               registers.PC = Common.makeShort(addr_low, read_memory(registers.PC));
               break;
           // Read instructions: LDA, LDX, LDY, EOR, AND, ORA, ADC, SBC, CMP, BIT, LAX, NOP
           case LDA:
           case LDX:
           case LDY:
           case EOR:
           case AND:
           case ORA:
           case ADC:
           case SBC:
           case CMP:
           case BIT:
           case NOP:
           case CPX: // Added CPX because of CMP, it was not mentioned in the documentation (http://www.atarihq.com/danb/files/64doc.txt)
           case CPY: // Same for CPY
           case LAX:
                //fetch low byte of address, increment PC
                addr_low = read_memory(registers.PC);
                registers.PC ++;

                // fetch high byte of address, increment PC
                addr_high = read_memory(registers.PC);
                registers.PC ++;

                // read from effective address
                short effective_addr = Common.makeShort(addr_low, addr_high);
                fetched_data = read_memory(effective_addr);
                break;
           // Read-Modify-Write instructions (ASL, LSR, ROL, ROR, INC, DEC, SLO, SRE, RLA, RRA, ISB, DCP)
           case ASL:
           case LSR:
           case ROL:
           case ROR:
           case INC:
           case DEC:
           case DCP:
           case ISB:
           case RRA:
           case RLA:
           case SRE:
           case SLO:
               //fetch low byte of address, increment PC
               addr_low = read_memory(registers.PC);
               registers.PC ++;

               // fetch high byte of address, increment PC
               addr_high = read_memory(registers.PC);
               registers.PC ++;

               // read from effective address
               effective_addr = Common.makeShort(addr_low, addr_high);
               fetched_data = read_memory(effective_addr);

               // write the value back to effective address, and do the operation on it
               // Note: we store address, and after the addressing mode is finished, we execute in different place
               fetched_addr = effective_addr;

               // write the new value to effective address
                write_memory(effective_addr, fetched_data);
               break;
           // Write instructions (STA, STX, STY, SAX)
           case STA:
           case STX:
           case STY:
           case SAX:
                // fetch low byte of address, increment PC
                addr_low = read_memory(registers.PC);
                registers.PC ++;

                // fetch high byte of address, increment PC
                addr_high = read_memory(registers.PC);
                registers.PC ++;

                // write register to effective address
                // Note: we store address, and after the addressing mode is finished, we execute in different place
                fetched_addr = Common.makeShort(addr_low, addr_high);
                break;
           default:
                throw new RuntimeException("Did not expect this instruction here");
       }
    }

    private void relative_addressing(Instructions instr) {
        switch(instr) {
            // Branch instructions (BCC, BCS, BNE, BEQ, BPL, BMI, BVC, BVS)
            case BPL:
            case BMI:
            case BVC:
            case BVS:
            case BCC:
            case BCS:
            case BNE:
            case BEQ:
                byte operand = read_memory(registers.PC);
                registers.PC ++;

                short addr_tmp = (short) (registers.PC + operand);
                byte addr_tmp_high = (byte) ((addr_tmp >> 8) & 0xFF);
                if (
                        (instr == Instructions.BMI && registers.getFlag(NEGATIVE)     == true)    ||
                                (instr == Instructions.BPL && registers.getFlag(NEGATIVE)     == false)   ||
                                (instr == Instructions.BNE && registers.getFlag(ZERO)         == false)   ||
                                (instr == Instructions.BVC && registers.getFlag(OVERFLOW)     == false)   ||
                                (instr == Instructions.BVS && registers.getFlag(OVERFLOW)     == true)    ||
                                (instr == Instructions.BEQ && registers.getFlag(ZERO)         == true)    ||
                                (instr == Instructions.BCS && registers.getFlag(CARRY)        == true)    ||
                                (instr == Instructions.BCC && registers.getFlag(CARRY)        == false)) {
                    // Branch taken

                    read_memory(registers.PC); // dummy read

                    // Set low 8-bits of PC to low 8-bits of addr_tmp
                    registers.PC = (short) ((registers.PC & 0xFF00) | (addr_tmp & 0xFF));
                    if (addr_tmp_high != (byte) (registers.PC >> 8)) {
                        // Page boundary crossed
                        read_memory(registers.PC); // dummy read
                        registers.PC = addr_tmp;
                    }
//                    read_memory(registers.PC); // dummy read
//                    registers.PC ++;
                } else {
                    // Branch is not taken
                    //registers.PC ++;
                }
                break;
            default:
                throw new RuntimeException("Instruction not implemented: " + instr);



//            case BPL:
//            case BMI:
//            case BVC:
//            case BVS:
//            case BCC:
//            case BCS:
//            case BNE:
//            case BEQ:
//                // fetch relative address, increment PC
//                byte relative_addr = read_memory(registers.PC);
//                registers.PC ++;
//
//                // add relative address to PC
//                fetched_addr = (short) (registers.PC + relative_addr);
//                break;
//            default:
//                throw new RuntimeException("Instruction not implemented: " + instr);
        }
    }

    private void push_stack(byte data) {
//        logger.debug("Pushing to stack: " +
//                Common.byteToHex(data, true) +
//                " at address: " + Common.byteToHex(registers.S, true));
        write_memory(Common.makeShort(registers.S, (byte) 0x01), data);
        registers.S = (byte) (registers.S - 1);
    }

    public byte pop_stack() {
        //read_memory(Common.makeShort(registers.S, (byte) 0x01)); // dummy read
        registers.S = (byte) ((registers.S & 0xFF) + 1);
        return read_memory(Common.makeShort(registers.S, (byte) 0x01));
    }

    /**
     * Push PC onto stack. Pushes high byte first, then low byte.
     */
    private void push_pc() {
        byte pc_msb = (byte) ((registers.PC) >> 8);
        byte pc_lsb = (byte) (registers.PC);
        push_stack(pc_msb); // store high
        push_stack(pc_lsb); // store low
    }

    /**
     * Execute cmp instruction
     */
    private void exec_cmp(byte register) {
		/*
		Link: http://www.6502.org/tutorials/compare_instructions.html
		Compare Results | N | Z | C
		---------------------------
		A, X, or Y < M  | * | 0 | 0
		A, X, or Y = M  | 0 | 1 | 1
		A, X, or Y > M  | * | 0 | 1

		*The N flag will be bit 7 of A, X, or Y - Memory
		*/
        byte sub = (byte) (register - fetched_data);
        boolean last_bit = Common.Bits.getBit(sub, 7);

        boolean new_n = false, new_z = false, new_c = false;

        if ((register & 0xFF) < (fetched_data & 0xFF)) {
            new_n = last_bit;
        } else if (register == fetched_data) {
            new_z = new_c = true;
        } else {
            new_n = last_bit;
            new_c = true;
        }

        registers.setFlag(NEGATIVE, new_n);
        registers.setFlag(ZERO, new_z);
        registers.setFlag(CARRY, new_c);
    }

    public void nmi_interrupt() {
        //logger.debug("NMI interrupt called");
        // Store current flags onto stack and when returning, restore them.
        push_pc();

        setFlag(BREAK, false);
        setFlag(UNUSED, true);
        setFlag(INTERRUPT, true);

        push_stack(registers.P);

        byte vector_lsb = read_memory((short) 0xFFFA);
        byte vector_msb = read_memory((short) 0xFFFB);
        registers.PC = Common.makeShort(vector_lsb, vector_msb);

        cycles += 8; // TODO: IDK why but he does this: https://github.com/OneLoneCoder/olcNES/blob/ac5ce64cdb3a390a89d550c5f130682b37eeb080/Part%232%20-%20CPU/olc6502.cpp#L248C2-L248C2
    }

    public void irq_interrupt() {
        // if interrupt flag is not set, then do interrupt. Else, ignore. (This is the I flag - ignore interrupts if set)
        if (!registers.getFlag(INTERRUPT)) {
            push_pc();

            setFlag(BREAK, false);
            setFlag(UNUSED, true);
            setFlag(INTERRUPT, true);
            push_stack(registers.P);

            byte vector_lsb = read_memory((short) 0xFFFE);
            byte vector_msb = read_memory((short) 0xFFFF);
            short new_pc = Common.makeShort(vector_lsb, vector_msb);
            registers.PC = new_pc;

            cycles += 7; // TODO: IDK why
        }
    }

    private void exec_lda() {
        registers.A = fetched_data;
        registers.setFlag(NEGATIVE, Common.Bits.getBit(fetched_data, 7));
        registers.setFlag(ZERO, fetched_data == 0);
    }

    private void exec_adc() {
        int result_int = registers.A + fetched_data + (registers.getFlag(CARRY) ? 1 : 0);
        byte result = (byte) result_int;

        registers.modify_n(result);
        registers.modify_z(result);
        registers.modify_c(registers.A, fetched_data, (byte) (registers.getFlag(CARRY) ? 1 : 0));

        int a = result_int ^ registers.A;
        int b = result_int ^ fetched_data;
        int c = a & b & 0x80;
        boolean overflow = (c != 0);

        registers.setFlag(OVERFLOW, overflow);
        registers.A = result;
    }

    private void exec_tax() {
        registers.X = registers.A;
        registers.setFlag(ZERO, registers.X == 0);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.X, 7));
    }

    private void exec_tay() {
        registers.Y = registers.A;
        registers.setFlag(ZERO, registers.Y == 0);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.Y, 7));
    }

    private void exec_lsr(boolean is_accumulator) {
        if (is_accumulator)
            fetched_data = registers.A;

        // In addressing mode we already fetched it. So we don't need to read again.
//        else
//            fetched_data = read_memory(fetched_addr);

        boolean is_carry = Common.Bits.getBit(fetched_data, 0);
        byte result = (byte) ((fetched_data & 0xFF) >> 1);
        registers.setFlag(CARRY, is_carry);
        registers.setFlag(ZERO, result == 0);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(result, 7));

        if (is_accumulator)
            registers.A = result;
        else
            write_memory(fetched_addr, result);
    }

    private void exec_rol_or_ror(boolean is_accumulator, boolean is_left_shift) {
        if (is_accumulator)
            fetched_data = registers.A;

        // In addressing mode we already fetched it. So we don't need to read again.
//        else
//            fetched_data = read_memory(fetched_addr);

        boolean in_carry = registers.getFlag(CARRY);
        boolean out_carry;
        byte result;
        if (is_left_shift) {
            result = (byte) ((fetched_data & 0xFF) << 1);
            out_carry = Common.Bits.getBit(fetched_data, 7);
            result = Common.Bits.setBit(result, 0, in_carry);
        }
        else {
            result = (byte) ((fetched_data & 0xFF) >> 1);
            out_carry = Common.Bits.getBit(fetched_data, 0);
            result = Common.Bits.setBit(result, 7, in_carry);
        }

        registers.setFlag(CARRY, out_carry);
        registers.setFlag(ZERO, result == 0);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(result, 7));

        if (is_accumulator)
            registers.A = result;
        else
            write_memory(fetched_addr, result);
    }

    private void exec_and() {
        registers.A = (byte) (registers.A & fetched_data);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.A, 7));
        registers.setFlag(ZERO, registers.A == 0);
    }

    private void exec_tsx() {
        registers.X = registers.S;
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.X, 7));
        registers.setFlag(ZERO, registers.X == 0);
    }

    private void exec_txa() {
        registers.A = registers.X;
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.A, 7));
        registers.setFlag(ZERO, registers.A == 0);
    }

    private void exec_ora() {
        registers.A = (byte) (registers.A | fetched_data);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.A, 7));
        registers.setFlag(ZERO, registers.A == 0);
    }

    private void exec_inc_or_dec(boolean is_inc) {
        byte result = (byte) (fetched_data + (is_inc ? 1 : -1));
        registers.setFlag(NEGATIVE, Common.Bits.getBit(result, 7));
        registers.setFlag(ZERO, result == 0);
        write_memory(fetched_addr, result);
    }

    private void exec_ldx() {
        registers.X = fetched_data;
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.X, 7));
        registers.setFlag(ZERO, registers.X == 0);
    }

    private void exec_bit() {
        registers.setFlag(NEGATIVE, Common.Bits.getBit(fetched_data, 7));
        registers.setFlag(OVERFLOW, Common.Bits.getBit(fetched_data, 6));
        registers.setFlag(ZERO, (registers.A & fetched_data) == 0);
    }

    private void exec_eor() {
        registers.A = (byte) (registers.A ^ fetched_data);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.A, 7));
        registers.setFlag(ZERO, registers.A == 0);
    }

    private void exec_ldy() {
        registers.Y = fetched_data;
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.Y, 7));
        registers.setFlag(ZERO, registers.Y == 0);
    }

    private void exec_sbc() {
        // Like ADC but we invert the fetched data
        fetched_data = (byte) (fetched_data ^ 0xFF);

        byte result = (byte) (registers.A + fetched_data + (registers.getFlag(CARRY) ? 1 : 0));

        byte m_plus_a = (byte) (fetched_data + registers.A);
        boolean is_carry = Common.isAdditionCarry(fetched_data, registers.A);
        boolean is_carry2 = Common.isAdditionCarry(m_plus_a, (byte) (registers.getFlag(CARRY) ? 1 : 0));
        boolean negative_flag_set = ((registers.A ^ result) & (fetched_data ^ result) & 0x80) != 0;

        registers.modify_n(result);
        registers.modify_z(result);
        registers.setFlag(CARRY, is_carry || is_carry2);
        registers.setFlag(OVERFLOW, negative_flag_set);
        registers.A = result;
    }

    private void exec_iny() {
        registers.Y = (byte) (registers.Y + 1);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.Y, 7));
        registers.setFlag(ZERO, registers.Y == 0);
    }

    private void exec_inx() {
        registers.X = (byte) (registers.X + 1);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.X, 7));
        registers.setFlag(ZERO, registers.X == 0);
    }

    private void exec_dey() {
        registers.Y = (byte) (registers.Y - 1);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.Y, 7));
        registers.setFlag(ZERO, registers.Y == 0);
    }

    private void exec_dex() {
        registers.X = (byte) (registers.X - 1);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.X, 7));
        registers.setFlag(ZERO, registers.X == 0);
    }

    private void exec_tya() {
        registers.A = registers.Y;
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.A, 7));
        registers.setFlag(ZERO, registers.A == 0);
    }

    private void exec_asl(boolean is_accumulator) {
        if (is_accumulator)
            fetched_data = registers.A;

        // In addressing mode we already fetched it. So we don't need to read again.
//        else
//            fetched_data = read_memory(fetched_addr);

        boolean carry_out = Common.Bits.getBit(fetched_data, 7);
        byte result = (byte) ((fetched_data & 0xFF) << 1);
        registers.setFlag(CARRY, carry_out);
        registers.setFlag(ZERO, result == 0);
        registers.setFlag(NEGATIVE, Common.Bits.getBit(result, 7));

        if (is_accumulator)
            registers.A = result;
        else
            write_memory(fetched_addr, result);
    }

    private void exec_lax() {
        registers.A = fetched_data;
        registers.X = fetched_data;
        registers.setFlag(NEGATIVE, Common.Bits.getBit(registers.A, 7));
        registers.setFlag(ZERO, registers.A == 0);
    }

    private void exec_sax() {
        byte result = (byte) (registers.A & registers.X);
        write_memory(fetched_addr, result);
    }

    private void exec_dcp() {
        fetched_data -= 1;
        write_memory(fetched_addr, fetched_data);
        exec_cmp(registers.A);
    }

    private void setFlag(Flags flag, boolean value) {
        registers.setFlag(flag, value);
    }

    private void exec_isb() {
        fetched_data += 1;
        write_memory(fetched_addr, fetched_data);

        int not_carry = (registers.getFlag(CARRY) ? 0 : 1);

        byte result = (byte) (registers.A - fetched_data - not_carry);

        byte a_minus_m = (byte) (registers.A - fetched_data);
        byte a_minus_m_minus_c_tag = (byte) (a_minus_m - not_carry);

        boolean borrowOut = (a_minus_m & 0x100) != 0; // Check if bit 8 (0x100) is set.
        boolean borrowOut2 = (a_minus_m_minus_c_tag & 0x100) != 0; // Check if bit 8 (0x100) is set.

        boolean negative_flag_set = ((registers.A ^ result) & (fetched_data ^ result) & 0x80) != 0;

        registers.modify_n(result);
        registers.modify_z(result);
        registers.setFlag(CARRY, borrowOut || borrowOut2);
        registers.setFlag(OVERFLOW, negative_flag_set);
        registers.A = result;


        /*

        fetched_data = (byte) (fetched_data ^ 0xFF);

        byte result = (byte) (registers.A + fetched_data + (registers.getFlag(CARRY) ? 1 : 0));

        byte m_plus_a = (byte) (fetched_data + registers.A);
        boolean is_carry = Common.isAdditionCarry(fetched_data, registers.A);
        boolean is_carry2 = Common.isAdditionCarry(m_plus_a, (byte) (registers.getFlag(CARRY) ? 1 : 0));
        boolean negative_flag_set = ((registers.A ^ result) & (fetched_data ^ result) & 0x80) != 0;

        registers.modify_n(result);
        registers.modify_z(result);
        registers.setFlag(CARRY, is_carry || is_carry2);
        registers.setFlag(OVERFLOW, negative_flag_set);
        registers.A = result;

         */

    }

    private void exec_jam() {
        throw new RuntimeException("Not yet implemented");
    }

    private void exec_rra() {
        throw new RuntimeException("Not yet implemented");
    }

    private void exec_slo(boolean is_accumulator) {
        exec_asl(is_accumulator);
        exec_ora();
    }
}
