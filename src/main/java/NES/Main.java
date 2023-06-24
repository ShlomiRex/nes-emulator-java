package NES;

import NES.Cartridge.ROMParser;
import NES.UI.Debugger.DebuggerWindow;
import NES.UI.Game.GamePanel;
import NES.UI.Game.GameWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws IOException, ROMParser.ParsingException {
        String program = "6502_programs/nestest/nestest.nes";
        //String program = "6502_programs/greenscreen/greenscreen.nes";

        ROMParser romParser = new ROMParser(program);

        NES nes = new NES(romParser);

        //run_without_debugger(nes);
        run_with_debugger(nes);
    }

    private static void run_with_debugger(NES nes) {
        GamePanel panel = new GamePanel();
        GameWindow gameWindow = new GameWindow(panel);

        byte[] ppuData = nes.ppu.getFrameBuffer(); // get reference
        panel.setPPUFrameBuffer(ppuData);

        DebuggerWindow debuggerWindow = new DebuggerWindow(nes); // The debugger controls the NES program on the GUI thread


    }

    private static void run_without_debugger(NES nes) {
        GamePanel panel = new GamePanel();
        GameWindow gameWindow = new GameWindow(panel);

        byte[] ppuData = nes.ppu.getFrameBuffer(); // get reference
        panel.setPPUFrameBuffer(ppuData);

        final boolean[] is_running = {true};
        // Set the runnable that will be called when the PPU is ready to be redrawn
        AtomicInteger frames_to_draw = new AtomicInteger(2);
        //            frames_to_draw.addAndGet(-1);
        //            if (frames_to_draw.get() <= 0)
        //                is_running[0] = false;
        Runnable redrawRunnable = gameWindow::repaint;
        nes.ppu.set_redraw_runnable_trigger(redrawRunnable);
        while (is_running[0]) {
            nes.cpu.clock_tick();
            nes.ppu.clock_tick();
            nes.ppu.clock_tick();
            nes.ppu.clock_tick();
        }

        logger.debug("Finished running");

        //nes.run(debuggerWindow.getUpdateRunnable());
        //nes.run(null);
    }
}