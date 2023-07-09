package NES.CPU.Registers;

public enum Flags {
    CARRY(0),
    ZERO(1),
    INTERRUPT(2),
    DECIMAL(3),
    BREAK(4),
    UNUSED(5),
    OVERFLOW(6),
    NEGATIVE(7);

    private final int bit;

    Flags(int bit) {
        this.bit = bit;
    }

    public int getBit() {
        return bit;
    }
}
