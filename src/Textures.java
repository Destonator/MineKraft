import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Textures {
    static BufferedImage[] textures = new BufferedImage[4];
    static {
        try {
            textures[0] = ImageIO.read(new File("textures/blocks/dirt.png"));
            textures[1] = ImageIO.read(new File("textures/blocks/grass_block_side.png"));
            textures[2] = ImageIO.read(new File("textures/blocks/grass_block_top.png"));
            textures[3] = ImageIO.read(new File("textures/blocks/stone.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Color[][] cachedColors = new Color[textures.length][256];
    public Textures() {
        int j = 0;
        for (BufferedImage texture : textures) {
            for (int i = 0; i < 256; i++) {
                int y = texture.getWidth() - 1 - (i % texture.getWidth());
                int x = i / texture.getWidth();
                cachedColors[j][i] = new Color(texture.getRGB(x, y));
            }
            j++;
        }
    }
}
