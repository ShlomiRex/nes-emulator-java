package NES.UI.Game;

import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GamePanel canvas = new GamePanel();
        add(canvas);
        setVisible(true);
    }
}
