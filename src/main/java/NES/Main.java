package NES;

import NES.Cartridge.Cartridge;
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
        // My programs
        String nestest = "6502_programs/nestest/nestest.nes";
        String greenscreen = "6502_programs/greenscreen/greenscreen.nes";
        String single_sprite = "6502_programs/single_sprite/hb1.nes";
        String mario = "6502_programs/mario/Super Mario Bros (E).nes";
        String color_test = "6502_programs/color_test/color_test.nes";
        String donkey_kong_mapper3 = "6502_programs/donkey_kong/Donkey Kong Classics (U).nes";
        String donkey_kong_mapper0 = "6502_programs/donkey_kong/Donkey Kong (USA) (GameCube Edition).nes";
        String pacman = "6502_programs/pacman/Pac-Man (USA) (Namco).nes";
        String scroll_test = "6502_programs/scroll_test/scroll.nes";
        String hello_sprites = "6502_programs/hello_sprites/hello.nes";
        String hello_backgrounds = "6502_programs/hello_backgrounds/example.nes";
        String hello_backgrounds2 = "6502_programs/hello_backgrounds2/example.nes";
        String hello = "6502_programs/hello/hello.nes";
        String single_bt_tile_at_start = "6502_programs/single_bg_tile_at_start/example.nes";

        // Balrgg's tests
        String palette_ram = "nes-test-roms/blargg_ppu_tests_2005.09.15b/palette_ram.nes";
        String power_up_palette = "nes-test-roms/blargg_ppu_tests_2005.09.15b/power_up_palette.nes";

        String program = hello_backgrounds;

        Cartridge cartridge = ROMParser.parse_rom(program);

        NES nes = new NES(cartridge);

        boolean debugger = Boolean.parseBoolean(System.getenv().get("debugger"));

        if (debugger)
            run_with_debugger(nes);
        else
            run_without_debugger(nes);
    }

    private static void run_with_debugger(NES nes) {
        GamePanel panel = new GamePanel(nes.ppu);
        GameWindow gameWindow = new GameWindow(nes, panel);
        DebuggerWindow debuggerWindow = new DebuggerWindow(nes); // The debugger controls the NES program on the GUI thread

        Runnable redrawRunnable = gameWindow::repaint;
        nes.ppu.addGameCanvasRepaintRunnable(redrawRunnable);
    }

    private static void run_without_debugger(NES nes) {
        GamePanel panel = new GamePanel(nes.ppu);
        GameWindow gameWindow = new GameWindow(nes, panel);

        Runnable redrawRunnable = gameWindow::repaint;
        nes.ppu.addGameCanvasRepaintRunnable(redrawRunnable);
        nes.run();
    }
}