package NES.UI.Game;

import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow(GamePanel panel) {
        add(panel);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("NES Emulator - Written by: Shlomi Domnenko");
        setVisible(true);
    }
}
