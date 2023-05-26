package NES;

import NES.Cartridge.ROMParser;
import NES.UI.Debugger.DebuggerWindow;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ROMParser.ParsingException {
        String program = "6502_programs/nestest/nestest.nes";
        //String program = "6502_programs/greenscreen/greenscreen.nes";

        ROMParser romParser = new ROMParser(program);

        NES nes = new NES(romParser);
        new DebuggerWindow(nes); // The debugger controls the NES program on the GUI thread
    }
}