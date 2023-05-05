public class Decoder {
    public enum Instructions {
        ADC, // add with carry
        AND, // and (with accumulator)
        ASL, // arithmetic shift left
        BCC, // branch on carry clear
        BCS, // branch on carry set
        BEQ, // branch on equal (zero set)
        BIT, // bit test
        BMI, // branch on minus (negative set)
        BNE, // branch on not equal (zero clear)
        BPL, // branch on plus (negative clear)
        BRK, // break / interrupt
        BVC, // branch on overflow clear
        BVS, // branch on overflow set
        CLC, // clear carry
        CLD, // clear decimal
        CLI, // clear interrupt disable
        CLV, // clear overflow
        CMP, // compare (with accumulator)
        CPX, // compare with X
        CPY, // compare with Y
        DEC, // decrement
        DEX, // decrement X
        DEY, // decrement Y
        EOR, // exclusive or (with accumulator)
        INC, // increment
        INX, // increment X
        INY, // increment Y
        JMP, // jump
        JSR, // jump subroutine
        LDA, // load accumulator
        LDX, // load X
        LDY, // load Y
        LSR, // logical shift right
        NOP, // no operation
        ORA, // or with accumulator
        PHA, // push accumulator
        PHP, // push processor status (SR)
        PLA, // pull accumulator
        PLP, // pull processor status (SR)
        ROL, // rotate left
        ROR, // rotate right
        RTI, // return from interrupt
        RTS, // return from subroutine
        SBC, // subtract with carry
        SEC, // set carry
        SED, // set decimal
        SEI, // set interrupt disable
        STA, // store accumulator
        STX, // store X
        STY, // store Y
        TAX, // transfer accumulator to X
        TAY, // transfer accumulator to Y
        TSX, // transfer stack pointer to X
        TXA, // transfer X to accumulator
        TXS, // transfer X to stack pointer
        TYA  // transfer Y to accumulator
    }

    public enum AddressingMode {
        IMPLIED,
        ABSOLUTE,
        ABSOLUTE_X,
        ABSOLUTE_Y,
        ZEROPAGE,
        ZEROPAGE_X,
        ZEROPAGE_Y,
        RELATIVE,
        ACCUMULATOR,
        INDIRECT,
        INDIRECT_X,
        INDIRECT_Y,
        IMMEDIATE,
    }

    public enum OopsCycle {
        NONE,
        PageBoundaryCrossed,
        BranchOccursOn
    }

    class InstructionInfo {
        public final Instructions instr;
        public final AddressingMode addrmode;
        public final int bytes, cycles;
        public final OopsCycle oopsCycle;

        public InstructionInfo(Instructions instr, AddressingMode addrmode, int bytes, int cycles, OopsCycle oopsCycle) {
            this.instr = instr;
            this.addrmode = addrmode;
            this.bytes = bytes;
            this.cycles = cycles;
            this.oopsCycle = oopsCycle;
        }
    }

    public InstructionInfo decode_opcode(byte opcode) {
        switch (opcode) {
            case 0x00:
                return new InstructionInfo(Instructions.BRK, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
            case 0x01:
                return new InstructionInfo(Instructions.ORA, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
            case 0x05:
                return new InstructionInfo(Instructions.ORA, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
            case 0x06:
                return new InstructionInfo(Instructions.ASL, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
            case 0x08:
                return new InstructionInfo(Instructions.PHP, AddressingMode.IMPLIED, 1, 3, OopsCycle.NONE);
            case 0x09:
                return new InstructionInfo(Instructions.ORA, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
            case 0x0A:
                return new InstructionInfo(Instructions.ASL, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
            case 0x0D:
                return new InstructionInfo(Instructions.ORA, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
            case 0x0E:
                return new InstructionInfo(Instructions.ASL, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
            case 0x10:
                return new InstructionInfo(Instructions.BPL, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
            case 0x11:
                return new InstructionInfo(Instructions.ORA, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
            case 0x15:
                return new InstructionInfo(Instructions.ORA, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
            case 0x16:
                return new InstructionInfo(Instructions.ASL, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
            case 0x18:
                return new InstructionInfo(Instructions.CLC, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
            case 0x19:
                return new InstructionInfo(Instructions.ORA, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
            case 0x1D:
                return new InstructionInfo(Instructions.ORA, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
            case 0x1E:
                return new InstructionInfo(Instructions.ASL, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
            case 0x20:
                return new InstructionInfo(Instructions.JSR, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
            case 0x21:
                return new InstructionInfo(Instructions.AND, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
            case 0x24:
                return new InstructionInfo(Instructions.BIT, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
            Case 0x25:
            return new InstructionInfo(Instructions.AND, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
            case 0x26:
                return new InstructionInfo(Instructions.ROL, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
            case 0x28:
                return new InstructionInfo(Instructions.PLP, AddressingMode.IMPLIED, 1, 4, OopsCycle.NONE);
            case 0x29:
                return new InstructionInfo(Instructions.AND, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
            case 0x2A:
                return new InstructionInfo(Instructions.ROL, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
            case 0x2C:
                return new InstructionInfo(Instructions.BIT, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
            case 0x2D:
                return new InstructionInfo(Instructions.AND, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
            case 0x2E:
                return new InstructionInfo(Instructions.ROL, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
            case 0x30:
                return new InstructionInfo(Instructions.BMI, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
            case 0x31:
                return new InstructionInfo(Instructions.AND, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
            case 0x35:
                return new InstructionInfo(Instructions.AND, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
            case 0x36:
                return new InstructionInfo(Instructions.ROL, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
            case 0x38:
                return new InstructionInfo(Instructions.SEC, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
            case 0x39:
                return new InstructionInfo(Instructions.AND, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
            case 0x3D:
                return new InstructionInfo(Instructions.AND, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
            case 0x3E:
                return new InstructionInfo(Instructions.ROL, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
            case 0x40:
                return new InstructionInfo(Instructions.RTI, AddressingMode.IMPLIED, 1, 6, OopsCycle.NONE);
            case 0x41:
                return new InstructionInfo(Instructions.EOR, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
            case 0x45:
                return new InstructionInfo(Instructions.EOR, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
            case 0x46:
                return new InstructionInfo(Instructions.LSR, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
            case 0x48:
                return new InstructionInfo(Instructions.PHA, AddressingMode.IMPLIED, 1, 3, OopsCycle.NONE);
            case 0x49:
                return new InstructionInfo(Instructions.EOR, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
            case 0x4A:
                return new InstructionInfo(Instructions.LSR, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
            case 0x4C:
                return new InstructionInfo(Instructions.JMP, AddressingMode.ABSOLUTE, 3, 3, OopsCycle.NONE);
            case 0x4D:
                return new InstructionInfo(Instructions.EOR, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
            case 0x4E:
                return new InstructionInfo(Instructions.LSR, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
            case 0x50:
                return new InstructionInfo(Instructions.BVC, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
            case 0x51:
                return new InstructionInfo(Instructions.EOR, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
            case 0x55:
                return new InstructionInfo(Instructions.EOR, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
            case 0x56:
                return new InstructionInfo(Instructions.LSR, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
            case 0x58:
                return new InstructionInfo(Instructions.CLI, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
            case 0x59:
                return new InstructionInfo(Instructions.EOR, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
            case 0x5D:
                return new InstructionInfo(Instructions.EOR, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
            case 0x5E:
                return new InstructionInfo(Instructions.LSR, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
            case 0x60:
                return new InstructionInfo(Instructions.RTS, AddressingMode.IMPLIED, 1, 6, OopsCycle.NONE);
            case 0x61:
                return new InstructionInfo(Instructions.ADC, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
            case 0x65:
                return new InstructionInfo(Instructions.ADC, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
            case 0x66:
                return new InstructionInfo(Instructions.ROR, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
            case 0x68:
                return new InstructionInfo(Instructions.PLA, AddressingMode.IMPLIED, 1, 4, OopsCycle.NONE);
            case 0x69:
                return new InstructionInfo(Instructions.ADC, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
            case 0x6A:
                return new InstructionInfo(Instructions.ROR, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
            case 0x6C:
                return new InstructionInfo(Instructions.JMP, AddressingMode.INDIRECT, 3, 5, OopsCycle.NONE);
            case 0x6D:
                return new InstructionInfo(Instructions.ADC, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
            case 0x6E:
                return new InstructionInfo(Instructions.ROR, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
            case 0x70:
                return new InstructionInfo(Instructions.BVS, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
            case 0x71:
                return new InstructionInfo(Instructions.ADC, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.NONE);
            case 0x75:
                return new InstructionInfo(Instructions.ADC, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
            case 0x76:
                return new InstructionInfo(Instructions.ROR, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
            case 0x78:
                return new InstructionInfo(Instructions.SEI, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
            case 0x79:
                return new InstructionInfo(Instructions.ADC, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.NONE);
            case 0x7C:
                return new InstructionInfo(Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.NONE);
            case 0x7D:
                return new InstructionInfo(Instructions.ADC, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.NONE);
            case 0x7E:
                return new InstructionInfo(Instructions.ROR, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
            case 0x81:
                return new InstructionInfo(Instructions.STA, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
            case 0x84:
                return new InstructionInfo(Instructions.STY, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
            case 0x85:
                return new InstructionInfo(Instructions.STA, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
            case 0x86:
                return new InstructionInfo(Instructions.STX, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
            case 0x88:
                return new InstructionInfo(Instructions.DEY, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
            case 0x8A:
                return new InstructionInfo(Instructions.TXA, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
            case 0x8C:
                return new InstructionInfo(Instructions.STY, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
            case 0x8D:
                return new InstructionInfo(Instructions.STA, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
            case 0x8E:
                return new InstructionInfo(Instructions.STX, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
            case 0x90:
                return new InstructionInfo(Instructions.BCC, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
            case 0x91:
                return new InstructionInfo(Instructions.STA, AddressingMode.INDIRECT_Y, 2, 6, OopsCycle.NONE);
            case 0x94:
                return new InstructionInfo(Instructions.STY, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
            case 0x94:
                return new InstructionInfo(Instructions.STY, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
            case 0x95:
                return new InstructionInfo(Instructions.STA, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
            case 0x96:
                return new InstructionInfo(Instructions.STX, AddressingMode.ZEROPAGE_Y, 2, 4, OopsCycle.NONE);
            case 0x98:
                return new InstructionInfo(Instructions.TYA, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
            case 0x99:
                return new InstructionInfo(Instructions.STA, AddressingMode.ABSOLUTE_Y, 3, 5, OopsCycle.NONE);
            //TODO: COMPLETE
        }
        throw new RuntimeException("Couldn't decode instruction: " + Common.byteToHexString(opcode));
    }
}
