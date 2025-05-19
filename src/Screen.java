import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Screen extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    static ArrayList<DPolygon> DPolygons = new ArrayList<DPolygon>();
    static ArrayList<DPolygon> RenderPolygons = new ArrayList<DPolygon>();

    double SleepTime = 1000.0/30.0;//30 frames per second
    double LastRefresh = 0.0;

    static Polygon polygonOver = null;
    static Block blockOver = null;
    static int blockOverSide = 0;

    static double[] ViewFrom = new double[] {-5,0,2};
    static double[] ViewTo =  new double[] {0,0,2};

    int[] NewOrder;

    Robot r;//to keep mouse centered

    //Higher HorRot&VertRot means slower speed
    double VertLook = 0, HorLook = 0, aimSight = 7, HorRotSpeed = 1900, VertRotSpeed = 5000;
    static double moveSpeed = 0.1, zoom = 250, MouseX = 0, MouseY = 0;

    boolean[] Keys = new boolean[10];
    boolean centeringMouse = true;

    public Screen() {
        World.Generate();

        addKeyListener(this);
        setFocusable(true);

        addMouseListener(this);
        addMouseMotionListener(this);

        invisibleMouse();
        SwingUtilities.invokeLater(() -> CenterMouse());
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Controls();
        g.clearRect(0, 0, getWidth(), getHeight());

        Calculator.SetPrederterminedInfo();

        for (DPolygon dPolygon : DPolygons) {//updates polygons
            dPolygon.updatePolygon();
        }
        setRenderPolygons();
        setOrder();
        setPolygonOver();

        for(int i = 0; i < NewOrder.length; i++) {//draws polygon
            RenderPolygons.get(NewOrder[i]).GetDrawabePolygon().drawPolygon(g);
        }
        //debug
        g.setColor(new Color(255, 255, 255, 100));
        g.fillRect(0, 0, 250, 170);
        g.setColor(Color.BLACK);
        g.drawString(System.currentTimeMillis() + "", 20, 20);
        g.drawString("View From: " +
                String.format("%.2f", ViewFrom[0]) + ", " +
                String.format("%.2f", ViewFrom[1]) + ", " +
                String.format("%.2f", ViewFrom[2]), 20, 40);
        g.drawString("View To: " +
                String.format("%.2f", ViewTo[0]) + ", " +
                String.format("%.2f", ViewTo[1]) + ", " +
                String.format("%.2f", ViewTo[2]), 20, 60);
        g.drawString("Vert Look: " + VertLook, 20, 80);
        g.drawString("Blocks: " + World.wBlocks.size(), 20, 100);
        g.drawString("Hor Look: " + HorLook, 20, 120);
        g.drawString("Polgons: " + NewOrder.length, 20, 140);

        if(blockOver != null) {
            g.drawString("Looking at Block: " + blockOver.getX() + ", " + blockOver.getY() + ", " + blockOver.getZ() + " (" + blockOverSide + ")", 20, 160);
        }else{
            g.drawString("Looking at Block: null" , 20, 160);
        }

        drawMouseAim(g);
        SleepAndRefresh();
    }

    private void setRenderPolygons() {
        RenderPolygons.clear();
        for(DPolygon dPolygon : DPolygons) {
            if(dPolygon.side == 1){
                if(ViewFrom[2] < dPolygon.parentBlock.getZ()){
                    RenderPolygons.add(dPolygon);
                }
            }
            else if(dPolygon.side == 2){
                if(ViewFrom[2] > dPolygon.parentBlock.getZ()){
                    RenderPolygons.add(dPolygon);
                }
            }
            else if(dPolygon.side == 3){
                if(ViewFrom[0] < dPolygon.parentBlock.getX()){
                    RenderPolygons.add(dPolygon);
                }
            }
            else if(dPolygon.side == 4){
                if(ViewFrom[0] > dPolygon.parentBlock.getX()){
                    RenderPolygons.add(dPolygon);
                }
            }
            else if(dPolygon.side == 5){
                if(ViewFrom[1] > dPolygon.parentBlock.getY()){
                    RenderPolygons.add(dPolygon);
                }
            }
            else if(dPolygon.side == 6){
                if(ViewFrom[1] < dPolygon.parentBlock.getY()){
                    RenderPolygons.add(dPolygon);
                }
            }
            else{
                RenderPolygons.add(dPolygon);
            }
        }
    }//end renderPolygons

    void setOrder(){
        double[] k = new double[RenderPolygons.size()];
        NewOrder = new int[RenderPolygons.size()];
        for(int i = 0; i < RenderPolygons.size(); i++) {
            k[i] = RenderPolygons.get(i).AvgDist;
            NewOrder[i] = i;
        }

        double temp;
        int tempr;
        for(int a = 0; a < k.length - 1; a++) {
            for(int b = 0; b < k.length - 1; b++) {
                if(k[b] < k[b+1]) {
                    temp = k[b];
                    tempr = NewOrder[b];
                    NewOrder[b] = NewOrder[b+1];
                    k[b] = k[b+1];

                    NewOrder[b+1] = tempr;
                    k[b+1] = temp;
                }
            }
        }
    }//end setOrder

    void invisibleMouse() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
        Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, new Point(0,0), "InvisibleCursor");
        setCursor(invisibleCursor);
    }

    void drawMouseAim(Graphics g)
    {
        g.setColor(Color.black);
        g.drawLine((int)(NewWindow.screenSize.getWidth()/2 - aimSight), (int)(NewWindow.screenSize.getHeight()/2), (int)(NewWindow.screenSize.getWidth()/2 + aimSight), (int)(NewWindow.screenSize.getHeight()/2));
        g.drawLine((int)(NewWindow.screenSize.getWidth()/2), (int)(NewWindow.screenSize.getHeight()/2 - aimSight), (int)(NewWindow.screenSize.getWidth()/2), (int)(NewWindow.screenSize.getHeight()/2 + aimSight));
    }

    void SleepAndRefresh() {
        while(true) {
            if((System.currentTimeMillis() - LastRefresh) > SleepTime) {
                LastRefresh = System.currentTimeMillis();
                repaint();
                break;
            }
            else{
                try{
                    Thread.sleep((long)(SleepTime - (System.currentTimeMillis()-LastRefresh)));
                }catch(Exception e){

                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    void Controls(){
        Vector ViewVector = new Vector(ViewTo[0] - ViewFrom[0], ViewTo[1] - ViewFrom[1], ViewTo[2] - ViewFrom[2]);
        Vector VerticalVector = new Vector(0,0,1);
        Vector SideViewVector = VerticalVector.CrossProduct(ViewVector);
        double dx = ViewTo[0] - ViewFrom[0];
        double dy = ViewTo[1] - ViewFrom[1];
        double length = Math.sqrt(dx*dx + dy*dy);
        dx /= length;
        dy /= length;
        double sideX = -dy;
        double sideY = dx;

        if(Keys[4]) {//Move Forward
            ViewFrom[0] += moveSpeed* dx;
            ViewFrom[1] += moveSpeed* dy;
            ViewTo[0] += moveSpeed* dx;
            ViewTo[1] += moveSpeed* dy;
        }
        if(Keys[5]) {//Move Left
            ViewFrom[0] -= moveSpeed * sideX;
            ViewFrom[1] -= moveSpeed * sideY;
            ViewTo[0] -= moveSpeed * sideX;
            ViewTo[1] -= moveSpeed * sideY;
        }
        if(Keys[6]) {//Move Backward
            ViewFrom[0] -= moveSpeed* dx;
            ViewFrom[1] -= moveSpeed* dy;
            ViewTo[0] -= moveSpeed* dx;
            ViewTo[1] -= moveSpeed* dy;
        }
        if(Keys[7]) {//Move Right
            ViewFrom[0] += moveSpeed * sideX;
            ViewFrom[1] += moveSpeed * sideY;
            ViewTo[0] += moveSpeed * sideX;
            ViewTo[1] += moveSpeed * sideY;
        }
        if(Keys[8]) {//fly up
            ViewFrom[2] += moveSpeed;
            ViewTo[2] += moveSpeed;
        }
        if(Keys[9]) {//fly down
            ViewFrom[2] -= moveSpeed;
            ViewTo[2] -= moveSpeed;
        }
    }//end Controls

    void setPolygonOver(){
        polygonOver = null;
        blockOver = null;
        for (int i = NewOrder.length - 1; i >= 0; i--) {
            if(DPolygons.get(NewOrder[i]).GetDrawabePolygon().MouseOver() && DPolygons.get(NewOrder[i]).draw) {
                polygonOver = DPolygons.get(NewOrder[i]).GetDrawabePolygon();
                blockOver = DPolygons.get(NewOrder[i]).GetParentBlock();
                blockOverSide = 0;
                for(int j = 0; j < 6; j++) {
                    blockOverSide++;
                    if(blockOver.p[j] == DPolygons.get(NewOrder[i])){
                        break;
                    }
                }
                break;
            }
        }
    }//end SetPolygonOver

    void MouseMovement(double NewMouseX, double NewMouseY){
        double difX = NewWindow.screenSize.getWidth()/2 - NewMouseX;
        double difY = NewWindow.screenSize.getHeight()/2 - NewMouseY;
        difY *= 6 - Math.abs(VertLook) * 5;
        VertLook += difY / VertRotSpeed;
        HorLook -= difX / HorRotSpeed;

        if(VertLook>0.999){
            VertLook = 0.999;
        }
        if(VertLook<-0.999){
            VertLook = -0.999;
        }
        if(HorLook>0.999){
            HorLook -= 0.999;
        }
        if(HorLook<0){
            HorLook += 0.999;
        }
        updateView();
    }

    void updateView() {
        // Clamp vertical look between -89 and +89 degrees
        if (VertLook > 0.999) VertLook = 0.999;
        if (VertLook < -0.999) VertLook = -0.999;

        double pitch = Math.asin(VertLook);  // vertical angle in radians
        double yaw = HorLook * 2 * Math.PI;  // convert to radians (from normalized -1 to +1)

        double viewDistance = 5.0;

        double dx = Math.cos(pitch) * Math.cos(yaw);
        double dy = Math.cos(pitch) * Math.sin(yaw);
        double dz = Math.sin(pitch);

        ViewTo[0] = ViewFrom[0] + dx * viewDistance;
        ViewTo[1] = ViewFrom[1] + dy * viewDistance;
        ViewTo[2] = ViewFrom[2] + dz * viewDistance;
    }

    void CenterMouse(){
        if(r == null) {
            try {
                r = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
        Point panelPos = this.getLocationOnScreen();
        int centerX = panelPos.x + getWidth()/2;
        int centerY = panelPos.y + getHeight()/2;
        centeringMouse = true;
        r.mouseMove(centerX, centerY);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {Keys[0] = true;}
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {Keys[1] = true;}
        if(e.getKeyCode() == KeyEvent.VK_UP) {Keys[2] = true;}
        if(e.getKeyCode() == KeyEvent.VK_DOWN) {Keys[3] = true;}
        if(e.getKeyCode() == KeyEvent.VK_W) {Keys[4] = true;}
        if(e.getKeyCode() == KeyEvent.VK_A) {Keys[5] = true;}
        if(e.getKeyCode() == KeyEvent.VK_S) {Keys[6] = true;}
        if(e.getKeyCode() == KeyEvent.VK_D) {Keys[7] = true;}
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {Keys[8] = true;}
        if(e.getKeyCode() == KeyEvent.VK_SHIFT) {Keys[9] = true;}
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_LEFT) {Keys[0] = false;}
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {Keys[1] = false;}
        if(e.getKeyCode() == KeyEvent.VK_UP) {Keys[2] = false;}
        if(e.getKeyCode() == KeyEvent.VK_DOWN) {Keys[3] = false;}
        if(e.getKeyCode() == KeyEvent.VK_W) {Keys[4] = false;}
        if(e.getKeyCode() == KeyEvent.VK_A) {Keys[5] = false;}
        if(e.getKeyCode() == KeyEvent.VK_S) {Keys[6] = false;}
        if(e.getKeyCode() == KeyEvent.VK_D) {Keys[7] = false;}
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {Keys[8] = false;}
        if(e.getKeyCode() == KeyEvent.VK_SHIFT) {Keys[9] = false;}
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            if(blockOver != null) {
                World.removeBlock(blockOver);
            }
        }
        if(e.getButton() == MouseEvent.BUTTON3) {
            if(blockOver != null) {
                if(blockOverSide == 1){
                    World.addBlock(blockOver.getX(), blockOver.getY(), blockOver.getZ() - 1);
                }
                if(blockOverSide == 2){
                    World.addBlock(blockOver.getX(), blockOver.getY(), blockOver.getZ() + 1);
                }
                if(blockOverSide == 3){
                    World.addBlock(blockOver.getX() - 1, blockOver.getY(), blockOver.getZ());
                }
                if(blockOverSide == 4){
                    World.addBlock(blockOver.getX() + 1, blockOver.getY(), blockOver.getZ());
                }
                if(blockOverSide == 5){
                    World.addBlock(blockOver.getX(), blockOver.getY() + 1, blockOver.getZ());
                }
                if(blockOverSide == 6){
                    World.addBlock(blockOver.getX(), blockOver.getY() - 1, blockOver.getZ());

                }
            }
        }//end place block
    }//end mouseClicked

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(centeringMouse){
            centeringMouse = false;
            return;
        }
        MouseMovement(e.getX(), e.getY());
        MouseX = e.getX();
        MouseY = e.getY();
        CenterMouse();
    }
}
