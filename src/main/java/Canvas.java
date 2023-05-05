import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {
    public Canvas() {

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(100, 100, 50, 50);
    }
}
