import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Dirt_Block extends Block {
    static BufferedImage texture;
    static {
        try {
            texture = ImageIO.read(new File("textures/blocks/dirt.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Dirt_Block(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    BufferedImage getBottomTexture() {
        return texture;
    }

    @Override
    BufferedImage getTopTexture() {
        return texture;
    }

    @Override
    BufferedImage getFrontTexture() {
        return texture;
    }

    @Override
    BufferedImage getBackTexture() {
        return texture;
    }

    @Override
    BufferedImage getLeftTexture() {
        return texture;
    }

    @Override
    BufferedImage getRightTexture() {
        return texture;
    }
}
