package NES.Bus;

import java.util.List;

public class CPUBus {

    private final Bus bus;

    /**
     * All 64KB addressable memory of CPU address space.
     */
    private final byte[] cpu_memory;

    /**
     * If in testing mode, the bus will return the value from the memory array. Not using mappers.
     */
    private final boolean is_testing_mode;

    /**
     * Should the bus record memory accesses?
     */
    private final boolean is_record_memory;

    /**
     * Only used in testing. If record_memory is true, this will contain all the memory reads and writes.
     */
    public List<MemoryAccessRecord> recorded_memory;

    public CPUBus(Bus bus, boolean is_testing_mode, boolean is_record_memory, byte[] cpu_memory) {
        this.bus = bus;
        this.is_testing_mode = is_testing_mode;
        this.is_record_memory = is_record_memory;
        this.cpu_memory = cpu_memory;
    }


    // TODO: Need to fill this up most of my code is inside CPU class but I need mappers (which will be implemented in the Bus class)
    public void cpu_write(short addr, byte data) {
        //        logger.debug("Writing memory: ["+addr + " (" + Common.shortToHex(addr, true)+")] = "
//                +value + " ("+Common.byteToHex(value, true)+")");

        if (is_testing_mode) {
            cpu_memory[addr & 0xFFFF] = data;
        } else {
            // Mirror PPU registers
            // From NESDEV wiki: "they're mirrored in every 8 bytes from $2008 through $3FFF, so a write to $3456 is the same as a write to $2006."
            if (addr >= 0x2000 && addr <= 0x3FFF) {
                addr = (short) (0x2000 + (addr % 8));
            }

            // Check PPU writes. If so, write to PPU registers and return.
            if ((addr >= 0x2000 && addr <= 0x2007) || addr == 0x4014) {
                switch (addr) {
                    case 0x2000 -> bus.ppuBus.ppuRegisters.writePPUCTRL(data);
                    case 0x2001 -> bus.ppuBus.ppuRegisters.writePPUMASK(data);
                    case 0x2002 -> {} // ignore - read only
                    case 0x2003 -> bus.ppuBus.ppuRegisters.writeOAMADDR(data);
                    case 0x2004 -> bus.ppuBus.ppuRegisters.writeOAMDATA(data);
                    case 0x2005 -> bus.ppuBus.ppuRegisters.writePPUSCROLL(data);
                    case 0x2006 -> bus.ppuBus.ppuRegisters.writePPUADDR(data);
                    case 0x2007 -> bus.ppuBus.ppuRegisters.writePPUDATA(data);
                    case 0x4014 -> bus.ppuBus.ppuRegisters.writeOAMDMA(data);
                }
            }
            // Check controller writes. If so, write to controller registers and return.
            else if (addr == 0x4016 || addr == 0x4017) {
                bus.controllers_state[addr & 1] = bus.controllers[addr & 1];
            } else {
                cpu_memory[addr & 0xFFFF] = data;
            }
        }

        if (is_record_memory)
            recorded_memory.add(new MemoryAccessRecord(addr, data, false));
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
            if (addr >= 0x2000 && addr <= 0x2007 || addr == 0x4014) {
                switch (addr) {
                    case 0x2000 -> throw new RuntimeException("Can't read from write-only register: PPUCTRL");
                    case 0x2001 -> throw new RuntimeException("Can't read from write-only register: PPUMASK");
                    case 0x2002 -> res = bus.ppuBus.ppuRegisters.readPPUSTATUS();
                    case 0x2003 -> throw new RuntimeException("Can't read from write-only register: OAMADDR");
                    case 0x2004 -> res = bus.ppuBus.ppuRegisters.readOamData();
                    case 0x2005 -> throw new RuntimeException("Can't read from write-only register: PPUSCROLL");
                    case 0x2006 -> throw new RuntimeException("Can't read from write-only register: PPUADDR");
                    case 0x2007 -> res = bus.ppuBus.ppuRegisters.readPPUDATA();
                    case 0x4014 -> throw new RuntimeException("Can't read from write-only register: OAMDMA");
                }
            } else if (addr == 0x4016 || addr == 0x4017) {
                // Read most significant bit of controller state, serial shift it and return it.
                res = (byte) ((bus.controllers_state[addr & 1] & 0x80) > 0 ? 1 : 0);
                bus.controllers_state[addr & 1] <<= 1;
            } else {
                // Not PPU mapping, read from internal memory
                res = cpu_memory[addr & 0xFFFF];
            }
        }

        if (is_record_memory)
            recorded_memory.add(new MemoryAccessRecord(addr, res, true));

        return res;
    }

    /**
     * Used only in debugging or testing or GUI.
     * Do not use in any other case.
     * @param addr
     * @return
     */
    public byte get_cpu_memory(short addr) {
        return cpu_memory[addr & 0xFFFF];
    }

}
