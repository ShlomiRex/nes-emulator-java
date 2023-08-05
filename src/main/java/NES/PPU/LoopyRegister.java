package NES.PPU;

/**
 * Represents loopy register:
 * yyy NN YYYYY XXXXX
 * ||| || ||||| +++++-- coarse X scroll
 * ||| || +++++-------- coarse Y scroll
 * ||| ++-------------- nametable select
 * +++----------------- fine Y scroll
 */
public class LoopyRegister {
    /**
     * Coarse scrolling (5 bits).
     */
    public byte coarse_x_select, coarse_y_select;

    /**
     * Base address of nametable (2 bits).
     */
    public byte nametable_select;

    /**
     * Fine scrolling (3 bits).
     */
    public byte fine_y_scroll;

    public LoopyRegister() {

    }
}
