package NES.UI.Game;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    public GameWindow(GamePanel panel) {
        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("NES Emulator - Written by: Shlomi Domnenko");

        // Set icon
        Image img_icon = new ImageIcon("resources/NES_icon.png").getImage();
        setIconImage(img_icon);

        setVisible(true);
        pack();
        setLocationRelativeTo(null);
    }
}
