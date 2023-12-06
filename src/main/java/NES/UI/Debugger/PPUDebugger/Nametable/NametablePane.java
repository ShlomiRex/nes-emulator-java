package NES.UI.Debugger.PPUDebugger.Nametable;

import NES.Cartridge.Mirroring;
import NES.PPU.PPU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
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

        JPanel box_pane = new JPanel();
        JPanel right_pane = new JPanel();

        box_pane.setLayout(new BoxLayout(box_pane, BoxLayout.Y_AXIS));
        right_pane.setLayout(new BoxLayout(right_pane, BoxLayout.Y_AXIS));

        info_pane = new TileInfoPane(this, ppu, mirroring);

        canvas0 = new NametableCanvas(ppu, 0);
        canvas1 = new NametableCanvas(ppu, 1);
        canvas2 = new NametableCanvas(ppu, 2);
        canvas3 = new NametableCanvas(ppu, 3);

        GridBagLayout layout = new GridBagLayout();

        JPanel canvas_container = new JPanel(layout);
        canvas_container.setBorder(BorderFactory.createTitledBorder("Nametables"));

        canvas_container.add(canvas0, createGbc(0, 0));
        canvas_container.add(canvas1, createGbc(1, 0));
        canvas_container.add(canvas2, createGbc(0, 1));
        canvas_container.add(canvas3, createGbc(1, 1));

        right_pane.add(new JLabel("Mirroring: " + mirroring.toString()));
        right_pane.add(info_pane);

        add(canvas_container);
        add(right_pane);

        bind_canvas_mouse_events(canvas0);
        bind_canvas_mouse_events(canvas1);
        bind_canvas_mouse_events(canvas2);
        bind_canvas_mouse_events(canvas3);
    }

    private static GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int gap = 3;
        gbc.insets = new Insets(gap, gap, gap, gap);
        return gbc;
    }

    private void bind_canvas_mouse_events(NametableCanvas canvas) {
        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                int selected_tile = canvas.tile_hover;
                int table_index = canvas.table_index;
                NametableCanvas mirrored = getMirrorCanvas(canvas);

                // Tell both canvases which tile is selected
                canvas.tile_selected = selected_tile;
                mirrored.tile_selected = selected_tile;

                NametableCanvas other_canvas1;
                NametableCanvas other_canvas2;
                if (mirroring == Mirroring.HORIZONTAL) {
                    if (table_index == 0 || table_index == 1) {
                        other_canvas1 = canvas2;
                        other_canvas2 = canvas3;
                    } else {
                        other_canvas1 = canvas0;
                        other_canvas2 = canvas1;
                    }
                } else {
                    if (table_index == 0 || table_index == 2) {
                        other_canvas1 = canvas1;
                        other_canvas2 = canvas3;
                    } else {
                        other_canvas1 = canvas0;
                        other_canvas2 = canvas2;
                    }
                }

                other_canvas1.tile_selected = -1;
                other_canvas2.tile_selected = -1;

                // Update info pane
                info_pane.change_pattern_and_palette(selected_tile, table_index);

                // Repaint
                canvas.repaint();
                mirrored.repaint();
                other_canvas1.repaint();
                other_canvas2.repaint();
                info_pane.repaint();
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
