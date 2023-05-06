import java.util.HexFormat;

public class Common {
    public static byte[] hexStringToByteArray(String s) {
        StringBuilder new_s = new StringBuilder();
        for (int i = 0; i < s.length(); i+=5) {
            new_s.append(s.charAt(i + 2));
            new_s.append(s.charAt(i + 3));
        }
        return HexFormat.of().parseHex(new_s.toString());
    }

    public enum BytesToHexStringFormat {
        NO_FORMAT,
        ARRAY,
    }

    public static String bytesToHexString(byte[] bytes, BytesToHexStringFormat format, boolean leading_0x) {
        if (format == BytesToHexStringFormat.NO_FORMAT) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                if (leading_0x)
                    sb.append(String.format("0x%02x", b));
                else
                    sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < bytes.length; i++) {
                if (leading_0x)
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

    public static String byteToBinaryString(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    public static String byteToHexString(byte b, boolean leading_0x) {
        if (leading_0x)
            return String.format("0x%02X", b & 0xFF);
        else
            return String.format("%02X", b & 0xFF);
    }

    public static String shortToHexString(short s, boolean leading_0x) {
        if (!leading_0x)
            return String.format("%04X", s & 0xFFFF);
        else
            return String.format("0x%04X", s & 0xFFFF);
    }

    public class Bits {
        public static boolean getBit(byte variable, int bitIndex) {
            if (bitIndex < 0 || bitIndex > 7)
                throw new IllegalArgumentException("Bit index must be between 0 and 7");
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
    }
}