package NES;


import NES.Bus.Bus;
import NES.Bus.CPUBus;
import NES.Bus.PPUBus;
import NES.CPU.CPU;
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

    // We want to deal with creating the memory here, so its more manageable, and each component can take modular memory.
    public NES(ROMParser romParser) {
        iNESHeader header = romParser.getHeader();
        this.header = header;
        byte[] prg_rom = romParser.getPrg_rom();
        byte[] chr_rom = romParser.getChr_rom();

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
        ppuBus = new PPUBus(ppu.registers, chr_rom, header.getMirrorType());

        bus.attachCPUBus(cpuBus);
        bus.attachPPUBus(ppuBus);

        ppu.attachBus(bus);

        cpu.reset();
    }

    public void run() {
        is_running = true;

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            if(!is_running)
                executor.shutdown();

            cpu.clock_tick();
            ppu.clock_tick();
            ppu.clock_tick();
            ppu.clock_tick();
        }, 0, 559, TimeUnit.NANOSECONDS);
    }

    public void stop() {
        is_running = false;
    }
}
