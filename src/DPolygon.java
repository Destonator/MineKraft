import java.awt.*;

public class DPolygon {
    Color c;
    double[] x, y, z;
    boolean draw = true;
    double[] CalcPos, newX, newY;
    Polygon DrawabePolygon;
    double AvgDist;
    public Block parentBlock;

    public DPolygon(double[] x, double[] y, double[]z, Color c, Block parentBlock) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.c = c;
        this.parentBlock = parentBlock;
        createPolygon();
    }

    void createPolygon() {
        DrawabePolygon = new Polygon(new double[x.length], new double[x.length], c);
    }

    void updatePolygon() {
        newX = new double[x.length];
        newY = new double[x.length];
        draw = true;
        for (int i = 0; i < x.length; i++) {
            CalcPos = Calculator.CalculatePositionP(Screen.ViewFrom, Screen.ViewTo, x[i], y[i], z[i]);
            newX[i] = (NewWindow.screenSize.getWidth()/2 - Calculator.CalcFocusPos[0]) + CalcPos[0]* Screen.zoom;
            newY[i] = (NewWindow.screenSize.getHeight()/2 - Calculator.CalcFocusPos[1]) + CalcPos[1]* Screen.zoom;
            if(Calculator.t < 0)
                draw = false;
        }
        DrawabePolygon.draw = draw;
        DrawabePolygon.updatePolygon(newX, newY);
        AvgDist = GetDist();
    }

    double GetDist() {
        double total = 0.0;
        for (int i = 0; i < x.length; i++) {
            total += (GetDistanceToP(i));
        }
        return total/x.length;
    }

    private double GetDistanceToP(int i) {
        return Math.sqrt((Screen.ViewFrom[0] -x[i]) * (Screen.ViewFrom[0] -x[i]) +
                (Screen.ViewFrom[1] -y[i]) * (Screen.ViewFrom[1] -y[i]) +
                (Screen.ViewFrom[2] -z[i]) * (Screen.ViewFrom[2] -z[i]));
    }

    public Polygon GetDrawabePolygon() {
        return DrawabePolygon;
    }

    public Block GetParentBlock() {
        return parentBlock;
    }
}//end class
