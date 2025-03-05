package NES.CPU.Decoder;

import NES.CPU.AddressingMode;
import NES.CPU.Instructions;

public class Decoder {

    public enum OopsCycle {
        NONE,
        PageBoundaryCrossed,
        BranchOccursOn
    }

    public static final InstructionInfo[] instructions_table = init_instructions_table();

    private static InstructionInfo[] init_instructions_table() {
        if (instructions_table != null)
            return instructions_table;

        InstructionInfo[] abc = new InstructionInfo[256];
        abc[0x00] = new InstructionInfo((byte)0x00, Instructions.BRK, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x01] = new InstructionInfo((byte)0x01, Instructions.ORA, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
//        abc[0x02] = new InstructionInfo((byte)0x02, Instructions.JAM, AddressingMode.IMPLIED, 0, 0, OopsCycle.NONE); // illegal. TODO: Unknown amount of bytes, addressing mode, cycles, oops cycle
        abc[0x03] = new InstructionInfo((byte)0x03, Instructions.SLO, AddressingMode.INDIRECT_X, 2, 8, OopsCycle.NONE, true); // illegal
        abc[0x04] = new InstructionInfo((byte)0x04, Instructions.NOP, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE, true); // illegal
        abc[0x05] = new InstructionInfo((byte)0x05, Instructions.ORA, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x06] = new InstructionInfo((byte)0x06, Instructions.ASL, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0x08] = new InstructionInfo((byte)0x08, Instructions.PHP, AddressingMode.IMPLIED, 1, 3, OopsCycle.NONE);
        abc[0x09] = new InstructionInfo((byte)0x09, Instructions.ORA, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0x0A] = new InstructionInfo((byte)0x0A, Instructions.ASL, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
        abc[0x0C] = new InstructionInfo((byte)0x0C, Instructions.NOP, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE, true); // illegal
        abc[0x0D] = new InstructionInfo((byte)0x0D, Instructions.ORA, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x0E] = new InstructionInfo((byte)0x0E, Instructions.ASL, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);

        abc[0x10] = new InstructionInfo((byte)0x10, Instructions.BPL, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0x11] = new InstructionInfo((byte)0x11, Instructions.ORA, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
        abc[0x14] = new InstructionInfo((byte)0x14, Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE, true); // illegal
        abc[0x15] = new InstructionInfo((byte)0x15, Instructions.ORA, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x16] = new InstructionInfo((byte)0x16, Instructions.ASL, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0x18] = new InstructionInfo((byte)0x18, Instructions.CLC, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x19] = new InstructionInfo((byte)0x19, Instructions.ORA, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x1A] = new InstructionInfo((byte)0x1A, Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE, true); // illegal
        abc[0x1C] = new InstructionInfo((byte)0x1C, Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed, true); // illegal
        abc[0x1D] = new InstructionInfo((byte)0x1D, Instructions.ORA, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x1E] = new InstructionInfo((byte)0x1E, Instructions.ASL, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);

        abc[0x20] = new InstructionInfo((byte)0x20, Instructions.JSR, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
        abc[0x21] = new InstructionInfo((byte)0x21, Instructions.AND, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0x24] = new InstructionInfo((byte)0x24, Instructions.BIT, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x25] = new InstructionInfo((byte)0x25, Instructions.AND, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x26] = new InstructionInfo((byte)0x26, Instructions.ROL, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0x28] = new InstructionInfo((byte)0x28, Instructions.PLP, AddressingMode.IMPLIED, 1, 4, OopsCycle.NONE);
        abc[0x29] = new InstructionInfo((byte)0x29, Instructions.AND, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0x2A] = new InstructionInfo((byte)0x2A, Instructions.ROL, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
        abc[0x2C] = new InstructionInfo((byte)0x2C, Instructions.BIT, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x2D] = new InstructionInfo((byte)0x2D, Instructions.AND, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x2E] = new InstructionInfo((byte)0x2E, Instructions.ROL, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);

        abc[0x30] = new InstructionInfo((byte)0x30, Instructions.BMI, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0x31] = new InstructionInfo((byte)0x31, Instructions.AND, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
        abc[0x32] = new InstructionInfo((byte)0x32, Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE, true); // illegal
        abc[0x3A] = new InstructionInfo((byte)0x3A, Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE, true); // illegal
        abc[0x34] = new InstructionInfo((byte)0x34, Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE, true); // illegal
        abc[0x35] = new InstructionInfo((byte)0x35, Instructions.AND, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x36] = new InstructionInfo((byte)0x36, Instructions.ROL, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0x38] = new InstructionInfo((byte)0x38, Instructions.SEC, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x39] = new InstructionInfo((byte)0x39, Instructions.AND, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x3C] = new InstructionInfo((byte)0x3C, Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed, true); // illegal
        abc[0x3D] = new InstructionInfo((byte)0x3D, Instructions.AND, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x3E] = new InstructionInfo((byte)0x3E, Instructions.ROL, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
        abc[0x3F] = new InstructionInfo((byte)0x3F, Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE, true); // illegal

        abc[0x40] = new InstructionInfo((byte)0x40, Instructions.RTI, AddressingMode.IMPLIED, 1, 6, OopsCycle.NONE);
        abc[0x41] = new InstructionInfo((byte)0x41, Instructions.EOR, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0x44] = new InstructionInfo((byte)0x44, Instructions.NOP, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE, true); // illegal
        abc[0x45] = new InstructionInfo((byte)0x45, Instructions.EOR, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x46] = new InstructionInfo((byte)0x46, Instructions.LSR, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0x48] = new InstructionInfo((byte)0x48, Instructions.PHA, AddressingMode.IMPLIED, 1, 3, OopsCycle.NONE);
        abc[0x49] = new InstructionInfo((byte)0x49, Instructions.EOR, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0x4A] = new InstructionInfo((byte)0x4A, Instructions.LSR, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
        abc[0x4C] = new InstructionInfo((byte)0x4C, Instructions.JMP, AddressingMode.ABSOLUTE, 3, 3, OopsCycle.NONE);
        abc[0x4D] = new InstructionInfo((byte)0x4D, Instructions.EOR, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x4E] = new InstructionInfo((byte)0x4E, Instructions.LSR, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);

        abc[0x50] = new InstructionInfo((byte)0x50, Instructions.BVC, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0x51] = new InstructionInfo((byte)0x51, Instructions.EOR, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
        abc[0x54] = new InstructionInfo((byte)0x54, Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE, true); // illegal
        abc[0x55] = new InstructionInfo((byte)0x55, Instructions.EOR, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x56] = new InstructionInfo((byte)0x56, Instructions.LSR, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0x58] = new InstructionInfo((byte)0x58, Instructions.CLI, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x59] = new InstructionInfo((byte)0x59, Instructions.EOR, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x5A] = new InstructionInfo((byte)0x5A, Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE, true); // illegal
        abc[0x5C] = new InstructionInfo((byte)0x5C, Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed, true); // illegal
        abc[0x5D] = new InstructionInfo((byte)0x5D, Instructions.EOR, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x5E] = new InstructionInfo((byte)0x5E, Instructions.LSR, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);

        abc[0x60] = new InstructionInfo((byte)0x60, Instructions.RTS, AddressingMode.IMPLIED, 1, 6, OopsCycle.NONE);
        abc[0x61] = new InstructionInfo((byte)0x61, Instructions.ADC, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0x64] = new InstructionInfo((byte)0x64, Instructions.NOP, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE, true); // illegal
        abc[0x65] = new InstructionInfo((byte)0x65, Instructions.ADC, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x66] = new InstructionInfo((byte)0x66, Instructions.ROR, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0x68] = new InstructionInfo((byte)0x68, Instructions.PLA, AddressingMode.IMPLIED, 1, 4, OopsCycle.NONE);
        abc[0x69] = new InstructionInfo((byte)0x69, Instructions.ADC, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0x6A] = new InstructionInfo((byte)0x6A, Instructions.ROR, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
        abc[0x6C] = new InstructionInfo((byte)0x6C, Instructions.JMP, AddressingMode.ABSOLUTE_INDIRECT, 3, 5, OopsCycle.NONE);
        abc[0x6D] = new InstructionInfo((byte)0x6D, Instructions.ADC, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x6E] = new InstructionInfo((byte)0x6E, Instructions.ROR, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);

        abc[0x70] = new InstructionInfo((byte)0x70, Instructions.BVS, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0x71] = new InstructionInfo((byte)0x71, Instructions.ADC, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.NONE);
        abc[0x74] = new InstructionInfo((byte)0x74, Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE, true); // illegal
        abc[0x75] = new InstructionInfo((byte)0x75, Instructions.ADC, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x76] = new InstructionInfo((byte)0x76, Instructions.ROR, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0x78] = new InstructionInfo((byte)0x78, Instructions.SEI, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x79] = new InstructionInfo((byte)0x79, Instructions.ADC, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.NONE);
        abc[0x7A] = new InstructionInfo((byte)0x7A, Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE, true); // illegal
        abc[0x7B] = new InstructionInfo((byte)0x7B, Instructions.RRA, AddressingMode.ABSOLUTE_Y, 3, 7, OopsCycle.NONE, true); // illegal
        abc[0x7C] = new InstructionInfo((byte)0x7C, Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.NONE, true); // illegal
        abc[0x7D] = new InstructionInfo((byte)0x7D, Instructions.ADC, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.NONE);
        abc[0x7E] = new InstructionInfo((byte)0x7E, Instructions.ROR, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
        abc[0x7F] = new InstructionInfo((byte)0x7F, Instructions.RRA, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE, true); // illegal

        abc[0x80] = new InstructionInfo((byte)0x80, Instructions.NOP, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE, true); // illegal
        abc[0x81] = new InstructionInfo((byte)0x81, Instructions.STA, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0x83] = new InstructionInfo((byte)0x83, Instructions.SAX, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE, true); // illegal
        abc[0x84] = new InstructionInfo((byte)0x84, Instructions.STY, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x85] = new InstructionInfo((byte)0x85, Instructions.STA, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x86] = new InstructionInfo((byte)0x86, Instructions.STX, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x87] = new InstructionInfo((byte)0x87, Instructions.SAX, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE, true); // illegal
        abc[0x88] = new InstructionInfo((byte)0x88, Instructions.DEY, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x89] = new InstructionInfo((byte)0x89, Instructions.NOP, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE, true); // illegal
        abc[0x8A] = new InstructionInfo((byte)0x8A, Instructions.TXA, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x8C] = new InstructionInfo((byte)0x8C, Instructions.STY, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x8D] = new InstructionInfo((byte)0x8D, Instructions.STA, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x8E] = new InstructionInfo((byte)0x8E, Instructions.STX, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x8F] = new InstructionInfo((byte)0x8F, Instructions.SAX, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE, true); // illegal

        abc[0x90] = new InstructionInfo((byte)0x90, Instructions.BCC, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0x91] = new InstructionInfo((byte)0x91, Instructions.STA, AddressingMode.INDIRECT_Y, 2, 6, OopsCycle.NONE);
        abc[0x92] = new InstructionInfo((byte)0x92, Instructions.JAM, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE, true); // illegal
        abc[0x94] = new InstructionInfo((byte)0x94, Instructions.STY, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x95] = new InstructionInfo((byte)0x95, Instructions.STA, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x96] = new InstructionInfo((byte)0x96, Instructions.STX, AddressingMode.ZEROPAGE_Y, 2, 4, OopsCycle.NONE);
        abc[0x97] = new InstructionInfo((byte)0x97, Instructions.SAX, AddressingMode.ZEROPAGE_Y, 2, 4, OopsCycle.NONE, true); // illegal
        abc[0x98] = new InstructionInfo((byte)0x98, Instructions.TYA, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x99] = new InstructionInfo((byte)0x99, Instructions.STA, AddressingMode.ABSOLUTE_Y, 3, 5, OopsCycle.NONE);
        abc[0x9A] = new InstructionInfo((byte)0x9A, Instructions.TXS, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x9D] = new InstructionInfo((byte)0x9D, Instructions.STA, AddressingMode.ABSOLUTE_X, 3, 5, OopsCycle.NONE);

        abc[0xA0] = new InstructionInfo((byte)0xA0, Instructions.LDY, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xA1] = new InstructionInfo((byte)0xA1, Instructions.LDA, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0xA2] = new InstructionInfo((byte)0xA2, Instructions.LDX, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xA3] = new InstructionInfo((byte)0xA3, Instructions.LAX, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE, true); // illegal
        abc[0xA4] = new InstructionInfo((byte)0xA4, Instructions.LDY, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0xA5] = new InstructionInfo((byte)0xA5, Instructions.LDA, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0xA6] = new InstructionInfo((byte)0xA6, Instructions.LDX, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0xA7] = new InstructionInfo((byte)0xA7, Instructions.LAX, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE, true); // illegal
        abc[0xA8] = new InstructionInfo((byte)0xA8, Instructions.TAY, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xA9] = new InstructionInfo((byte)0xA9, Instructions.LDA, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xAA] = new InstructionInfo((byte)0xAA, Instructions.TAX, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xAC] = new InstructionInfo((byte)0xAC, Instructions.LDY, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xAD] = new InstructionInfo((byte)0xAD, Instructions.LDA, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xAE] = new InstructionInfo((byte)0xAE, Instructions.LDX, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xAF] = new InstructionInfo((byte)0xAF, Instructions.LAX, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE, true); // illegal

        abc[0xB0] = new InstructionInfo((byte)0xB0, Instructions.BCS, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0xB1] = new InstructionInfo((byte)0xB1, Instructions.LDA, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
        abc[0xB3] = new InstructionInfo((byte)0xB3, Instructions.LAX, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed, true); // illegal
        abc[0xB4] = new InstructionInfo((byte)0xB4, Instructions.LDY, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0xB5] = new InstructionInfo((byte)0xB5, Instructions.LDA, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0xB6] = new InstructionInfo((byte)0xB6, Instructions.LDX, AddressingMode.ZEROPAGE_Y, 2, 4, OopsCycle.NONE);
        abc[0xB7] = new InstructionInfo((byte)0xB7, Instructions.LAX, AddressingMode.ZEROPAGE_Y, 2, 4, OopsCycle.NONE, true); // illegal
        abc[0xB8] = new InstructionInfo((byte)0xB8, Instructions.CLV, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xB9] = new InstructionInfo((byte)0xB9, Instructions.LDA, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xBA] = new InstructionInfo((byte)0xBA, Instructions.TSX, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xBC] = new InstructionInfo((byte)0xBC, Instructions.LDY, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xBD] = new InstructionInfo((byte)0xBD, Instructions.LDA, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xBE] = new InstructionInfo((byte)0xBE, Instructions.LDX, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xBF] = new InstructionInfo((byte)0xBF, Instructions.LAX, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed, true); // illegal

        abc[0xC0] = new InstructionInfo((byte)0xC0, Instructions.CPY, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xC1] = new InstructionInfo((byte)0xC1, Instructions.CMP, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0xC4] = new InstructionInfo((byte)0xC4, Instructions.CPY, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0xC5] = new InstructionInfo((byte)0xC5, Instructions.CMP, AddressingMode.ZEROPAGE, 2, 2, OopsCycle.NONE);
        abc[0xC6] = new InstructionInfo((byte)0xC6, Instructions.DEC, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0xC7] = new InstructionInfo((byte)0xC7, Instructions.DCP, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE, true); // illegal
        abc[0xC8] = new InstructionInfo((byte)0xC8, Instructions.INY, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xC9] = new InstructionInfo((byte)0xC9, Instructions.CMP, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xCA] = new InstructionInfo((byte)0xCA, Instructions.DEX, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xC3] = new InstructionInfo((byte)0xC3, Instructions.DCP, AddressingMode.INDIRECT_X, 2, 8, OopsCycle.NONE, true); // illegal
        abc[0xCC] = new InstructionInfo((byte)0xCC, Instructions.CPY, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xCD] = new InstructionInfo((byte)0xCD, Instructions.CMP, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xCE] = new InstructionInfo((byte)0xCE, Instructions.DEC, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
        abc[0xCF] = new InstructionInfo((byte)0xCF, Instructions.DCP, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE, true); // illegal

        abc[0xD0] = new InstructionInfo((byte)0xD0, Instructions.BNE, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0xD1] = new InstructionInfo((byte)0xD1, Instructions.CMP, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
        abc[0xD3] = new InstructionInfo((byte)0xD3, Instructions.DCP, AddressingMode.INDIRECT_Y, 2, 8, OopsCycle.NONE, true); // illegal
        abc[0xD4] = new InstructionInfo((byte)0xD4, Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE, true); // illegal
        abc[0xD5] = new InstructionInfo((byte)0xD5, Instructions.CMP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0xD6] = new InstructionInfo((byte)0xD6, Instructions.DEC, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0xD7] = new InstructionInfo((byte)0xD7, Instructions.DCP, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE, true); // illegal
        abc[0xD8] = new InstructionInfo((byte)0xD8, Instructions.CLD, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xD9] = new InstructionInfo((byte)0xD9, Instructions.CMP, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xDA] = new InstructionInfo((byte)0xDA, Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE, true); // illegal
        abc[0xDB] = new InstructionInfo((byte)0xDB, Instructions.DCP, AddressingMode.ABSOLUTE_Y, 3, 7, OopsCycle.NONE, true); // illegal
        abc[0xDC] = new InstructionInfo((byte)0xDC, Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed, true); // illegal
        abc[0xDD] = new InstructionInfo((byte)0xDD, Instructions.CMP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xDE] = new InstructionInfo((byte)0xDE, Instructions.DEC, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
        abc[0xDF] = new InstructionInfo((byte)0xDF, Instructions.DCP, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE, true); // illegal

        abc[0xE0] = new InstructionInfo((byte)0xE0, Instructions.CPX, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xE1] = new InstructionInfo((byte)0xE1, Instructions.SBC, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0xE3] = new InstructionInfo((byte)0xE3, Instructions.ISB, AddressingMode.INDIRECT_X, 2, 8, OopsCycle.NONE, true); // illegal
        abc[0xE4] = new InstructionInfo((byte)0xE4, Instructions.CPX, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0xE5] = new InstructionInfo((byte)0xE5, Instructions.SBC, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0xE6] = new InstructionInfo((byte)0xE6, Instructions.INC, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0xE7] = new InstructionInfo((byte)0xE7, Instructions.ISB, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE, true); // illegal
        abc[0xE8] = new InstructionInfo((byte)0xE8, Instructions.INX, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xE9] = new InstructionInfo((byte)0xE9, Instructions.SBC, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xEA] = new InstructionInfo((byte)0xEA, Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xEB] = new InstructionInfo((byte)0xEB, Instructions.SBC, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE, true); // illegal
        abc[0xEC] = new InstructionInfo((byte)0xEC, Instructions.CPX, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xED] = new InstructionInfo((byte)0xED, Instructions.SBC, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xEE] = new InstructionInfo((byte)0xEE, Instructions.INC, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
        abc[0xEF] = new InstructionInfo((byte)0xEF, Instructions.ISB, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE, true); // illegal

        abc[0xF0] = new InstructionInfo((byte)0xF0, Instructions.BEQ, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0xF1] = new InstructionInfo((byte)0xF1, Instructions.SBC, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
        abc[0xF3] = new InstructionInfo((byte)0xF3, Instructions.ISB, AddressingMode.INDIRECT_Y, 2, 8, OopsCycle.NONE, true); // illegal
        abc[0xF4] = new InstructionInfo((byte)0xF4, Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE, true); // illegal
        abc[0xF5] = new InstructionInfo((byte)0xF5, Instructions.SBC, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0xF6] = new InstructionInfo((byte)0xF6, Instructions.INC, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0xF7] = new InstructionInfo((byte)0xF7, Instructions.ISB, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE, true); // illegal
        abc[0xF8] = new InstructionInfo((byte)0xF8, Instructions.SED, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xF9] = new InstructionInfo((byte)0xF9, Instructions.SBC, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xFA] = new InstructionInfo((byte)0xFA, Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE); // illegal
        abc[0xFB] = new InstructionInfo((byte)0xFB, Instructions.ISB, AddressingMode.ABSOLUTE_Y, 3, 7, OopsCycle.NONE, true); // illegal
        abc[0xFC] = new InstructionInfo((byte)0xFC, Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed, true); // illegal
        abc[0xFD] = new InstructionInfo((byte)0xFD, Instructions.SBC, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xFE] = new InstructionInfo((byte)0xFE, Instructions.INC, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
        abc[0xFF] = new InstructionInfo((byte)0xFF, Instructions.ISB, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE, true); // illegal

        return abc;
    }

    public static InstructionInfo decode_opcode(byte opcode) {
        return instructions_table[opcode & 0xFF];
    }

}
