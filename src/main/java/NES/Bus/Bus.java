package NES.Bus;

import NES.PPU.PPU;
import NES.PPU.PPURegisters;
import NES.PPU.SystemPallete;

import java.awt.*;

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

    /**
     *  System palette is hard-wired into the NES.
     */
    public static final Color[] SYSTEM_PALETTE = SystemPallete.getSystemPalette();

    public CPUBus cpuBus;
    public PPUBus ppuBus;

    public void attachCPUBus(CPUBus cpuBus) {
        this.cpuBus = cpuBus;
    }

    public void attachPPUBus(PPUBus ppuBus) {
        this.ppuBus = ppuBus;
    }
}
