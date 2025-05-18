import java.awt.*;

public class Polygon {
    java.awt.Polygon p;
    Color c;
    boolean draw = true;

    public Polygon(double[] x, double[] y, Color c) {
        p = new java.awt.Polygon();
        for (int i = 0; i < x.length; i++) {
            p.addPoint((int)x[i], (int)y[i]);
        }
        this.c = c;
    }

    void updatePolygon(double[] x, double[] y) {
        p.reset();
        for(int i = 0; i<x.length; i++)
        {
            p.xpoints[i] = (int) x[i];
            p.ypoints[i] = (int) y[i];
            p.npoints = x.length;
        }

    }

    void drawPolygon(Graphics g) {
        if (draw) {
            Image texture = new Image(p);
            texture.drawImage(g);

            if(Screen.polygonOver == this){
                g.setColor(new Color(255, 255, 255, 100));
                g.fillPolygon(p);
            }
        }
    }//end drawPolygon

    boolean MouseOver(){
        return p.contains(NewWindow.screenSize.getWidth()/2, NewWindow.screenSize.getHeight()/2);
    }
}//end Polygon Class
