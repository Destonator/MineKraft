public class Grass_Block extends Block {

    public Grass_Block(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    int getBottomTexture() {
        return 0;
    }

    @Override
    int getTopTexture() {
        return 2;
    }

    @Override
    int getFrontTexture() {
        return 1;
    }

    @Override
    int getBackTexture() {
        return 1;
    }

    @Override
    int getLeftTexture() {
        return 1;
    }

    @Override
    int getRightTexture() {
        return 1;
    }
}
