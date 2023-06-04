package NES.CPU;

import NES.CPU.Registers.CPURegisters;
import NES.Common;
import NES.PPU.PPURegisters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CPU {

    private final Logger logger = LoggerFactory.getLogger(CPU.class);

    public final CPURegisters registers;
    public long cycles;
    private final byte[] memory; // All addressable memory (64KB)

    private final Decoder decoder;

    private final PPURegisters ppuRegisters;
    public long instructions = 0; // counter number of instructions executed

    private boolean is_record_memory; // Only used in testing. If true, the CPU will record when memory is read and written.
    private List<MemoryAccessRecord> recorded_memory; // Only used in testing. If record_memory is true, this will contain all the memory reads and writes.

    private byte fetched_data; // Set in addressing modes, used afterwards.
    private short fetched_addr; // Set in addressing modes, used afterwards.

    public CPU(byte[] cpu_memory, PPURegisters ppuRegisters) {
        if (cpu_memory.length != 1024 * 64)
            throw new RuntimeException("Unexpected CPU memory address space size");

        this.memory = cpu_memory;
        this.ppuRegisters = ppuRegisters;

        this.registers = new CPURegisters();
        this.decoder = new Decoder();
    }

    public void clock_tick() {
//        logger.debug("Tick, cycle: " + this.cycles);
//        logger.debug(registers.toString());

        // Fetch
        byte opcode = read_memory(registers.getPC()); // Read at address of Program Counter (duh!)
        registers.incrementPC(); // Increment PC

        // Decode
        Decoder.InstructionInfo instr_info = decoder.decode_opcode(opcode);
        Instructions instr = instr_info.instr;
        AddressingMode addrmode = instr_info.addrmode;
//        int bytes = instr_info.bytes;
//        int cycles = instr_info.cycles;
//        Decoder.OopsCycle oops_cycle = instr_info.oopsCycle;
//        logger.debug(
//                instr.toString()+"("+Common.byteToHex(opcode, true)+")\t"
//                +addrmode+"\tBytes: "
//                +bytes+"\tCycles: "
//                +cycles+"\tOops cycle: "
//                +oops_cycle);

        // Execute
        execute_instruction(instr, addrmode);

        //TODO: Check OOPS cycle.

        // Check NMI interrupt
        if (ppuRegisters != null && ppuRegisters.isNmiEnabled()) {
            logger.debug("NMI interrupt called");
            ppuRegisters.setNmiEnabled(false); // After reading the NMI flag ($2002) , it is cleared.
            nmi_interrupt();
        }

        instructions ++;
    }

    private byte read_memory(short addr) {
        //TODO: Add mapping here. For now I only support mapper 0.

        byte res;

        // TODO: Do something about PPU memory. I think the PPU should modify the CPU address space
//        // Map certain addresses to PPU if needed
//        switch (addr) {
//            case 0x2002 -> {
//                res = ppuRegisters.readStatus();
//            }
//            case 0x2000 -> {
//                res = ppuRegisters.getCtrl();
//            }
//            default -> {
//                // Note: 'addr' is short, which means in Java it can be negative. However we deal with unsigned numbers.
//                // This is the best way to convert any signed number to unsigned, which allows accessing arrays.
//                res = memory[addr & 0xFFFF];
//            }
//        }\
        res = memory[addr & 0xFFFF];
//        logger.debug("Reading memory: [" +
//                (addr & 0xFFFF) + " (" + Common.shortToHex(addr, true) + ")] = " + (res & 0xFF) +" ("+
//                Common.byteToHex(res, true) + ")");
        if (is_record_memory)
            recorded_memory.add(new MemoryAccessRecord(addr, res, true));
        cycles ++;
        return res;
    }

    public void res_interrupt() {
        logger.debug("Reset interrupt called");

        registers.reset();

        short new_pc = read_address_from_memory((short) 0xFFFC);
        logger.debug("Jumping to interrupt address: " + Common.shortToHex(new_pc, true));

        registers.setPC(new_pc);
        cycles = 8;
    }

    private short read_address_from_memory(short addr) {
        byte lsb = read_memory(addr);
        byte msb = read_memory((short) (addr + 1));
        return Common.makeShort(lsb, msb);
    }

    private void execute_instruction(Instructions instr, AddressingMode addrmode) {
        // This help me to make the CPU cycle accurate:
        // http://www.atarihq.com/danb/files/64doc.txt

        boolean is_instructions_accessing_the_stack = false;
        switch(instr) {
            // Instructions accessing the stack
            case BRK:
                // read next instruction byte (and throw it away), increment PC
                read_memory(registers.getPC());
                registers.incrementPC();

                // push PCH on stack, decrement S
                push_stack((byte) (registers.getPC() >> 8));

                // push PCL on stack, decrement S
                push_stack((byte) (registers.getPC() & 0xFF));

                // push P on stack (with B flag set), decrement S
                // TODO: Expected to write 113 (0x71) but mine is writing 105 (0x69)
                push_stack((byte) (registers.getP().getAllFlags() | 0b00010000));

                // fetch PCL
                byte pcl = read_memory((short) 0xFFFE);

                // fetch PCH
                byte pch = read_memory((short) 0xFFFF);

                registers.setPC(Common.makeShort(pcl, pch));

                // Set interrupt disable flag (bit 2 of status register)
                registers.getP().setInterruptDisable(true);

                break;
            case RTI:
                throw new RuntimeException("RTI instruction not implemented");
            case RTS:
                throw new RuntimeException("RTS instruction not implemented");
            case PHA:
            case PHP:
                throw new RuntimeException("PHA/PHP instruction not implemented");
            case PLA:
            case PLP:
                throw new RuntimeException("PLA/PLP instruction not implemented");
            case JSR:
                // fetch low address byte, increment PC
                byte addr_low = read_memory(registers.getPC());
                registers.incrementPC();

                // internal operation (predecrement S?)
                read_memory(Common.makeShort(registers.getS(), (byte) 0x01));

                // push PCH on stack, decrement S
                push_stack((byte) ((registers.getPC()) >> 8));

                // push PCL on stack, decrement S
                push_stack((byte) ((registers.getPC()) & 0xFF));

                // copy low address byte to PCL, fetch high address byte to PCH
                byte pc_high = read_memory(registers.getPC());
                registers.setPC(Common.makeShort(addr_low, pc_high));
                break;
            default:
                is_instructions_accessing_the_stack = true;
                break;
        }

        if (!is_instructions_accessing_the_stack)
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
            case INDIRECT_Y:
                indirect_indexed_addressing(instr, addrmode);
                break;
            // Indirect indexed addressing
            // TODO: ??

            // Absolute indirect addressing (JMP)
            // TODO: ??
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
                registers.getP().setInterruptDisable(false);
                break;
            case STA:
                write_memory(fetched_addr, registers.getA());
                break;
            case TAX:
                exec_tax();
                break;
            case CPX:
                exec_cmp(addrmode, registers.getX());
                break;
            case CLV:
                registers.getP().setOverflow(false);
                break;
            case TAY:
                exec_tay();
                break;
            case CPY:
                exec_cmp(addrmode, registers.getY());
                break;
            case STX:
                write_memory(fetched_addr, registers.getX());
                break;
            case STY:
                write_memory(fetched_addr, registers.getY());
                break;
            case BCC:
            case LSR:
                exec_lsr(addrmode == AddressingMode.ACCUMULATOR);
                break;
            case CMP:
                exec_cmp(addrmode, registers.getA());
                break;
            default:
                throw new RuntimeException("Instruction not implemented: " + instr);
        }

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
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // LAX

                // fetch pointer address, increment PC
                byte pointer_addr = read_memory(registers.getPC());
                registers.incrementPC();

                // read from the address, add X to it
                short addr = (short) (pointer_addr & 0xFF);
                byte effective_addr = read_memory((short) (pointer_addr & 0xFF));
                addr += registers.getX();

                // fetch effective address low
                byte effective_addr_low = read_memory((short) (addr & 0xFF));

                // fetch effective address high
                byte effective_addr_high = read_memory((short) ((addr + 1) & 0xFF));

                // read from effective address
                fetched_data = read_memory(Common.makeShort(effective_addr_low, effective_addr_high));
                break;
            // Read-Modify-Write instructions (SLO, SRE, RLA, RRA, ISB, DCP)
//            case SLO:
//            case SRE:
//            case RLA:
//            case RRA:
//            case ISB:
//            case DCP:
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SLO, SRE, RLA, RRA, ISB, DCP
//                throw new RuntimeException("Read-Modify-Write instructions not implemented: " + instr);
            // Write instructions (STA, SAX)
            case STA:
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SAX

                // fetch pointer address, increment PC
                pointer_addr = read_memory(registers.getPC());
                registers.incrementPC();

                // read from the address, add X to it
                addr = (short) (pointer_addr & 0xFF);
                effective_addr = read_memory((short) (pointer_addr & 0xFF));
                addr += registers.getX();

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
            register = registers.getX();
        else
            register = registers.getY();

        switch(instr) {
            // Read instructions (LDA, EOR, AND, ORA, ADC, SBC, CMP)
            case LDA:
            case EOR:
            case AND:
            case ORA:
            case ADC:
            case SBC:
            case CMP:
                // fetch pointer address, increment PC
                byte pointer_addr = read_memory(registers.getPC());
                registers.incrementPC();

                // fetch effective address low
                byte effective_addr_low = read_memory((short) (pointer_addr & 0xFF));

                // fetch effective address high, add Y to low byte of effective address
                // TODO: Need to read at address 149 (0x95)
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
                    cycles ++;
                }


                break;
            // Read-Modify-Write instructions (SLO, SRE, RLA, RRA, ISB, DCP)
//            case SLO:
//            case SRE:
//            case RLA:
//            case RRA:
//            case ISB:
//            case DCP:
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SLO, SRE, RLA, RRA, ISB, DCP
//                throw new RuntimeException("Instruction not implemented: " + instr);
            // Write instructions (STA, SHA)
            case STA:
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SHA

                // fetch pointer address, increment PC
                pointer_addr = read_memory(registers.getPC());
                registers.incrementPC();

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
        }
    }

    private void absolute_indexed_addressing(Instructions instr, AddressingMode addrmode) {
        byte register;
        if (addrmode == AddressingMode.ABSOLUTE_X)
            register = registers.getX();
        else
            register = registers.getY();

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
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // LAX, LAE, SHS

                // fetch low byte of address, increment PC
                byte low_byte = read_memory(registers.getPC());
                registers.incrementPC();

                // fetch high byte of address, add index register to low address byte, increment PC
                byte high_byte = read_memory(registers.getPC());
                byte new_low_byte = (byte) (low_byte + register);
                registers.incrementPC();

                // read from effective address, fix the high byte of effective address
                short effective_addr = Common.makeShort(new_low_byte, high_byte);
                fetched_data = read_memory(effective_addr);

                // Check page boundary crossing
                if (Common.isAdditionCarry(low_byte, register)) {
                    effective_addr += 0x100;

                    // re-read from effective address
                    fetched_data = read_memory(effective_addr);

                    // This cycle will be executed only if the effective address was invalid during cycle #4, i.e. page boundary was crossed.
                    cycles ++;
                }
                break;
            // Read-Modify-Write instructions (ASL, LSR, ROL, ROR, INC, DEC, SLO, SRE, RLA, RRA, ISB, DCP)
            case ASL:
            case LSR:
            case ROL:
            case ROR:
            case INC:
            case DEC:
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SLO, SRE, RLA, RRA, ISB, DCP

                // fetch low byte of address, increment PC
                low_byte = read_memory(registers.getPC());
                registers.incrementPC();

                // fetch high byte of address, add index register X to low address byte, increment PC
                high_byte = read_memory(registers.getPC());
                new_low_byte = (byte) (low_byte + register);
                registers.incrementPC();

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
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SHA, SHX, SHY

                // fetch low byte of address, increment PC
                low_byte = read_memory(registers.getPC());
                registers.incrementPC();

                // fetch high byte of address, add index register to low address byte, increment PC
                high_byte = read_memory(registers.getPC());
                new_low_byte = (byte) (low_byte + register);
                registers.incrementPC();

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
        }
    }

    private void accumulator_or_implied_addressing(Instructions instr) {
        // read next instruction byte (and throw it away)
        read_memory(registers.getPC());
    }

    private void immediate_addressing() {
        // fetch value, increment PC
        byte value = read_memory(registers.getPC());
        registers.incrementPC();

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
                // fetch address, increment PC
                byte addr_low = read_memory(registers.getPC());
                registers.incrementPC();

                // read from address, add index register to it
                read_memory((short) (addr_low & 0xFF));
                byte register;
                // TODO: Move register outside of switch-case this should be at top of function
                if (addrmode == AddressingMode.ZEROPAGE_X)
                    register = registers.getX();
                else
                    register = registers.getY();
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
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SLO, SRE, RLA, RRA, ISB, DCP

                // fetch address, increment PC
                addr_low = read_memory(registers.getPC());
                registers.incrementPC();

                // read from address, add index register X to it
                read_memory((short) (addr_low & 0xFF));
                addr_low += registers.getX();

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
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SAX

                // fetch address, increment PC
                addr_low = read_memory(registers.getPC());
                registers.incrementPC();

                // read from address, add index register to it
                read_memory((short) (addr_low & 0xFF));
                if (addrmode == AddressingMode.ZEROPAGE_X)
                    register = registers.getX();
                else
                    register = registers.getY();
                addr_low += register;

                // write to effective address
                // Note: we store address, and after the addressing mode is finished, we execute in different place
                fetched_addr = Common.makeShort(addr_low, (byte) 0x00);
                break;
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
                // fetch address, increment PC
                addr_low = read_memory(registers.getPC());
                registers.incrementPC();

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
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SLO, SRE, RLA, RRA, ISB, DCP

                // fetch address, increment PC
                addr_low = read_memory(registers.getPC());
                registers.incrementPC();

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
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SAX

                // fetch address, increment PC
                addr_low = read_memory(registers.getPC());
                registers.incrementPC();

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
                throw new RuntimeException("Not implemented yet");
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
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // LAX

                //fetch low byte of address, increment PC
                addr_low = read_memory(registers.getPC());
                registers.incrementPC();

                // fetch high byte of address, increment PC
                addr_high = read_memory(registers.getPC());
                registers.incrementPC();

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
               //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
               // SLO, SRE, RLA, RRA, ISB, DCP

               //fetch low byte of address, increment PC
               addr_low = read_memory(registers.getPC());
               registers.incrementPC();

               // fetch high byte of address, increment PC
               addr_high = read_memory(registers.getPC());
               registers.incrementPC();

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
                //TODO: Add illegal instructions to the switch-case when we want to support illegal instructions:
                // SAX

                // fetch low byte of address, increment PC
                addr_low = read_memory(registers.getPC());
                registers.incrementPC();

                // fetch high byte of address, increment PC
                addr_high = read_memory(registers.getPC());
                registers.incrementPC();

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
                byte operand = read_memory(registers.getPC());
                registers.incrementPC();

                short addr_tmp = (short) (registers.getPC() + operand);
                byte addr_tmp_high = (byte) ((addr_tmp >> 8) & 0xFF);
                if (
                        (instr == Instructions.BMI && registers.getP().getNegative()     == true)    ||
                                (instr == Instructions.BPL && registers.getP().getNegative()     == false)   ||
                                (instr == Instructions.BNE && registers.getP().getZero()         == false)   ||
                                (instr == Instructions.BVC && registers.getP().getOverflow()     == false)   ||
                                (instr == Instructions.BVS && registers.getP().getOverflow()     == true)    ||
                                (instr == Instructions.BEQ && registers.getP().getZero()         == true)    ||
                                (instr == Instructions.BCS && registers.getP().getCarry()        == true)    ||
                                (instr == Instructions.BCC && registers.getP().getCarry()        == false)) {
                    // Branch taken

                    read_memory(registers.getPC()); // dummy read

                    // Set low 8-bits of PC to low 8-bits of addr_tmp
                    registers.setPC((short) ((registers.getPC() & 0xFF00) | (addr_tmp & 0xFF)));
                    if (addr_tmp_high != (byte) (registers.getPC() >> 8)) {
                        // Page boundary crossed
                        read_memory(registers.getPC()); // dummy read
                        registers.setPC(addr_tmp);
                    }
//                    read_memory(registers.getPC()); // dummy read
//                    registers.incrementPC();
                } else {
                    // Branch is not taken
                    //registers.incrementPC();
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
//                byte relative_addr = read_memory(registers.getPC());
//                registers.incrementPC();
//
//                // add relative address to PC
//                fetched_addr = (short) (registers.getPC() + relative_addr);
//                break;
//            default:
//                throw new RuntimeException("Instruction not implemented: " + instr);
        }
    }

    private void write_memory(short addr, byte value) {
//        logger.debug("Writing memory: ["+addr + " (" + Common.shortToHex(addr, true)+")] = "
//                +value + " ("+Common.byteToHex(value, true)+")");
        memory[addr & 0xFFFF] = value;
        cycles ++;
        if (is_record_memory)
            recorded_memory.add(new MemoryAccessRecord(addr, value, false));
    }

    private void push_stack(byte data) {
        // TODO: Expected to write at addres: 498 (0x1F2) but instead it writes at 242 (0xF2)
        write_memory(Common.makeShort(registers.getS(), (byte) 0x01), data);
        registers.setS((byte) (registers.getS() - 1));
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
    private void exec_cmp(AddressingMode addrmode, byte register) {
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

        registers.getP().setNegative(new_n);
        registers.getP().setZero(new_z);
        registers.getP().setCarry(new_c); // TODO: Expected true
    }

    private void nmi_interrupt() {
        logger.debug("NMI interrupt called");
        // Store current flags onto stack and when returning, restore them.
        push_pc((short) 0);
        byte p_flag = registers.getP().getAllFlags();
        // Read about B flag: https://www.nesdev.org/wiki/Status_flags#The_B_flag
        p_flag = Common.Bits.setBit(p_flag, 4, false);
        push_stack(p_flag);

        // Load interrupt vector and jump to address.
        byte vector_lsb = read_memory((short) 0xFFFA);
        byte vector_msb = read_memory((short) 0xFFFB);
        short new_pc = Common.makeShort(vector_lsb, vector_msb);
        logger.debug("Jumping to interrupt address: " + Common.shortToHex(new_pc, true));
        registers.setPC(new_pc);
    }

    /**
     * Only use in tests. Starts to record memory reads and writes.
     * @param is_record
     */
    public void set_debugger_record_memory(boolean is_record) {
        if (is_record && this.recorded_memory == null)
            this.recorded_memory = new ArrayList<>();
        this.is_record_memory = is_record;
    }

    public List<MemoryAccessRecord> get_debugger_memory_records() {
        return this.recorded_memory;
    }

    public void clear_debugger_memory_records() {
        this.recorded_memory.clear();
    }

    public record MemoryAccessRecord(short addr, byte value, boolean is_read) {
        @Override
        public String toString() {
            String read_write = is_read ? "read" : "write";
            return "["+(addr & 0xFFFF)+","+(value & 0xFF)+","+read_write+"]";
        }
    }

    private void exec_lda() {
        registers.setA(fetched_data);
        registers.getP().setNegative(Common.Bits.getBit(fetched_data, 7));
        registers.getP().setZero(fetched_data == 0);
    }

    private void exec_adc() {
        byte result = (byte) (registers.getA() + fetched_data + (registers.getP().getCarry() ? 1 : 0));

        byte m_plus_a = (byte) (fetched_data + registers.getA());
        boolean is_carry = Common.isAdditionCarry(fetched_data, registers.getA());
        boolean is_carry2 = Common.isAdditionCarry(m_plus_a, (byte) (registers.getP().getCarry() ? 1 : 0));
        boolean negative_flag_set = ((registers.getA() ^ result) & (fetched_data ^ result) & 0x80) != 0;

        registers.getP().modify_n(result);
        registers.getP().modify_z(result);
        registers.getP().setCarry(is_carry || is_carry2);
        registers.getP().setOverflow(negative_flag_set);
        registers.setA(result);
    }

    private void exec_tax() {
        registers.setX(registers.getA());
        registers.getP().setZero(registers.getX() == 0);
        registers.getP().setNegative(Common.Bits.getBit(registers.getX(), 7));
    }

    private void exec_tay() {
        registers.setY(registers.getA());
        registers.getP().setZero(registers.getY() == 0);
        registers.getP().setNegative(Common.Bits.getBit(registers.getY(), 7));
    }

    private void exec_lsr(boolean is_accumulator) {
        if (is_accumulator)
            fetched_data = registers.getA();

        // In addressing mode we already fetched it. So we don't need to read again.
//        else
//            fetched_data = read_memory(fetched_addr);

        boolean is_carry = Common.Bits.getBit(fetched_data, 0);
        byte result = (byte) ((fetched_data & 0xFF) >> 1);
        registers.getP().setCarry(is_carry);
        registers.getP().setZero(result == 0);
        registers.getP().setNegative(Common.Bits.getBit(result, 7));

        if (is_accumulator)
            registers.setA(result);
        else
            write_memory(fetched_addr, result);
    }
}
