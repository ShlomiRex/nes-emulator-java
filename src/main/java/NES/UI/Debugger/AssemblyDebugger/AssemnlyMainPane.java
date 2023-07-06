package NES.UI.Debugger.AssemblyDebugger;

import NES.CPU.CPU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class AssemnlyMainPane extends JPanel {


    private final Logger logger = LoggerFactory.getLogger(AssemnlyMainPane.class);
    public final AssemblyTextPane assembly_text_area;

    public AssemnlyMainPane(CPU cpu, byte[] cpu_memory) {
        setBorder(BorderFactory.createLoweredBevelBorder());
        assembly_text_area = new AssemblyTextPane(cpu_memory);


        AssemblyScrollPane scrollPane = new AssemblyScrollPane(assembly_text_area);

        add(scrollPane);

        // Highlight first instruction
        assembly_text_area.ready_next_instruction();
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
        Document doc = assembly_text_area.getStyledDocument();
        doc.insertString(doc.getLength(), str, set);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        assembly_text_area.ready_next_instruction();
    }
}
