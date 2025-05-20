import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Grass_Block extends Block {
    static BufferedImage bTexture;
    static {
        try {
            bTexture = ImageIO.read(new File("textures/blocks/dirt.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static BufferedImage sTexture;
    static {
        try {
            sTexture = ImageIO.read(new File("textures/blocks/grass_block_side.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static BufferedImage tTexture;
    static {
        try {
            tTexture = ImageIO.read(new File("textures/blocks/grass_block_top.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Grass_Block(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    BufferedImage getBottomTexture() {
        return bTexture;
    }

    @Override
    BufferedImage getTopTexture() {
        return tTexture;
    }

    @Override
    BufferedImage getFrontTexture() {
        return sTexture;
    }

    @Override
    BufferedImage getBackTexture() {
        return sTexture;
    }

    @Override
    BufferedImage getLeftTexture() {
        return sTexture;
    }

    @Override
    BufferedImage getRightTexture() {
        return sTexture;
    }
}
