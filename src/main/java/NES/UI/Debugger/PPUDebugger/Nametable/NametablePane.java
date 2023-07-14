package NES.UI.Debugger.PPUDebugger.Nametable;

import NES.Cartridge.Mirroring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class NametablePane extends JPanel {
    private final Logger logger = LoggerFactory.getLogger(NametablePane.class);
    private final Mirroring mirroring;

    private final NametableCanvas canvas0, canvas1, canvas2, canvas3;

    public NametablePane(Mirroring mirroring) {
        this.mirroring = mirroring;

        JPanel top_pane = new JPanel();
        JPanel bot_pane = new JPanel();
        JPanel box_pane = new JPanel();

        box_pane.setLayout(new BoxLayout(box_pane, BoxLayout.Y_AXIS));

        NametableInfoPane info_pane = new NametableInfoPane();

        canvas0 = new NametableCanvas(0, info_pane);
        canvas1 = new NametableCanvas(1, info_pane);
        canvas2 = new NametableCanvas(2, info_pane);
        canvas3 = new NametableCanvas(3, info_pane);

        JPanel wrapper0 = new JPanel();
        JPanel wrapper1 = new JPanel();
        JPanel wrapper2 = new JPanel();
        JPanel wrapper3 = new JPanel();

        wrapper0.add(canvas0);
        wrapper1.add(canvas1);
        wrapper2.add(canvas2);
        wrapper3.add(canvas3);

        wrapper0.setBorder(BorderFactory.createTitledBorder("Nametable 0"));
        wrapper1.setBorder(BorderFactory.createTitledBorder("Nametable 1"));
        wrapper2.setBorder(BorderFactory.createTitledBorder("Nametable 2"));
        wrapper3.setBorder(BorderFactory.createTitledBorder("Nametable 3"));

        info_pane.setBorder(BorderFactory.createTitledBorder("Tile info"));

        top_pane.add(wrapper0);
        top_pane.add(wrapper1);

        bot_pane.add(wrapper2);
        bot_pane.add(wrapper3);

        box_pane.add(top_pane);
        box_pane.add(bot_pane);

        add(box_pane);
        add(info_pane);

        bind_canvas_mouse_events(canvas0);
        bind_canvas_mouse_events(canvas1);
        bind_canvas_mouse_events(canvas2);
        bind_canvas_mouse_events(canvas3);
    }

    private void bind_canvas_mouse_events(NametableCanvas canvas) {
        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                canvas.tile_selected = canvas.tile_hover;
                getMirrorCanvas(canvas).tile_selected = canvas.tile_hover;

                canvas.repaint();
                getMirrorCanvas(canvas).repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseExitedEvent(canvas, e);
                mouseExitedEvent(getMirrorCanvas(canvas), e);
            }
        });

        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseMovedEvent(canvas, e);
                mouseMovedEvent(getMirrorCanvas(canvas), e);
            }
        });
    }

    private void mouseMovedEvent(NametableCanvas canvas, MouseEvent e) {
        int x = e.getX() / (8 * NametableCanvas.SCALE);
        int y = e.getY() / (8 * NametableCanvas.SCALE);

        canvas.tile_hover = y * 32 + x;
        canvas.repaint();
    }

    private void mouseExitedEvent(NametableCanvas canvas, MouseEvent e) {
        canvas.tile_hover = -1;
        canvas.repaint();
    }

    private NametableCanvas getMirrorCanvas(NametableCanvas original) {
        int original_index = original.table_index;

        if (mirroring == Mirroring.VERTICAL) {
            if (original_index == 0) {
                return canvas2;
            } else if (original_index == 1) {
                return canvas3;
            } else if (original_index == 2) {
                return canvas0;
            } else {
                return canvas1;
            }
        } else if (mirroring == Mirroring.HORIZONTAL) {
            if (original_index == 0) {
                return canvas1;
            } else if (original_index == 1) {
                return canvas0;
            } else if (original_index == 2) {
                return canvas3;
            } else {
                return canvas2;
            }
        } else {
            throw new RuntimeException("Not implemented");
        }
    }
}
