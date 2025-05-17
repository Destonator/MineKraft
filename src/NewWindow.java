import javax.swing.*;
import java.awt.*;

public class NewWindow{
    JFrame frame = new JFrame();
    Screen screen = new Screen();
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    NewWindow() {
        frame.add(screen);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);

        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        graphicsDevice.setFullScreenWindow(frame);

        frame.setVisible(true);
    }
}
