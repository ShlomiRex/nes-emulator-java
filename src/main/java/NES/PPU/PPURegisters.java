package NES.PPU;

public class PPURegisters {

    private byte status;
    private byte ctrl;
    private byte mask;

    public void reset() {
        status = ctrl = mask = 0;
    }

    public byte readStatus() {
        // TODO: Clear VBlank
        return status;
    }

    /**
     * Do not use in CPU. Only outside CPU.
     * In CPU, use this instead: `readStatus()`
     * @return
     */
    public byte getStatus() {
        return status;
    }

    public byte getCtrl() {
        return ctrl;
    }

    public byte getMask() {
        return mask;
    }
}
