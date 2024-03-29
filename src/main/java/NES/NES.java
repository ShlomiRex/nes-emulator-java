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

    public final byte[] cpu_memory; // All 64KB addressable memory
    public final iNESHeader header;
    public final Bus bus;
    public final CPUBus cpuBus;
    public final PPUBus ppuBus;

    private Boolean is_running = false;

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
        run(false, null);
    }

    public void run(boolean max_speed) {
        run(max_speed, null);
    }
    public void run(Runnable post_run) {
        run(false, post_run);
    }

    public void run(boolean max_speed, Runnable post_run) {
        logger.debug("Running NES");
        is_running = true;

        if (!max_speed) {
            long delay = 559;
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(() -> {
                if (!is_running) {
                    if (post_run != null)
                        post_run.run();

                    executor.shutdown();
                }

                try {
                    cpu.clock_tick();
                    ppu.clock_tick();
                    ppu.clock_tick();
                    ppu.clock_tick();
                } catch(Exception e) {
                    logger.error("Exception in NES.run", e);
                    System.exit(1); // Close other threads (swing)
                }
            }, 0, delay, TimeUnit.NANOSECONDS);
        } else {
            // We can't set delay to 0.
            // This is code is a bit more efficient than setting delay to 1.
            new Thread(() -> {
                while (is_running) {
                    try {
                        cpu.clock_tick();
                        ppu.clock_tick();
                        ppu.clock_tick();
                        ppu.clock_tick();
                    } catch (Exception e) {
                        logger.error("Exception in NES.run", e);
                        System.exit(1); // Close other threads (swing)
                    }
                }
                if (post_run != null)
                    post_run.run();
            }).start();
        }
    }

    public void stop() {
        logger.debug("Stopping NES");
        is_running = false;
    }
}
