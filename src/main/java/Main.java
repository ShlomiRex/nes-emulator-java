import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ROMParser.ParsingException {
        Window window = new Window();
        ROMParser romParser = new ROMParser("6502_programs/nestest/nestest.nes");

        NES nes = new NES(romParser);

        // Step instructions by clicking on "Enter", or run continuously
        boolean allow_stepping = true;
        Scanner scanner = new Scanner(System.in);

        while(true) {
            if (allow_stepping)
                scanner.nextLine();

            nes.cpu.clock_tick();
        }
    }


}