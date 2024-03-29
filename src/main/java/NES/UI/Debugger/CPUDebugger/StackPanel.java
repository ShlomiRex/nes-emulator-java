package NES.UI.Debugger.CPUDebugger;

import NES.Bus.Bus;
import NES.CPU.CPU;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class StackPanel extends JPanel {

    private final DefaultListModel model;
    private final CPU cpu;
    private final Bus bus;

    public StackPanel(CPU cpu, Bus bus) {
        this.cpu = cpu;
        this.bus = bus;

        this.model = new DefaultListModel();

        setBorder(new TitledBorder("Stack"));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JList my_list = new JList(model);
        my_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        my_list.setVisibleRowCount(5);
        my_list.setFixedCellWidth(50);

        JScrollPane scrollPane = new JScrollPane(my_list);

        //add(new JLabel("$1FF"));
        add(scrollPane);
        //add(new JLabel("$100"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int stack_pointer = cpu.registers.S & 0xFF;

        model.clear(); // Clear the list, we add elements again
        for(int i = 0xFF; i > stack_pointer; i--) {
            short addr = (short) (0x100 + i);
            byte mem = bus.cpuBus.get_cpu_memory(addr);
            model.addElement(String.format("%02X: %02X", i, mem));
        }
    }
}
