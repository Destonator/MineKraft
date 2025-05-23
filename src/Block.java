import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Block {
    int x,y,z;
    DPolygon[] p = new DPolygon[6];
    public Block(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void updateBlock(){
        for (DPolygon dPolygon : p) {
            Screen.DPolygons.remove(dPolygon);
        }
        if(!World.HasBlockAt(x, y, z-1)) {
            p[0] = new DPolygon(new double[]{x, x + 1, x + 1, x}, new double[]{y, y, y + 1, y + 1}, new double[]{z, z, z, z}, this, 1, getBottomTexture());//bottom
            Screen.DPolygons.add(p[0]);
        }
        if(!World.HasBlockAt(x, y, z+1)) {
            p[1] = new DPolygon(new double[]{x, x + 1, x + 1, x}, new double[]{y, y, y + 1, y + 1}, new double[]{z + 1, z + 1, z + 1, z + 1}, this, 2, getTopTexture());//top
            Screen.DPolygons.add(p[1]);
        }
        if(!World.HasBlockAt(x-1, y, z)) {
            p[2] = new DPolygon(new double[]{x, x, x, x}, new double[]{y, y + 1, y + 1, y}, new double[]{z, z, z + 1, z + 1}, this, 3, getFrontTexture());//front
            Screen.DPolygons.add(p[2]);
        }
        if(!World.HasBlockAt(x+1, y, z)) {
            p[3] = new DPolygon(new double[]{x + 1, x + 1, x + 1, x + 1}, new double[]{y, y + 1, y + 1, y}, new double[]{z, z, z + 1, z + 1}, this, 4, getBackTexture());//back
            Screen.DPolygons.add(p[3]);
        }
        if(!World.HasBlockAt(x, y+1, z)) {
            p[4] = new DPolygon(new double[]{x, x + 1, x + 1, x}, new double[]{y + 1, y + 1, y + 1, y + 1}, new double[]{z, z, z + 1, z + 1}, this, 5, getLeftTexture());//left
            Screen.DPolygons.add(p[4]);
        }
        if(!World.HasBlockAt(x, y-1, z)){
            p[5] = new DPolygon(new double[]{x,x+1,x+1,x}, new double[]{y,y,y,y},new double[]{z,z,z+1,z+1}, this, 6, getRightTexture());//right
            Screen.DPolygons.add(p[5]);
        }
    }//end UpdateBlock

    abstract int getBottomTexture();
    abstract int getTopTexture();
    abstract int getFrontTexture();
    abstract int getBackTexture();
    abstract int getLeftTexture();
    abstract int getRightTexture();

    public void remove(){
        for (DPolygon dPolygon : p) {
            Screen.DPolygons.remove(dPolygon);
        }
        World.wBlocks.remove(this);
    }//end remove

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getZ() {
        return z;
    }
}//end block class
