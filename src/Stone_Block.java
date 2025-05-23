public class Stone_Block extends Block {
    public Stone_Block(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    int getBottomTexture() {
        return 3;
    }

    @Override
    int getTopTexture() {
        return 3;
    }

    @Override
    int getFrontTexture() {
        return 3;
    }

    @Override
    int getBackTexture() {
        return 3;
    }

    @Override
    int getLeftTexture() {
        return 3;
    }

    @Override
    int getRightTexture() {
        return 3;
    }
}
