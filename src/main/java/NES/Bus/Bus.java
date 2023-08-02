package NES.Bus;

import NES.CPU.CPU;
import NES.PPU.PPU;
import NES.PPU.PPURegisters;

import java.util.List;

public class Bus {

    /**
     * The PPU sets the NMI line to true when Vblank occured.
     * The CPU checks this line before executing each instruction.
     */
    public boolean nmi_line;

    // The parallel port for the controllers.
    public byte[] controllers = new byte[2];

    // The state of the controllers (when the CPU writes to $4016).
    public byte[] controllers_state = new byte[2];

    public PPURegisters ppuRegisters;

    /**
     * If in testing mode, the bus will return the value from the memory array. Not using mappers.
     */
    private final boolean is_testing_mode;

    /**
     * Should the bus record memory accesses?
     */
    private final boolean is_record_memory;

    /**
     * All 64KB addressable memory of CPU address space.
     */
    private final byte[] cpu_memory;

    /**
     * Only used in testing. If record_memory is true, this will contain all the memory reads and writes.
     */
    private List<CPU.MemoryAccessRecord> recorded_memory;

    public Bus(boolean is_testing_mode, boolean is_record_memory, byte[] cpu_memory) {
        this.is_testing_mode = is_testing_mode;
        this.is_record_memory = is_record_memory;
        this.cpu_memory = cpu_memory;
    }

    public Bus(byte[] cpu_memory) {
        this(false, false, cpu_memory);
    }

    public void attachPPU(PPU ppu) {
        this.ppuRegisters = ppu.registers;
    }

    // TODO: Need to fill this up most of my code is inside CPU class but I need mappers (which will be implemented in the Bus class)
    public void cpu_write(short addr, byte data) {
        if (addr == 0x4016 || addr == 0x4017) {
            controllers_state[addr & 1] = controllers[addr & 1];
        }
    }

    public byte cpu_read(short addr) {
        //TODO: Add mapping here. For now I only support mapper 0.

        byte res = 0;

        if (is_testing_mode) {
            res = cpu_memory[addr & 0xFFFF];
        } else {
            // Mirror PPU registers
            // From NESDEV wiki: "they're mirrored in every 8 bytes from $2008 through $3FFF, so a write to $3456 is the same as a write to $2006."
            if (addr >= 0x2000 && addr <= 0x3FFF) {
                addr = (short) (0x2000 + (addr % 8));
            }

            // Check PPU address space
            if (addr >= 0x2000 && addr <= 0x2007) {
                switch (addr) {
                    case 0x2000 -> throw new RuntimeException("Can't read from write-only register: PPUCTRL");
                    case 0x2001 -> throw new RuntimeException("Can't read from write-only register: PPUMASK");
                    case 0x2002 -> res = ppuRegisters.readPPUSTATUS();
                    case 0x2003 -> throw new RuntimeException("Can't read from write-only register: OAMADDR");
                    case 0x2004 -> res = ppuRegisters.readOamData();
                    case 0x2005 -> throw new RuntimeException("Can't read from write-only register: PPUSCROLL");
                    case 0x2006 -> throw new RuntimeException("Can't read from write-only register: PPUADDR");
                    case 0x2007 -> res = ppuRegisters.readPPUDATA();
                }
            } else if (addr == 0x4016 || addr == 0x4017) {
                // Read most significant bit of controller state, serial shift it and return it.
                res = (byte) ((controllers_state[addr & 1] & 0x80) > 0 ? 1 : 0);
                controllers_state[addr & 1] <<= 1;
            } else {
                // Not PPU mapping, read from internal memory
                res = cpu_memory[addr & 0xFFFF];
            }
        }

        if (is_record_memory)
            recorded_memory.add(new CPU.MemoryAccessRecord(addr, res, true));

        return res;
    }
}
