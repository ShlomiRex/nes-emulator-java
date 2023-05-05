import javax.swing.*;

public class Window extends JFrame {
    private Canvas canvas;
    public Window() {
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas = new Canvas();
        add(canvas);
        setVisible(true);
    }
}
