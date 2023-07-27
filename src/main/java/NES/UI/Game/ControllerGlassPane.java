package NES.UI.Game;

import NES.Bus.Bus;

import javax.swing.*;
import java.awt.*;

public class ControllerGlassPane extends JComponent {

    private final GamePanel gamePanel;
    private final Bus bus;

    private final Color pressed_key_color = Color.RED;
    private final Color released_key_color = Color.BLACK;

    public ControllerGlassPane(GamePanel gamePanel, Bus bus) {
        this.gamePanel = gamePanel;
        this.bus = bus;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int SCALE = 2;

        int width = gamePanel.getWidth();
        int height = gamePanel.getHeight();
        int pixel_width = width / 256 * SCALE;
        int pixel_height = height / 240 * SCALE;
        int x = 0;
        int y = 0;

        byte controller_dataline = bus.controllers[0];
        boolean is_a_pressed = (controller_dataline & 1) == 1;
        boolean is_b_pressed = (controller_dataline & 2) == 2;
        boolean is_select_pressed = (controller_dataline & 4) == 4;
        boolean is_start_pressed = (controller_dataline & 8) == 8;
        boolean is_up_pressed = (controller_dataline & 16) == 16;
        boolean is_down_pressed = (controller_dataline & 32) == 32;
        boolean is_left_pressed = (controller_dataline & 64) == 64;
        boolean is_right_pressed = (controller_dataline & 128) == 128;

        // TODO: Remove

        // Draw base canvas
        //g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y, pixel_width * 64, pixel_height * 8);

        // Draw border
        g2d.setColor(Color.RED);
        g2d.drawRect(x, y, pixel_width * 64, pixel_height * 8);

        // Draft left arrow
        g2d.setColor(is_left_pressed? pressed_key_color : released_key_color);
        g2d.fillRect(x, y + pixel_height * 3, pixel_width * 3, pixel_height * 2);

        // Draft up arrow
        g2d.setColor(is_up_pressed? pressed_key_color : released_key_color);
        g2d.fillRect(x + pixel_width * 3, y, pixel_width * 2, pixel_height * 3);

        // Draft down arrow
        g2d.setColor(is_down_pressed? pressed_key_color : released_key_color);
        g2d.fillRect(x + pixel_width * 3, y + pixel_height * 5, pixel_width * 2, pixel_height * 3);

        // Draft right arrow
        g2d.setColor(is_right_pressed? pressed_key_color : released_key_color);
        g2d.fillRect(x + pixel_width * 5, y + pixel_height * 3, pixel_width * 3, pixel_height * 2);
    }
}
