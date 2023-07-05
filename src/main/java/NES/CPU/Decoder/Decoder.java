package NES.CPU.Decoder;

import NES.CPU.AddressingMode;
import NES.CPU.Instructions;
import NES.Common;
import NES.UI.Debugger.AssemblyDebugger.AssemblyLineRecord;

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
        abc[0x00] = new InstructionInfo(Instructions.BRK, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x01] = new InstructionInfo(Instructions.ORA, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0x04] = new InstructionInfo(Instructions.NOP, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE); // illegal
        abc[0x05] = new InstructionInfo(Instructions.ORA, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x06] = new InstructionInfo(Instructions.ASL, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0x08] = new InstructionInfo(Instructions.PHP, AddressingMode.IMPLIED, 1, 3, OopsCycle.NONE);
        abc[0x09] = new InstructionInfo(Instructions.ORA, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0x0A] = new InstructionInfo(Instructions.ASL, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
        abc[0x0C] = new InstructionInfo(Instructions.NOP, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE); // illegal
        abc[0x0D] = new InstructionInfo(Instructions.ORA, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x0E] = new InstructionInfo(Instructions.ASL, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
        abc[0x10] = new InstructionInfo(Instructions.BPL, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0x11] = new InstructionInfo(Instructions.ORA, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
        abc[0x15] = new InstructionInfo(Instructions.ORA, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x14] = new InstructionInfo(Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE); // illegal
        abc[0x16] = new InstructionInfo(Instructions.ASL, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0x18] = new InstructionInfo(Instructions.CLC, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x19] = new InstructionInfo(Instructions.ORA, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x1A] = new InstructionInfo(Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE); // illegal
        abc[0x1C] = new InstructionInfo(Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed); // illegal
        abc[0x1D] = new InstructionInfo(Instructions.ORA, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x1E] = new InstructionInfo(Instructions.ASL, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
        abc[0x20] = new InstructionInfo(Instructions.JSR, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
        abc[0x21] = new InstructionInfo(Instructions.AND, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0x24] = new InstructionInfo(Instructions.BIT, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x25] = new InstructionInfo(Instructions.AND, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x26] = new InstructionInfo(Instructions.ROL, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0x28] = new InstructionInfo(Instructions.PLP, AddressingMode.IMPLIED, 1, 4, OopsCycle.NONE);
        abc[0x29] = new InstructionInfo(Instructions.AND, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0x2A] = new InstructionInfo(Instructions.ROL, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
        abc[0x2C] = new InstructionInfo(Instructions.BIT, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x2D] = new InstructionInfo(Instructions.AND, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x2E] = new InstructionInfo(Instructions.ROL, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
        abc[0x30] = new InstructionInfo(Instructions.BMI, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0x31] = new InstructionInfo(Instructions.AND, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
        abc[0x3A] = new InstructionInfo(Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE); // illegal
        abc[0x34] = new InstructionInfo(Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE); // illegal
        abc[0x35] = new InstructionInfo(Instructions.AND, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x36] = new InstructionInfo(Instructions.ROL, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0x38] = new InstructionInfo(Instructions.SEC, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x39] = new InstructionInfo(Instructions.AND, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x3C] = new InstructionInfo(Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed); // illegal
        abc[0x3D] = new InstructionInfo(Instructions.AND, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x3E] = new InstructionInfo(Instructions.ROL, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
        abc[0x40] = new InstructionInfo(Instructions.RTI, AddressingMode.IMPLIED, 1, 6, OopsCycle.NONE);
        abc[0x41] = new InstructionInfo(Instructions.EOR, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0x44] = new InstructionInfo(Instructions.NOP, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE); // illegal
        abc[0x45] = new InstructionInfo(Instructions.EOR, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x46] = new InstructionInfo(Instructions.LSR, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0x48] = new InstructionInfo(Instructions.PHA, AddressingMode.IMPLIED, 1, 3, OopsCycle.NONE);
        abc[0x49] = new InstructionInfo(Instructions.EOR, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0x4A] = new InstructionInfo(Instructions.LSR, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
        abc[0x4C] = new InstructionInfo(Instructions.JMP, AddressingMode.ABSOLUTE, 3, 3, OopsCycle.NONE);
        abc[0x4D] = new InstructionInfo(Instructions.EOR, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x4E] = new InstructionInfo(Instructions.LSR, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
        abc[0x50] = new InstructionInfo(Instructions.BVC, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0x51] = new InstructionInfo(Instructions.EOR, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
        abc[0x54] = new InstructionInfo(Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE); // illegal
        abc[0x55] = new InstructionInfo(Instructions.EOR, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x56] = new InstructionInfo(Instructions.LSR, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0x58] = new InstructionInfo(Instructions.CLI, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x59] = new InstructionInfo(Instructions.EOR, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x5A] = new InstructionInfo(Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE); // illegal
        abc[0x5C] = new InstructionInfo(Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed); // illegal
        abc[0x5D] = new InstructionInfo(Instructions.EOR, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0x5E] = new InstructionInfo(Instructions.LSR, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
        abc[0x60] = new InstructionInfo(Instructions.RTS, AddressingMode.IMPLIED, 1, 6, OopsCycle.NONE);
        abc[0x61] = new InstructionInfo(Instructions.ADC, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0x64] = new InstructionInfo(Instructions.NOP, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE); // illegal
        abc[0x65] = new InstructionInfo(Instructions.ADC, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x66] = new InstructionInfo(Instructions.ROR, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0x68] = new InstructionInfo(Instructions.PLA, AddressingMode.IMPLIED, 1, 4, OopsCycle.NONE);
        abc[0x69] = new InstructionInfo(Instructions.ADC, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0x6A] = new InstructionInfo(Instructions.ROR, AddressingMode.ACCUMULATOR, 1, 2, OopsCycle.NONE);
        abc[0x6C] = new InstructionInfo(Instructions.JMP, AddressingMode.ABSOLUTE_INDIRECT, 3, 5, OopsCycle.NONE);
        abc[0x6D] = new InstructionInfo(Instructions.ADC, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x6E] = new InstructionInfo(Instructions.ROR, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
        abc[0x70] = new InstructionInfo(Instructions.BVS, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0x71] = new InstructionInfo(Instructions.ADC, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.NONE);
        abc[0x74] = new InstructionInfo(Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE); // illegal
        abc[0x75] = new InstructionInfo(Instructions.ADC, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x76] = new InstructionInfo(Instructions.ROR, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0x78] = new InstructionInfo(Instructions.SEI, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x79] = new InstructionInfo(Instructions.ADC, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.NONE);
        abc[0x7A] = new InstructionInfo(Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE); // illegal
        abc[0x7C] = new InstructionInfo(Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.NONE); // illegal
        abc[0x7D] = new InstructionInfo(Instructions.ADC, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.NONE);
        abc[0x7E] = new InstructionInfo(Instructions.ROR, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
        abc[0x80] = new InstructionInfo(Instructions.NOP, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE); // illegal
        abc[0x81] = new InstructionInfo(Instructions.STA, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0x83] = new InstructionInfo(Instructions.SAX, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE); // illegal
        abc[0x84] = new InstructionInfo(Instructions.STY, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x85] = new InstructionInfo(Instructions.STA, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x86] = new InstructionInfo(Instructions.STX, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0x87] = new InstructionInfo(Instructions.SAX, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE); // illegal
        abc[0x88] = new InstructionInfo(Instructions.DEY, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x8A] = new InstructionInfo(Instructions.TXA, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x8C] = new InstructionInfo(Instructions.STY, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x8D] = new InstructionInfo(Instructions.STA, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x8E] = new InstructionInfo(Instructions.STX, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0x8F] = new InstructionInfo(Instructions.SAX, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE); // illegal
        abc[0x90] = new InstructionInfo(Instructions.BCC, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0x91] = new InstructionInfo(Instructions.STA, AddressingMode.INDIRECT_Y, 2, 6, OopsCycle.NONE);
        abc[0x94] = new InstructionInfo(Instructions.STY, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x95] = new InstructionInfo(Instructions.STA, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0x96] = new InstructionInfo(Instructions.STX, AddressingMode.ZEROPAGE_Y, 2, 4, OopsCycle.NONE);
        abc[0x97] = new InstructionInfo(Instructions.SAX, AddressingMode.ZEROPAGE_Y, 2, 4, OopsCycle.NONE); // illegal
        abc[0x98] = new InstructionInfo(Instructions.TYA, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x99] = new InstructionInfo(Instructions.STA, AddressingMode.ABSOLUTE_Y, 3, 5, OopsCycle.NONE);
        abc[0x9A] = new InstructionInfo(Instructions.TXS, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0x9D] = new InstructionInfo(Instructions.STA, AddressingMode.ABSOLUTE_X, 3, 5, OopsCycle.NONE);
        abc[0xA0] = new InstructionInfo(Instructions.LDY, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xA1] = new InstructionInfo(Instructions.LDA, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0xA2] = new InstructionInfo(Instructions.LDX, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xA3] = new InstructionInfo(Instructions.LAX, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE); // illegal
        abc[0xA4] = new InstructionInfo(Instructions.LDY, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0xA5] = new InstructionInfo(Instructions.LDA, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0xA6] = new InstructionInfo(Instructions.LDX, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0xA7] = new InstructionInfo(Instructions.LAX, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE); // illegal
        abc[0xA8] = new InstructionInfo(Instructions.TAY, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xA9] = new InstructionInfo(Instructions.LDA, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xAA] = new InstructionInfo(Instructions.TAX, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xAC] = new InstructionInfo(Instructions.LDY, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xAD] = new InstructionInfo(Instructions.LDA, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xAE] = new InstructionInfo(Instructions.LDX, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xAF] = new InstructionInfo(Instructions.LAX, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE); // illegal
        abc[0xB0] = new InstructionInfo(Instructions.BCS, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0xB1] = new InstructionInfo(Instructions.LDA, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
        abc[0xB3] = new InstructionInfo(Instructions.LAX, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed); // illegal
        abc[0xB4] = new InstructionInfo(Instructions.LDY, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0xB5] = new InstructionInfo(Instructions.LDA, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0xB6] = new InstructionInfo(Instructions.LDX, AddressingMode.ZEROPAGE_Y, 2, 4, OopsCycle.NONE);
        abc[0xB7] = new InstructionInfo(Instructions.LAX, AddressingMode.ZEROPAGE_Y, 2, 4, OopsCycle.NONE); // illegal
        abc[0xB8] = new InstructionInfo(Instructions.CLV, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xB9] = new InstructionInfo(Instructions.LDA, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xBA] = new InstructionInfo(Instructions.TSX, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xBC] = new InstructionInfo(Instructions.LDY, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xBD] = new InstructionInfo(Instructions.LDA, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xBE] = new InstructionInfo(Instructions.LDX, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xBF] = new InstructionInfo(Instructions.LAX, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed); // illegal
        abc[0xC0] = new InstructionInfo(Instructions.CPY, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xC1] = new InstructionInfo(Instructions.CMP, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE);
        abc[0xC4] = new InstructionInfo(Instructions.CPY, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0xC5] = new InstructionInfo(Instructions.CMP, AddressingMode.ZEROPAGE, 2, 2, OopsCycle.NONE);
        abc[0xC6] = new InstructionInfo(Instructions.DEC, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0xC7] = new InstructionInfo(Instructions.DCP, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE); // illegal
        abc[0xC8] = new InstructionInfo(Instructions.INY, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xC9] = new InstructionInfo(Instructions.CMP, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xCA] = new InstructionInfo(Instructions.DEX, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xC3] = new InstructionInfo(Instructions.DCP, AddressingMode.INDIRECT_X, 2, 8, OopsCycle.NONE); // illegal
        abc[0xCC] = new InstructionInfo(Instructions.CPY, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xCD] = new InstructionInfo(Instructions.CMP, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xCE] = new InstructionInfo(Instructions.DEC, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
        abc[0xCF] = new InstructionInfo(Instructions.DCP, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE); // illegal
        abc[0xD0] = new InstructionInfo(Instructions.BNE, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0xD1] = new InstructionInfo(Instructions.CMP, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed);
        abc[0xD3] = new InstructionInfo(Instructions.DCP, AddressingMode.INDIRECT_Y, 2, 8, OopsCycle.NONE); // illegal
        abc[0xD4] = new InstructionInfo(Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE); // illegal
        abc[0xD5] = new InstructionInfo(Instructions.CMP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE);
        abc[0xD6] = new InstructionInfo(Instructions.DEC, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0xD7] = new InstructionInfo(Instructions.DCP, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE); // illegal
        abc[0xD8] = new InstructionInfo(Instructions.CLD, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xD9] = new InstructionInfo(Instructions.CMP, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xDA] = new InstructionInfo(Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE); // illegal
        abc[0xDB] = new InstructionInfo(Instructions.DCP, AddressingMode.ABSOLUTE_Y, 3, 7, OopsCycle.NONE); // illegal
        abc[0xDC] = new InstructionInfo(Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed); // illegal
        abc[0xDD] = new InstructionInfo(Instructions.CMP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed);
        abc[0xDE] = new InstructionInfo(Instructions.DEC, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);
        abc[0xDF] = new InstructionInfo(Instructions.DCP, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE); // illegal
        abc[0xE0] = new InstructionInfo(Instructions.CPX, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE);
        abc[0xE1] = new InstructionInfo(Instructions.SBC, AddressingMode.INDIRECT_X, 2, 6, OopsCycle.NONE); // illegal
        abc[0xE4] = new InstructionInfo(Instructions.CPX, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE);
        abc[0xE5] = new InstructionInfo(Instructions.SBC, AddressingMode.ZEROPAGE, 2, 3, OopsCycle.NONE); // illegal
        abc[0xE6] = new InstructionInfo(Instructions.INC, AddressingMode.ZEROPAGE, 2, 5, OopsCycle.NONE);
        abc[0xE8] = new InstructionInfo(Instructions.INX, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xE9] = new InstructionInfo(Instructions.SBC, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE); // illegal
        abc[0xEA] = new InstructionInfo(Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xEB] = new InstructionInfo(Instructions.SBC, AddressingMode.IMMEDIATE, 2, 2, OopsCycle.NONE); // illegal
        abc[0xEC] = new InstructionInfo(Instructions.CPX, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE);
        abc[0xED] = new InstructionInfo(Instructions.SBC, AddressingMode.ABSOLUTE, 3, 4, OopsCycle.NONE); // illegal
        abc[0xEE] = new InstructionInfo(Instructions.INC, AddressingMode.ABSOLUTE, 3, 6, OopsCycle.NONE);
        abc[0xF0] = new InstructionInfo(Instructions.BEQ, AddressingMode.RELATIVE, 2, 2, OopsCycle.BranchOccursOn);
        abc[0xF1] = new InstructionInfo(Instructions.SBC, AddressingMode.INDIRECT_Y, 2, 5, OopsCycle.PageBoundaryCrossed); // illegal
        abc[0xF4] = new InstructionInfo(Instructions.NOP, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE); // illegal
        abc[0xF5] = new InstructionInfo(Instructions.SBC, AddressingMode.ZEROPAGE_X, 2, 4, OopsCycle.NONE); // illegal
        abc[0xF6] = new InstructionInfo(Instructions.INC, AddressingMode.ZEROPAGE_X, 2, 6, OopsCycle.NONE);
        abc[0xF8] = new InstructionInfo(Instructions.SED, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE);
        abc[0xF9] = new InstructionInfo(Instructions.SBC, AddressingMode.ABSOLUTE_Y, 3, 4, OopsCycle.PageBoundaryCrossed); // illegal
        abc[0xFA] = new InstructionInfo(Instructions.NOP, AddressingMode.IMPLIED, 1, 2, OopsCycle.NONE); // illegal
        abc[0xFC] = new InstructionInfo(Instructions.NOP, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed); // illegal
        abc[0xFD] = new InstructionInfo(Instructions.SBC, AddressingMode.ABSOLUTE_X, 3, 4, OopsCycle.PageBoundaryCrossed); // illegal
        abc[0xFE] = new InstructionInfo(Instructions.INC, AddressingMode.ABSOLUTE_X, 3, 7, OopsCycle.NONE);

        return abc;
    }

    public static InstructionInfo decode_opcode(byte opcode) {
        return instructions_table[opcode & 0xFF];
    }

    public static AssemblyLineRecord decode_assembly_line2(short addr, byte[] cpu_memory) {
        byte opcode = cpu_memory[addr & 0xFFFF];
        InstructionInfo info = decode_opcode(opcode);

        int bytes = 1;
        if (info != null)
            bytes = info.bytes;

        Byte operand1 = null;
        Byte operand2 = null;

        if (bytes > 1)
            operand1 = cpu_memory[(addr + 1) & 0xFFFF];
        if (bytes > 2)
            operand2 = cpu_memory[(addr + 2) & 0xFFFF];

        return new AssemblyLineRecord(addr, bytes, opcode, operand1, operand2, info.instr.toString());
    }

    //TODO: Remove this function in favor of AssemblyLineRecord result
    public static AssemblyInfo decode_assembly_line(byte[] cpu_memory, short pc) {
        // Read opcode
        byte opcode = cpu_memory[pc & 0xFFFF];
        String str_opcode = Common.byteToHex(opcode, false);

        // Decode opcode
        InstructionInfo info = decode_opcode(opcode);

        String decoded_operand_or_symbol = null;
        String str_operand1;
        String str_operand2;

        if (info == null) {
            // Undefined
            str_operand1 = "  ";
            str_operand2 = "  ";
        } else if (info.bytes == 1) {
            str_operand1 = "  ";
            str_operand2 = "  ";
        }
        else if (info.bytes == 2) {
            byte operand1 = cpu_memory[pc + 1 & 0xFFFF];
            str_operand1 = Common.byteToHex(operand1, false);
            str_operand2 = "  ";

            decoded_operand_or_symbol = convert_1_operands_to_human_readable_text(info.addrmode, operand1, pc);;
        } else if (info.bytes == 3) {
            byte operand1 = cpu_memory[pc + 1 & 0xFFFF];
            byte operand2 = cpu_memory[pc + 2 & 0xFFFF];
            str_operand1 = Common.byteToHex(operand1, false);
            str_operand2 = Common.byteToHex(operand2, false);

            decoded_operand_or_symbol = convert_2_operands_to_human_readable_text(info.addrmode, operand1, operand2);;
        } else {
            throw new RuntimeException("Unexpected amount of bytes in instruction, must be at most 3");
        }

        String str_instr_bytes = str_opcode + " " + str_operand1 + " " + str_operand2;
        return new AssemblyInfo(info, str_instr_bytes, decoded_operand_or_symbol);
    }

    private static String convert_1_operands_to_human_readable_text(AddressingMode addrmode, byte operand1, short pc) {
        switch (addrmode) {
            case IMMEDIATE -> {
                return "#$" + Common.byteToHex(operand1, false);
            }
            case RELATIVE -> {
                // operand1 is offset
                // We add +2 because the debugger starts after the instruction is completed, i.e.
                // the PC is the next instruction. We want the old instruction
                short relative_addr = (short) (pc + operand1 + 2);
                return "$" + Common.shortToHex(relative_addr, false);
            }
            case ZEROPAGE -> {
                return "$" + Common.byteToHex(operand1, false);
            }
            case ZEROPAGE_X -> {
                return "$" + Common.byteToHex(operand1, false) + ",X";
            }
            case INDIRECT_X -> {
                return "($" + Common.byteToHex(operand1, false) + ",X)";
            }
            case INDIRECT_Y -> {
                return "($" + Common.byteToHex(operand1, false) + "),Y";
            }
            default -> throw new RuntimeException("Not implemented yet");
        }
    }

    private static String convert_2_operands_to_human_readable_text(AddressingMode addrmode, byte operand1, byte operand2) {
        short addr;
        String knownSymbol;
        switch (addrmode) {
            case ABSOLUTE:
                // Little endian = switch order of operands that represent the address
                addr = Common.makeShort(operand2, operand1);
                knownSymbol = convert_addr_to_symbol(addr);
                if (knownSymbol != null) {
                    return knownSymbol;
                }
                else {
                    return "#$" + Common.byteToHex(operand2, false) + Common.byteToHex(operand1, false);
                }
            case ABSOLUTE_X:
                addr = Common.makeShort(operand2, operand1);
                knownSymbol = convert_addr_to_symbol(addr);
                if (knownSymbol != null) {
                    return knownSymbol;
                } else {
                    return "$" + Common.shortToHex(addr, false) + ",X";
                }
            case ABSOLUTE_INDIRECT:
                addr = Common.makeShort(operand2, operand1);
                knownSymbol = convert_addr_to_symbol(addr);
                if (knownSymbol != null) {
                    return knownSymbol;
                } else {
                    return "($" + Common.shortToHex(addr, false) + ")";
                }
            case ABSOLUTE_Y:
                addr = Common.makeShort(operand1, operand2); // switch order
                knownSymbol = convert_addr_to_symbol(addr);
                if (knownSymbol != null) {
                    return knownSymbol;
                } else {
                    return "$" + Common.shortToHex(addr, false) + ",Y";
                }
            default:
                throw new RuntimeException("Not implemented yet");
        }
    }

    public static String convert_addr_to_symbol(short addr) {
        switch (addr) {
            case 0x2000 -> {
                return "PPU_CTRL";
            }
            case 0x2001 -> {
                return "PPU_MASK";
            }
            case 0x2002 -> {
                return "PPU_STATUS";
            }
            case 0x2003 -> {
                return "OAM_ADDRESS";
            }
            case 0x2004 -> {
                return "OAM_DATA";
            }
            case 0x2005 -> {
                return "PPU_SCROLL";
            }
            case 0x2006 -> {
                return "PPU_ADDRRESS";
            }
            case 0x2007 -> {
                return "PPU_DATA";
            }
            case 0x4014 -> {
                return "OAM_DMA";
            }
            default -> {
                return null;
            }
        }
    }
}
