package NES.CPU;

public enum AddressingMode {
    IMPLIED,
    ABSOLUTE,
    ABSOLUTE_X, // absolute,X
    ABSOLUTE_Y, // absolute,Y
    ZEROPAGE,
    ZEROPAGE_X, // zeropage,X
    ZEROPAGE_Y,
    RELATIVE,
    ACCUMULATOR,
    ABSOLUTE_INDIRECT,
    INDIRECT_X, // (indirect,X)
    INDIRECT_Y, // (indirect),Y
    IMMEDIATE,
}

