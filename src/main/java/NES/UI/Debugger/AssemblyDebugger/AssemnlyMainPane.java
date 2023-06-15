package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.Decoder;
import NES.CPU.Registers.CPURegisters;
import NES.Common;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AssemnlyMainPane extends JPanel {

    public final AssemblyTextPane assembly_text_area;
    private final Decoder decoder;
    private final byte[] cpu_memory;
    private final CPURegisters cpuRegisters;

    private final SimpleAttributeSet attr_black, attr_blue, attr_gray, attr_green;

    public AssemnlyMainPane(CPURegisters cpuRegisters, byte[] cpu_memory) {
        this.cpu_memory = cpu_memory;
        this.cpuRegisters = cpuRegisters;
        this.decoder = new Decoder();

        setBorder(BorderFactory.createLoweredBevelBorder());

        assembly_text_area = new AssemblyTextPane(cpuRegisters);

        try {
            attr_black = new SimpleAttributeSet();
            attr_gray = new SimpleAttributeSet();
            attr_blue = new SimpleAttributeSet();
            attr_green = new SimpleAttributeSet();

            StyleConstants.setForeground(attr_black, Color.BLACK);
            StyleConstants.setForeground(attr_gray, Color.GRAY);
            StyleConstants.setForeground(attr_blue, new Color(0, 30, 116));
            StyleConstants.setForeground(attr_green, new Color(8, 124, 0));

            StyleConstants.setBold(attr_black, true);
            StyleConstants.setBold(attr_gray, true);
            StyleConstants.setBold(attr_blue, true);
            StyleConstants.setBold(attr_green, true);

            initializeAssemblyText();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        // Highlight first instruction
        assembly_text_area.ready_next_instruction();

        JScrollPane scrollPane = new JScrollPane(assembly_text_area);
        scrollPane.setPreferredSize(new Dimension(250, 800));
        add(scrollPane);
    }

    private void initializeAssemblyText() throws BadLocationException {
        short pc = (short) (cpuRegisters.getPC() & 0xFFFF);

        for (int assembly_line_num = 0; assembly_line_num < 129; assembly_line_num++) {
            Decoder.AssemblyInfo info = decoder.decode_assembly_line(cpu_memory, pc);

            // Assembly line address
            String str_addr = Common.shortToHex(pc, true);
            append(str_addr+"\t", attr_black);

            // Instructions bytes (1-3 bytes)
            append(info.str_instr_bytes+"\t", attr_gray);

            // Opcode
            append(info.instr_info.instr.toString()+" ", attr_blue);

            // Operands
            String operands = info.decoded_operand_or_symbol;
            if (operands != null) {
                if (operands.contains("#")) {
                    // Not symbol - get the prefix and suffix
                    String[] split = operands.split("\\$");
                    append(split[0], attr_blue);
                    append("$"+split[1], attr_green);
                } else if (operands.contains("$")) {
                    // Not symbol - get the prefix and suffix
                    String[] split = operands.split("\\$");
                    append(split[0], attr_blue);
                    append("$"+split[1], attr_green);
                }
                else {
                    // Symbol
                    append(operands, attr_blue);
                }
            }

            append("\n", attr_black);
            pc += info.instr_info.bytes;
        }
    }

    private void append(String str, SimpleAttributeSet set) throws BadLocationException {
        Document doc = assembly_text_area.getStyledDocument();
        doc.insertString(doc.getLength(), str, set);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        assembly_text_area.ready_next_instruction();
    }
}
