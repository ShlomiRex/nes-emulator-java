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
    private final DebuggerUIEvents ui_events;

    public static void main(String[] args) throws IOException, ROMParser.ParsingException, InterruptedException {
        new Main().run();
    }

    private Main() throws ROMParser.ParsingException, IOException {
        ui_events = new DebuggerUIEvents();

        String program = "6502_programs/nestest/nestest.nes";
        //String program = "6502_programs/greenscreen/greenscreen.nes";

        ROMParser romParser = new ROMParser(program);

        nes = new NES(romParser);
        DebuggerWindow debuggerWindow = new DebuggerWindow(nes, ui_events);
    }

    public void run() throws InterruptedException {
        while(true) {
            synchronized (ui_events) {
                ui_events.wait(); // Wait for UI event to happen
                if (ui_events.next_tick_request) {
                    logger.debug("Tick request");
                    nes.cpu.clock_tick();
                    ui_events.notify();
                } else if (ui_events.run_request) {
                    logger.debug("Running request");
                    while(!ui_events.stop_request) {
                        nes.cpu.clock_tick();
                    }
                    logger.debug("Run finished");
                }
            }
        }
    }
}