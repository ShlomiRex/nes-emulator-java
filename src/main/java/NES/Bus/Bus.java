package NES.Bus;

public class Bus {

    /**
     * The PPU sets the NMI line to true when Vblank occured.
     * The CPU checks this line before executing each instruction.
     */
    public boolean nmi_line;

    public byte[] controllers = new byte[2];
}
