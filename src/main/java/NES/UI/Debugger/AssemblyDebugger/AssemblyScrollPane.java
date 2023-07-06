package NES.UI.Debugger.AssemblyDebugger;

import NES.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class AssemblyScrollPane extends JScrollPane {

    private final Logger logger = LoggerFactory.getLogger(AssemblyScrollPane.class);

    public AssemblyScrollPane(AssemblyTextPane assembly_text_area) {
        super(assembly_text_area);

        JScrollBar scrollbar = getVerticalScrollBar();

        // Can select address 0x0000 - 0xFFFF
        scrollbar.setMinimum(0);
        scrollbar.setMaximum(0xFFFF);
        scrollbar.setUnitIncrement(1); // When clicking the arrows
        scrollbar.setBlockIncrement(0xF); // When clicking the bar / track

        scrollbar.addAdjustmentListener(e -> {
            logger.info("Scrollbar value:" + Common.shortToHex((short) e.getValue(), true));
            assembly_text_area.jump_to_view((short) e.getValue());
        });

        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
