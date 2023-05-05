public class NES {

    private ROMParser romParser;
    public CPU cpu;

    public NES(ROMParser romParser) {
        this.romParser = romParser;
        cpu = new CPU();
    }
}
