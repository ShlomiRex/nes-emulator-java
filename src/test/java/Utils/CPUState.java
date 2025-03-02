package Utils;

import NES.Common;

public record CPUState(short pc, byte a, byte x, byte y, byte p, byte sp) {
    @Override
    public String toString() {
        return "CPUState[" +
                "pc=" + Common.shortToHex(pc, true) +
                ", a=" + Common.byteToHex(a, true) +
                ", x=" + Common.byteToHex(x, true) +
                ", y=" + Common.byteToHex(y, true) +
                ", p=" + Common.byteToHex(p, true) +
                ", sp=" + Common.byteToHex(sp, true) +
        ']';
    }
}