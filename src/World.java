import java.util.ArrayList;

public class World {
    static ArrayList<Block> wBlocks = new ArrayList<Block>();
    static void Generate(){
        for(int i = 0;i <8;i++) {//temporary floor
            for(int j = 0;j <8;j++) {
                for(int k = 0;k <1;k++) {
                    wBlocks.add(new Stone_Block(i,j,k));
                }
            }
        }
        for(int i = 0;i <8;i++) {//temporary floor
            for(int j = 0;j <8;j++) {
                for(int k = 1;k <2;k++) {
                    wBlocks.add(new Dirt_Block(i,j,k));
                }
            }
        }
        for(int i = 0;i <8;i++) {//temporary floor
            for(int j = 0;j <8;j++) {
                for(int k = 2;k <3;k++) {
                    wBlocks.add(new Grass_Block(i,j,k));
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
    }//end hasBlockAt

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
    }//end removeBlock

    public static void addBlock(int x, int y, int z){
        Block b = new Dirt_Block(x,y,z);
        wBlocks.add(b);
        b.updateBlock();
        updateSurrounding(b);
    }//end addBlock

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
    }//end updateSurrounding
}//end World class
