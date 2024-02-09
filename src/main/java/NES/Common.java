package NES;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HexFormat;

public class Common {

    private static final Logger logger = LoggerFactory.getLogger(Common.class);

    public static byte[] hexStringToByteArray(String s) {
        StringBuilder new_s = new StringBuilder();
        for (int i = 0; i < s.length(); i+=5) {
            new_s.append(s.charAt(i + 2));
            new_s.append(s.charAt(i + 3));
        }
        return HexFormat.of().parseHex(new_s.toString());
    }

    /**
     * Combine LSB and MSB to a create short (16-bit) value
     * @param lsb
     * @param msb
     * @return
     */
    public static short makeShort(byte lsb, byte msb) {
        return (short) ((msb << 8) | lsb & 0xFF);
    }

    public enum BytesToHexStringFormat {
        NO_FORMAT,
        ARRAY,
    }

    public static String bytesToHex(byte[] bytes, BytesToHexStringFormat format, boolean prefix_0x) {
        if (format == BytesToHexStringFormat.NO_FORMAT) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                if (prefix_0x)
                    sb.append(String.format("0x%02x", b));
                else
                    sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < bytes.length; i++) {
                if (prefix_0x)
                    sb.append(String.format("0x%02X", bytes[i]));
                else
                    sb.append(String.format("%02X", bytes[i]));
                if (i < bytes.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static String byteToBinary(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    public static String shortToBinary(short s) {
        return String.format("%16s", Integer.toBinaryString(s & 0xFFFF)).replace(' ', '0');
    }

    public static String byteToHex(byte b, boolean prefix_0x) {
        if (prefix_0x)
            return String.format("0x%02X", b & 0xFF);
        else
            return String.format("%02X", b & 0xFF);
    }

    public static String shortToHex(short s, boolean prefix_0x) {
        if (!prefix_0x)
            return String.format("%04X", s & 0xFFFF);
        else
            return String.format("0x%04X", s & 0xFFFF);
    }

    public static void debug_log_loopy(short loopy) {
        int coarse_x_scroll = loopy & 0b11111;
        int coarse_y_scroll = (loopy & 0b11111_00000) >> 5;
        int nametable_select = (loopy & 0b11_00000_00000) >> 10;
        int fine_y_scroll = (loopy & 0b111_00_00000_00000) >> 12;

        String binary = Common.byteToBinary((byte) fine_y_scroll).substring(5, 8) + "_" +
                Common.byteToBinary((byte) nametable_select).substring(6, 8) + "_" +
                Common.byteToBinary((byte) coarse_y_scroll).substring(3, 8) + "_" +
                Common.byteToBinary((byte) coarse_x_scroll).substring(3, 8);

        logger.debug("loopy_v: {}, Coarse X: {}, Coarse Y: {}, Nametable select: {}, Fine Y scroll: {}",
                binary, coarse_x_scroll, coarse_y_scroll, nametable_select, fine_y_scroll);
    }

    public static class Bits {
        public static boolean getBit(byte variable, int bitIndex) {
            // TODO: After testing and everything is working we can remove this check.
            if (bitIndex < 0 || bitIndex > 7)
                throw new IllegalArgumentException("Bit index must be between 0 and 7");
            return ((variable >> bitIndex) & 1) == 1;
        }

        public static boolean getBit(short variable, int bitIndex) {
            if (bitIndex < 0 || bitIndex > 15)
                throw new IllegalArgumentException("Bit index must be between 0 and 15");
            return ((variable >> bitIndex) & 1) == 1;
        }

        public static byte setBit(byte variable, int bitIndex, boolean value) {
            if (bitIndex < 0 || bitIndex > 7)
                throw new IllegalArgumentException("Bit index must be between 0 and 7");
            if (value)
                return (byte) (variable | (1 << bitIndex));
            else
                return (byte) (variable & ~(1 << bitIndex));
        }

        /**
         * See: <a href="https://stackoverflow.com/questions/2602823/in-c-c-whats-the-simplest-way-to-reverse-the-order-of-bits-in-a-byte/2602885#2602885">stackoverflow</a>
         * @param variable
         * @return
         */
        public static byte reverseByte(byte variable) {

            byte b = variable;

            b = (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);
            b = (byte) ((b & 0xCC) >> 2 | (b & 0x33) << 2);
            b = (byte) ((b & 0xAA) >> 1 | (b & 0x55) << 1);
            return b;
        }
    }

    public static boolean isAdditionCarry(byte a, byte b) {
        int sum = (a & 0xFF) + (b & 0xFF);
        return sum > 0xFF;
    }

    public static class Pair<A,B> {
        private final A a;
        private final B b;
        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }
    }
}
