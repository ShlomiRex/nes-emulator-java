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
        String nestest = "6502_programs/nestest/nestest.nes";
        String greenscreen = "6502_programs/greenscreen/greenscreen.nes";
        String single_sprite = "6502_programs/single_sprite/hb1.nes";
        String mario = "6502_programs/mario/Super Mario Bros (E).nes";
        String color_test = "6502_programs/color_test/color_test.nes";

        String program = single_sprite;

        ROMParser romParser = new ROMParser(program);

        NES nes = new NES(romParser);

        //TODO: Change according to my needs.
        //run_without_debugger(nes);
        run_with_debugger(nes);
    }

    private static void run_with_debugger(NES nes) {
        GamePanel panel = new GamePanel(nes.ppu);
        GameWindow gameWindow = new GameWindow(panel);
        DebuggerWindow debuggerWindow = new DebuggerWindow(nes); // The debugger controls the NES program on the GUI thread

        Runnable redrawRunnable = gameWindow::repaint;
        nes.ppu.addGameCanvasRepaintRunnable(redrawRunnable);
    }

    private static void run_without_debugger(NES nes) {
        GamePanel panel = new GamePanel(nes.ppu);
        GameWindow gameWindow = new GameWindow(panel);


        final boolean[] is_running = {true};
        // Set the runnable that will be called when the PPU is ready to be redrawn
        //is_running[0] = false; // TODO: Remove this. We only draw a single frame.
        Runnable redrawRunnable = gameWindow::repaint;
        nes.ppu.addGameCanvasRepaintRunnable(redrawRunnable);
        while (is_running[0]) {
            nes.cpu.clock_tick();
            nes.ppu.clock_tick();
        }

        logger.debug("Finished running");

        //nes.run(debuggerWindow.getUpdateRunnable());
        //nes.run(null);
    }
}