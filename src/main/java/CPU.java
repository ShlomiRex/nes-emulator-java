import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPU {

    private final Logger logger = LoggerFactory.getLogger(CPU.class);

    private CPURegisters registers;
    private long cycles;

    public CPU() {
        this.registers = new CPURegisters();

    }

    public void clock_tick() {
        logger.debug("Tick, cycle: " + this.cycles);
        logger.debug(registers.toString());


    }
}
