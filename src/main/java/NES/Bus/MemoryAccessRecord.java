package NES.Bus;

public record MemoryAccessRecord(short addr, byte value, boolean is_read) {
    @Override
    public String toString() {
        String read_write = is_read ? "read" : "write";
        return "["+(addr & 0xFFFF)+","+(value & 0xFF)+","+read_write+"]";
    }
}
