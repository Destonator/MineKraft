import java.awt.*;
import java.util.ArrayList;

public class World {
    static ArrayList<Block> wBlocks = new ArrayList<Block>();
    static void Generate(){
        for(int i = 0;i <16;i++) {//temporary floor
            for(int j = 0;j <16;j++) {
                for(int k = 0;k <2;k++) {
                    wBlocks.add(new Block(i,j,k));
                }
            }
        }
        for(Block b : wBlocks) {
            b.updateBlock();
        }
    }//end generate

    public static boolean HasBlockAt(int x, int y, int z){
        for(Block b : wBlocks){
            if(b.getX() == x && b.getY() == y && b.getZ() == z){
                return true;
            }
        }
        return false;
    }

    public static Block GetBlockAt(int x, int y, int z){
        for(Block b : wBlocks){
            if(b.getX() == x && b.getY() == y && b.getZ() == z){
                return b;
            }
        }
        return null;
    }//end GetBlockAt

    public static void removeBlock(Block b){
        b.remove();
        updateSurrounding(b);
    }

    public static void addBlock(int x, int y, int z){
        Block b = new Block(x,y,z);
        wBlocks.add(b);
        b.updateBlock();
        updateSurrounding(b);
    }

    static void updateSurrounding(Block b){
        if (HasBlockAt(b.x +1, b.y, b.z)) {
            GetBlockAt(b.x + 1, b.y, b.z).updateBlock();
        }
        if (HasBlockAt(b.x -1, b.y, b.z)) {
            GetBlockAt(b.x -1, b.y, b.z).updateBlock();
        }
        if (HasBlockAt(b.x, b.y + 1, b.z)) {
            GetBlockAt(b.x , b.y +1, b.z).updateBlock();
        }
        if (HasBlockAt(b.x, b.y -1, b.z)) {
            GetBlockAt(b.x , b.y -1, b.z).updateBlock();
        }
        if (HasBlockAt(b.x, b.y, b.z +1)) {
            GetBlockAt(b.x, b.y, b.z +1).updateBlock();
        }
        if (HasBlockAt(b.x, b.y, b.z-1)) {
            GetBlockAt(b.x , b.y, b.z-1).updateBlock();
        }
    }
}//end World class
