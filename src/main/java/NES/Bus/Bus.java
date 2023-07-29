package NES.Bus;

import NES.PPU.PPU;
import NES.PPU.PPURegisters;

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
        byte data = 0;
        if (addr == 0x4016 || addr == 0x4017) {
            // Read most significant bit of controller state, serial shift it and return it.
            data = (byte) ((controllers_state[addr & 1] & 0x80) > 0 ? 1 : 0);
            controllers_state[addr & 1] <<= 1;
        }

        return data;
    }
}
