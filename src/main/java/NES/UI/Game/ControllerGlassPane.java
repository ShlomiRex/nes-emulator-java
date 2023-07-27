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
        draw_controller(g, 0);
        draw_controller(g, 1);
    }

    private void draw_controller(Graphics g, int controller_id) {
        Graphics2D g2d = (Graphics2D) g;

        int SCALE = 2;

        int width = gamePanel.getWidth();
        int height = gamePanel.getHeight();
        int pixel_width = width / 256 * SCALE;
        int pixel_height = height / 240 * SCALE;
        int x = 0;
        if (controller_id == 1)
            x += pixel_width * 32;
        int y = pixel_height * 112;

        byte controller_dataline = bus.controllers[0];
        if (controller_id == 1)
            controller_dataline = bus.controllers[1];
        else if (controller_id != 0)
            throw new RuntimeException("Invalid controller id: " + controller_id + ". Valid values are 0 or 1.");

        boolean is_a_pressed = (controller_dataline & 1) == 1;
        boolean is_b_pressed = (controller_dataline & 2) == 2;
        boolean is_select_pressed = (controller_dataline & 4) == 4;
        boolean is_start_pressed = (controller_dataline & 8) == 8;
        boolean is_up_pressed = (controller_dataline & 16) == 16;
        boolean is_down_pressed = (controller_dataline & 32) == 32;
        boolean is_left_pressed = (controller_dataline & 64) == 64;
        boolean is_right_pressed = (controller_dataline & 128) == 128;

        // Set transparency
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

        // Draw base canvas
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y, pixel_width * 28, pixel_height * 8);

        // Draw border
//        g2d.setColor(Color.RED);
//        g2d.drawRect(x, y, pixel_width * 32, pixel_height * 8);

        // Draw left arrow
        g2d.setColor(is_left_pressed? pressed_key_color : released_key_color);
        g2d.fillRect(x, y + pixel_height * 3, pixel_width * 3, pixel_height * 2);

        // Draw up arrow
        g2d.setColor(is_up_pressed? pressed_key_color : released_key_color);
        g2d.fillRect(x + pixel_width * 3, y, pixel_width * 2, pixel_height * 3);

        // Draw down arrow
        g2d.setColor(is_down_pressed? pressed_key_color : released_key_color);
        g2d.fillRect(x + pixel_width * 3, y + pixel_height * 5, pixel_width * 2, pixel_height * 3);

        // Draw right arrow
        g2d.setColor(is_right_pressed? pressed_key_color : released_key_color);
        g2d.fillRect(x + pixel_width * 5, y + pixel_height * 3, pixel_width * 3, pixel_height * 2);

        // Draw select key
        g2d.setColor(is_select_pressed? pressed_key_color : released_key_color);
        g2d.fillRect(x + pixel_width * 10, y + pixel_height * 4, pixel_width * 3, pixel_height * 2);

        // Draw start key
        g2d.setColor(is_start_pressed? pressed_key_color : released_key_color);
        g2d.fillRect(x + pixel_width * 14, y + pixel_height * 4, pixel_width * 3, pixel_height * 2);

        // Draw B key
        g2d.setColor(is_b_pressed? pressed_key_color : released_key_color);
        // Horizontal cross
        g2d.fillRect(x + pixel_width * 19, y + pixel_height * 3, pixel_width * 4, pixel_height * 2);
        // Vertical cross
        g2d.fillRect(x + pixel_width * 20, y + pixel_height * 2, pixel_width * 2, pixel_height * 4);

        // Draw A key
        g2d.setColor(is_a_pressed? pressed_key_color : released_key_color);
        // Horizontal cross
        g2d.fillRect(x + pixel_width * 24, y + pixel_height * 3, pixel_width * 4, pixel_height * 2);
        // Vertical cross
        g2d.fillRect(x + pixel_width * 25, y + pixel_height * 2, pixel_width * 2, pixel_height * 4);
    }
}
