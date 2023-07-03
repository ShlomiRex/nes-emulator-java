package NES;

import NES.Cartridge.ROMParser;
import NES.PPU.PPU;
import NES.UI.Debugger.DebuggerWindow;
import NES.UI.Game.GamePanel;
import NES.UI.Game.GameWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws IOException, ROMParser.ParsingException {
        //String program = "6502_programs/nestest/nestest.nes";
        String program = "6502_programs/greenscreen/greenscreen.nes";

        ROMParser romParser = new ROMParser(program);

        NES nes = new NES(romParser);

        //TODO: Change according to my needs.
        run_without_debugger(nes);
        //run_with_debugger(nes);
    }

    private static void run_with_debugger(NES nes) {
        GamePanel panel = new GamePanel();
        GameWindow gameWindow = new GameWindow(panel);
        DebuggerWindow debuggerWindow = new DebuggerWindow(nes); // The debugger controls the NES program on the GUI thread
    }

    private static void run_without_debugger(NES nes) {
        GamePanel panel = new GamePanel();
        GameWindow gameWindow = new GameWindow(panel);

        byte[] ppuData = nes.ppu.getFrameBuffer(); // get reference
        panel.setPPUFrameBuffer(ppuData);

        final boolean[] is_running = {true};
        // Set the runnable that will be called when the PPU is ready to be redrawn
        Runnable redrawRunnable = () -> {
            //is_running[0] = false; // TODO: Remove this. We only draw a single frame.
            gameWindow.repaint();
        };
        nes.ppu.set_redraw_runnable_trigger(redrawRunnable);
        while (is_running[0]) {
            nes.cpu.clock_tick();
            nes.ppu.clock_tick();
        }

        logger.debug("Finished running");

        //nes.run(debuggerWindow.getUpdateRunnable());
        //nes.run(null);
    }
}