package NES.Cartridge;

import com.sun.jdi.Mirror;

public class iNESHeader {
    public enum TVSystem {
        PAL,
        NTSC,
        DUAL
    }

    public final int prg_rom_size;
    public final int chr_rom_size;
    private final int mapper;
    private final Mirroring mirrorType;
    private final boolean battery_prg_ram;
    private final boolean trainer;
    private final boolean ignore_mirroring_control;
    private final boolean vs_unit_system;
    private final boolean play_choise_10;
    private final boolean nes2_format;
    private final int prg_ram_size;
    private final TVSystem flags9_tv_system;
    private final TVSystem flags10_tv_system;
    private final boolean prg_ram_not_present;
    private final boolean bus_conflicts;

    public iNESHeader(int prg_rom_size,
                      int chr_rom_size,
                      int mapper,
                      Mirroring mirrorType,
                      boolean battery_prg_ram,
                      boolean trainer,
                      boolean ignore_mirroring_control,
                      boolean vs_unit_system,
                      boolean play_choise_10,
                      boolean nes2_format,
                      int prg_ram_size,
                      TVSystem flags9_tv_system,
                      TVSystem flags10_tv_system,
                      boolean prg_ram_not_present,
                      boolean bus_conflicts) {
        this.prg_rom_size = prg_rom_size;
        this.chr_rom_size = chr_rom_size;
        this.mapper = mapper;
        this.mirrorType = mirrorType;
        this.battery_prg_ram = battery_prg_ram;
        this.trainer = trainer;
        this.ignore_mirroring_control = ignore_mirroring_control;
        this.vs_unit_system = vs_unit_system;
        this.play_choise_10 = play_choise_10;
        this.nes2_format = nes2_format;
        this.prg_ram_size = prg_ram_size;
        this.flags9_tv_system = flags9_tv_system;
        this.flags10_tv_system = flags10_tv_system;
        this.prg_ram_not_present = prg_ram_not_present;
        this.bus_conflicts = bus_conflicts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NES.Cartridge.iNESHeader {\n");
            sb.append("\tprg_rom_size: ").append(prg_rom_size).append(",\n");
            sb.append("\tchr_rom_size: ").append(chr_rom_size).append(",\n");
            sb.append("\tmapper: ").append(mapper).append(",\n");
            sb.append("\tmirroring: ").append(mirrorType).append(",\n");
            sb.append("\tbattery_prg_ram: ").append(battery_prg_ram).append(",\n");
            sb.append("\ttrainer: ").append(trainer).append(",\n");
            sb.append("\tignore_mirroring_control: ").append(ignore_mirroring_control).append(",\n");
            sb.append("\tvs_unit_system: ").append(vs_unit_system).append(",\n");
            sb.append("\tplay_choise_10: ").append(play_choise_10).append(",\n");
            sb.append("\tnes2_format: ").append(nes2_format).append(",\n");
            sb.append("\tprg_ram_size: ").append(prg_ram_size).append(",\n");
            sb.append("\tflags9_tv_system: ").append(flags9_tv_system).append(",\n");
            sb.append("\tflags10_tv_system: ").append(flags10_tv_system).append(",\n");
            sb.append("\tprg_ram_not_present: ").append(prg_ram_not_present).append(",\n");
            sb.append("\tbus_conflicts: ").append(bus_conflicts).append("\n");
        sb.append("}");
        return sb.toString();
    }

    public Mirroring getMirrorType() {
        return mirrorType;
    }
}
