package NES;

import NES.Cartridge.ROMParser;
import NES.UI.Debugger.DebuggerWindow;
import NES.UI.Game.GamePanel;
import NES.UI.Game.GameWindow;

import java.io.IOException;

public class Main {
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
        DebuggerWindow debuggerWindow = new DebuggerWindow(nes); // The debugger controls the NES program on the GUI thread

    }

    private static void run_without_debugger(NES nes) {
        GamePanel panel = new GamePanel();
        GameWindow gameWindow = new GameWindow(panel);
        DebuggerWindow debuggerWindow = new DebuggerWindow(nes); // The debugger controls the NES program on the GUI thread

        byte[] ppuData = nes.ppu.getFrameBuffer(); // get reference
        panel.setPPUFrameBuffer(ppuData);

        final boolean[] is_running = {true};
        // Set the runnable that will be called when the PPU is ready to be redrawn
        Runnable redrawRunnable = () -> {
            is_running[0] = false;
            gameWindow.repaint();
        };
        nes.ppu.set_redraw_runnable_trigger(redrawRunnable);
        while (is_running[0]) {
            nes.cpu.clock_tick();
            nes.ppu.clock_tick();
        }

        //nes.run(debuggerWindow.getUpdateRunnable());
        //nes.run(null);
    }
}