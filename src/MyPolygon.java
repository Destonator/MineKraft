import java.awt.*;
import java.awt.image.BufferedImage;

public class MyPolygon {
    BufferedImage image;
    Polygon p;
    boolean draw = true;

    public MyPolygon(double[] x, double[] y, BufferedImage image) {
        p = new Polygon();
        for (int i = 0; i < x.length; i++) {
            p.addPoint((int)x[i], (int)y[i]);
        }
        this.image = image;
    }

    void updatePolygon(double[] x, double[] y) {
        p.reset();
        for(int i = 0; i<x.length; i++)
        {
            p.xpoints[i] = (int) x[i];
            p.ypoints[i] = (int) y[i];
            p.npoints = x.length;
        }
    }//en updatePolygon

    void drawPolygon(Graphics g) {
        if (draw) {
            Image texture = new Image(image);
            texture.drawImage(g, p);
            if(Screen.polygonOver == this){
                g.setColor(new Color(255, 255, 255, 100));
                g.fillPolygon(p);
            }
        }
    }//end drawPolygon

    boolean MouseOver(){
        return p.contains(NewWindow.screenSize.getWidth()/2, NewWindow.screenSize.getHeight()/2);
    }//end mouseOver
}//end Polygon Class
