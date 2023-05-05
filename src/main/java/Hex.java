import java.util.HexFormat;

public class Hex {
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

    public static String bytesToHexString(byte[] bytes, BytesToHexStringFormat format) {
        if (format == BytesToHexStringFormat.NO_FORMAT) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("0x%02X", bytes[i]));
                if (i < bytes.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }
}
