package NES.UI.Game;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    public GameWindow(GamePanel panel) {
        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("NES Emulator - Written by: Shlomi Domnenko");
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
    }
}
