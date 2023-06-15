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
        GamePanel panel = new GamePanel();
        GameWindow gameWindow = new GameWindow(panel);

        byte[] ppuData = nes.ppu.getFrameBuffer(); // get reference
        panel.setPPUFrameBuffer(ppuData);
        while (true) {
            nes.ppu.clock_tick();
            panel.repaint();
            try {
                Thread.sleep(1000); // Sleep for a short duration (adjust as needed for desired frame rate)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //DebuggerWindow debuggerWindow = new DebuggerWindow(nes); // The debugger controls the NES program on the GUI thread
        //nes.run(debuggerWindow.getUpdateRunnable());
        //nes.run(null);
    }
}