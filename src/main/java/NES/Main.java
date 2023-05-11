package NES;

import NES.Cartridge.ROMParser;
import NES.UI.Debugger.DebuggerWindow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) throws IOException, ROMParser.ParsingException, InterruptedException {
        // This latch is used both in UI thread and in main thread.
        // It synchronizes the UI to update itself after instruction has been executed.
        AtomicBoolean next_tick = new AtomicBoolean();

        String program = "6502_programs/nestest/nestest.nes";
        //String program = "6502_programs/greenscreen/greenscreen.nes";

        ROMParser romParser = new ROMParser(program);


        NES nes = new NES(romParser);
        DebuggerWindow debuggerWindow = new DebuggerWindow(nes, next_tick);

        synchronized (next_tick) {
            while(true) {
                next_tick.wait();
                if (next_tick.get()) {
                    nes.cpu.clock_tick();
                    next_tick.notify();
                    next_tick.set(false); // Reset latch
                }
            }
        }
    }


}