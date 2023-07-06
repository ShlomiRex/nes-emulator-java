package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.CPU;
import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AssemnlyMainPane extends JPanel {


    private final Logger logger = LoggerFactory.getLogger(AssemnlyMainPane.class);
    public final AssemblyTextPane asm_text_pane;

    public AssemnlyMainPane(byte[] cpu_memory) {
        setBorder(BorderFactory.createLoweredBevelBorder());

        short starting_addr = (short) 0xC004;

        // Init assembly text area (left)
        asm_text_pane = new AssemblyTextPane(starting_addr, cpu_memory, 20);

        JPanel noWrapPanel = new JPanel( new BorderLayout() );
        noWrapPanel.add( asm_text_pane );

        JScrollPane scroll_pane = new JScrollPane(noWrapPanel);
        scroll_pane.setMinimumSize(new Dimension(200, 200));
        scroll_pane.setPreferredSize(new Dimension(200, 200));
        scroll_pane.setMaximumSize(new Dimension(200, 200));

        // Initialize scroll bar (right)
        JScrollBar scrollbar = new JScrollBar(JScrollBar.VERTICAL);
        // Can select address 0x0000 - 0xFFFF
        scrollbar.setMinimum(0);
        scrollbar.setMaximum(0xFFFF);
        scrollbar.setUnitIncrement(1); // When clicking the arrows
        scrollbar.setBlockIncrement(0xF); // When clicking the bar / track
        scrollbar.setValue(starting_addr & 0xFFFF);

        scrollbar.addAdjustmentListener(e -> {
            logger.info("Scrollbar value:" + Common.shortToHex((short) e.getValue(), true));
            short addr = (short) e.getValue();
            asm_text_pane.generate_new_document(addr);
        });

        add(scroll_pane, BorderLayout.CENTER);
        add(scrollbar, BorderLayout.LINE_END);

        // Highlight first instruction
        asm_text_pane.ready_next_instruction();
    }

//    private void initializeAssemblyText() throws BadLocationException {
//        short pc = (short) (cpuRegisters.getPC() & 0xFFFF);
//
//        //TODO: Change assembly_line_num to something bigger... it should write all the assembly lines
//        for (int assembly_line_num = 0; assembly_line_num < 350; assembly_line_num++) {
//            AssemblyInfo info = Decoder.decode_assembly_line(cpu_memory, pc);
//
//            // Assembly line address
//            String str_addr = Common.shortToHex(pc, true);
//            append(str_addr+"\t", attr_black);
//
//            // Instructions bytes (1-3 bytes)
//            append(info.str_instr_bytes+"\t", attr_gray);
//
//            // Opcode
//            if (info.instr_info != null)
//                append(info.instr_info.instr.toString()+" ", attr_blue);
//            else
//                append("UNDEFINED", attr_blue);
//
//            // Operands
//            String operands = info.decoded_operand_or_symbol;
//            if (operands != null) {
////                if (operands.contains("#")) {
////                    // Not symbol - get the prefix and suffix
////                    String[] split = operands.split("\\$");
////                    append(split[0], attr_blue);
////                    append("$"+split[1], attr_green);
////                }
////                else {
////                    // Symbol
////                    append(operands, attr_blue);
////                }
//                append(operands, attr_green);
//            }
//
//            append("\n", attr_black);
//            if (info.instr_info != null)
//                pc += info.instr_info.bytes;
//            else
//                pc += 1;
//        }
//    }

    private void append(String str, SimpleAttributeSet set) throws BadLocationException {
        Document doc = asm_text_pane.getStyledDocument();
        doc.insertString(doc.getLength(), str, set);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        asm_text_pane.ready_next_instruction();
    }
}
