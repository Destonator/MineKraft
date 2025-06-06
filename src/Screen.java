import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Screen extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

    static ArrayList<DPolygon> DPolygons = new ArrayList<DPolygon>();
    static ArrayList<DPolygon> RenderPolygons = new ArrayList<DPolygon>();

    static MyPolygon polygonOver = null;
    static Block blockOver = null;
    static int blockOverSide = 0;

    static double[] ViewFrom = new double[] {-5,0,6};
    static double[] ViewTo =  new double[] {0,0,6};

    int[] NewOrder;

    Robot r;//to keep mouse centered

    double drawFPS = 0, MaxFPS = 60, SleepTime = 1000.0/MaxFPS, LastRefresh = 0, StartTime = System.currentTimeMillis(), LastFPSCheck = 0, Checks = 0, deltaTime = 0;
    //Higher HorRot&VertRot means slower speed
    double VertLook = 0, HorLook = 0, aimSight = 7, HorRotSpeed = 190, VertRotSpeed = 500;
    static double moveConstantSpeed = 2, zoom = 250, MouseX = 0, MouseY = 0;

    boolean[] Keys = new boolean[10];
    boolean centeringMouse = true;
    boolean needsUpdate = true;

    long lastTime = System.currentTimeMillis();

    public Screen() {
        Textures textureInfo = new Textures();
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
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastTime) / 1_000_000_000.0; // seconds
        lastTime = currentTime;

        Controls();

        g.clearRect(0, 0, getWidth(), getHeight());
        if (needsUpdate) {
            Calculator.SetPrederterminedInfo();

            for (DPolygon dPolygon : DPolygons) {//updates polygons
                dPolygon.updatePolygon();
            }
            setRenderPolygons();
            setOrder();
            setPolygonOver();
        }

        for(int i = 0; i < NewOrder.length; i++) {//draws polygon
            RenderPolygons.get(NewOrder[i]).GetDrawabePolygon().drawPolygon(g);
        }
        //debug
        g.setColor(new Color(255, 255, 255, 100));
        g.fillRect(0, 0, 250, 170);
        g.setColor(Color.BLACK);
        g.drawString("FPS: " + (int)drawFPS + " (Benchmark)", 20, 20);
        g.drawString("View From: " +
                String.format("%.2f", ViewFrom[0]) + ", " +
                String.format("%.2f", ViewFrom[1]) + ", " +
                String.format("%.2f", ViewFrom[2]), 20, 40);
        g.drawString("View To: " +
                String.format("%.2f", ViewTo[0]) + ", " +
                String.format("%.2f", ViewTo[1]) + ", " +
                String.format("%.2f", ViewTo[2]), 20, 60);
        g.drawString("Vert Look: " + VertLook, 20, 80);
        g.drawString("Hor Look: " + HorLook, 20, 100);

        g.drawString("Blocks: " + World.wBlocks.size(), 20, 120);
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
        if(needsUpdate) {
            RenderPolygons.clear();
            for (DPolygon dPolygon : DPolygons) {
                if (dPolygon.side == 1) {
                    if (ViewFrom[2] < dPolygon.parentBlock.getZ()) {
                        RenderPolygons.add(dPolygon);
                    }
                } else if (dPolygon.side == 2) {
                    if (ViewFrom[2] > dPolygon.parentBlock.getZ()) {
                        RenderPolygons.add(dPolygon);
                    }
                } else if (dPolygon.side == 3) {
                    if (ViewFrom[0] < dPolygon.parentBlock.getX()) {
                        RenderPolygons.add(dPolygon);
                    }
                } else if (dPolygon.side == 4) {
                    if (ViewFrom[0] > dPolygon.parentBlock.getX()) {
                        RenderPolygons.add(dPolygon);
                    }
                } else if (dPolygon.side == 5) {
                    if (ViewFrom[1] > dPolygon.parentBlock.getY()) {
                        RenderPolygons.add(dPolygon);
                    }
                } else if (dPolygon.side == 6) {
                    if (ViewFrom[1] < dPolygon.parentBlock.getY()) {
                        RenderPolygons.add(dPolygon);
                    }
                } else {
                    RenderPolygons.add(dPolygon);
                }
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
    }//end invisibleMouse

    void drawMouseAim(Graphics g) {
        g.setColor(Color.black);
        g.drawLine((int)(NewWindow.screenSize.getWidth()/2 - aimSight), (int)(NewWindow.screenSize.getHeight()/2), (int)(NewWindow.screenSize.getWidth()/2 + aimSight), (int)(NewWindow.screenSize.getHeight()/2));
        g.drawLine((int)(NewWindow.screenSize.getWidth()/2), (int)(NewWindow.screenSize.getHeight()/2 - aimSight), (int)(NewWindow.screenSize.getWidth()/2), (int)(NewWindow.screenSize.getHeight()/2 + aimSight));
    }//end drawMouseAim


void SleepAndRefresh()
{
    long timeSLU = (long) (System.currentTimeMillis() - LastRefresh);

    Checks ++;
    if(Checks >= 15)
    {
        drawFPS = Checks/((System.currentTimeMillis() - LastFPSCheck)/1000.0);
        LastFPSCheck = System.currentTimeMillis();
        Checks = 0;
    }

    if(timeSLU < 1000.0/MaxFPS)
    {
        try {
            Thread.sleep((long) (1000.0/MaxFPS - timeSLU));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    LastRefresh = System.currentTimeMillis();

    repaint();
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
        double moveSpeed = moveConstantSpeed*deltaTime;

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
            if(RenderPolygons.get(NewOrder[i]).GetDrawabePolygon().MouseOver() && RenderPolygons.get(NewOrder[i]).draw) {
                polygonOver = RenderPolygons.get(NewOrder[i]).GetDrawabePolygon();
                blockOver = RenderPolygons.get(NewOrder[i]).GetParentBlock();
                blockOverSide = 0;
                for(int j = 0; j < 6; j++) {
                    blockOverSide++;
                    if(blockOver.p[j] == RenderPolygons.get(NewOrder[i])){
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
        VertLook += (difY / VertRotSpeed) * deltaTime;
        HorLook -= (difX / HorRotSpeed) * deltaTime;

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
    }//end MouseMovement

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
    }//end updateView

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
    }//end CenterMouse

    @Override
    public void keyPressed(KeyEvent e) {
        needsUpdate = true;
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
        needsUpdate = true;
        if(e.getButton() == MouseEvent.BUTTON1) {
            if(blockOver != null) {
                World.removeBlock(blockOver);
            }
        }//end removeBlock
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
        needsUpdate = true;
        MouseMovement(e.getX(), e.getY());
        MouseX = e.getX();
        MouseY = e.getY();
        CenterMouse();
    }
}
