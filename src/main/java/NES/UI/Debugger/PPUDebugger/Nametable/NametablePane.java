package NES.UI.Debugger.PPUDebugger.Nametable;

import NES.Cartridge.Mirroring;
import NES.PPU.PPU;
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
    private final TileInfoPane info_pane;

    public NametablePane(PPU ppu, Mirroring mirroring) {
        this.mirroring = mirroring;

        JPanel top_pane = new JPanel();
        JPanel bot_pane = new JPanel();
        JPanel box_pane = new JPanel();

        box_pane.setLayout(new BoxLayout(box_pane, BoxLayout.Y_AXIS));

        info_pane = new TileInfoPane(this, ppu);

        canvas0 = new NametableCanvas(ppu, 0);
        canvas1 = new NametableCanvas(ppu, 1);
        canvas2 = new NametableCanvas(ppu, 2);
        canvas3 = new NametableCanvas(ppu, 3);

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
                int selected_tile = canvas.tile_hover;
                NametableCanvas mirrored = getMirrorCanvas(canvas);

                canvas.tile_selected = selected_tile;
                mirrored.tile_selected = selected_tile;

                int table_index = canvas.table_index;
                boolean is_nametable_A;
                if (mirroring == Mirroring.HORIZONTAL) {
                    if (table_index == 0 || table_index == 2) {
                        is_nametable_A = true;
                    } else {
                        is_nametable_A = false;
                    }
                } else {
                    if (table_index == 0 || table_index == 1) {
                        is_nametable_A = true;
                    } else {
                        is_nametable_A = false;
                    }
                }

                logger.debug("Selected tile in nametable A? {}, mirroring: {}", is_nametable_A ? "Yes" : "No", mirroring);

                info_pane.setSelectedTileIndex(is_nametable_A, selected_tile);

                canvas.repaint();
                mirrored.repaint();
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

    public void setShowTileGrid(boolean is_show) {
        canvas0.setShowTileGrid(is_show);
        canvas1.setShowTileGrid(is_show);
        canvas2.setShowTileGrid(is_show);
        canvas3.setShowTileGrid(is_show);
    }

    public void setShowAttributeGrid(boolean is_show) {
        canvas0.setShowAttributeGrid(is_show);
        canvas1.setShowAttributeGrid(is_show);
        canvas2.setShowAttributeGrid(is_show);
        canvas3.setShowAttributeGrid(is_show);
    }
}
