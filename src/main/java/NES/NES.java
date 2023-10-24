package NES;


import NES.Bus.Bus;
import NES.Bus.CPUBus;
import NES.Bus.PPUBus;
import NES.CPU.CPU;
import NES.Cartridge.Cartridge;
import NES.Cartridge.ROMParser;
import NES.Cartridge.iNESHeader;
import NES.PPU.PPU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NES {

    private final Logger logger = LoggerFactory.getLogger(NES.class);
    public final CPU cpu;
    public PPU ppu;
    private boolean is_running;


    public final byte[] cpu_memory; // All 64KB addressable memory
    public final iNESHeader header;
    public final Bus bus;
    public final CPUBus cpuBus;
    public final PPUBus ppuBus;

    // TODO: Do not touch, only for debugging
    private final double SPEED_MODIFIER = 1;

    // We want to deal with creating the memory here, so its more manageable, and each component can take modular memory.
    public NES(Cartridge cartridge) {
        this.header = cartridge.header();
        byte[] prg_rom = cartridge.prg_rom();

        this.cpu_memory = new byte[64 * 1024];

        //TODO: Mapper 0 only

        if (prg_rom.length == 16*1024) {
            // Mirror the PRG banks (mapper 0 only)
            // Lower bank
            System.arraycopy(prg_rom, 0, this.cpu_memory, 0x8000, 1024*16);
            // Upper bank
            System.arraycopy(prg_rom, 0, this.cpu_memory, 0xC000, 1024*16);
        } else {
            // 32KB
            System.arraycopy(prg_rom, 0, this.cpu_memory, 0x8000, 1024*32);
        }

        bus = new Bus();
        cpu = new CPU(bus, cpu_memory);
        ppu = new PPU();

        cpuBus = new CPUBus(bus, false, false, cpu_memory);
        ppuBus = new PPUBus(ppu.registers, cartridge);

        bus.attachCPUBus(cpuBus);
        bus.attachPPUBus(ppuBus);

        ppu.attachBus(bus);

        cpu.reset();
    }

    public void run() {
        try {
            is_running = true;

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!is_running)
                            executor.shutdown();

                        cpu.clock_tick();
                        ppu.clock_tick();
                        ppu.clock_tick();
                        ppu.clock_tick();
                    } catch(Exception e) {
                        logger.error("Error in NES.run() executor: ", e);
                        is_running = false;
                    }
                }
            }, 0, (long) (559 * SPEED_MODIFIER), TimeUnit.NANOSECONDS);
//            executor.scheduleAtFixedRate(() -> {
//
//            }, 0, (long) (559 * SPEED_MODIFIER), TimeUnit.NANOSECONDS);
        } catch(Throwable t) {
            logger.error("Error in NES.run()", t);
        }
    }

    public void stop() {
        is_running = false;
    }
}
