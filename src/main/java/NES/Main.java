package NES;

import NES.Cartridge.ROMParser;
import NES.UI.Debugger.DebuggerUIEvents;
import NES.UI.Debugger.DebuggerWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private final Logger logger = LoggerFactory.getLogger(Main.class);
    private NES nes;
    private final DebuggerUIEvents cpu_ui_events;
    private final DebuggerUIEvents ppu_ui_events;

    public static void main(String[] args) throws IOException, ROMParser.ParsingException, InterruptedException {
        new Main().run();
    }

    private Main() throws ROMParser.ParsingException, IOException {
        cpu_ui_events = new DebuggerUIEvents();
        ppu_ui_events = new DebuggerUIEvents();

        String program = "6502_programs/nestest/nestest.nes";
        //String program = "6502_programs/greenscreen/greenscreen.nes";

        ROMParser romParser = new ROMParser(program);

        nes = new NES(romParser);
        DebuggerWindow debuggerWindow = new DebuggerWindow(nes, cpu_ui_events, ppu_ui_events);
    }

    public void run() {
        while(true) {
            synchronized (cpu_ui_events) {
                try {
                    cpu_ui_events.wait(100); // Wait for CPU UI event to happen
                    if (cpu_ui_events.next_tick_request) {
                        logger.debug("CPU tick request");
                        nes.cpu.clock_tick();
                        cpu_ui_events.next_tick_request = false;
                        cpu_ui_events.notify();
                    } else if (cpu_ui_events.run_request) {
                        logger.debug("Running CPU");
                        while(!cpu_ui_events.stop_request) {
                            nes.cpu.clock_tick();
                        }
                        cpu_ui_events.run_request = false;
                        logger.debug("CPU run finished");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            synchronized (ppu_ui_events) {
                try {
                    ppu_ui_events.wait(100); // Wait for PPU UI event to happen
                    if (ppu_ui_events.next_tick_request) {
                        logger.debug("PPU tick request");
                        nes.ppu.clock_tick();
                        ppu_ui_events.next_tick_request = false;
                        ppu_ui_events.notify();
                    } else if (ppu_ui_events.run_request) {
                        logger.debug("Running PPU");
                        while(!ppu_ui_events.stop_request) {
                            nes.ppu.clock_tick();
                        }
                        ppu_ui_events.run_request = false;
                        logger.debug("PPU run finished");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}