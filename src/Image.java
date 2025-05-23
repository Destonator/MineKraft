import java.awt.*;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

public class Image {
    static java.util.List<Polygon> quads = new java.util.ArrayList<>();
    Color[] cachedColors = new Color[256];

    public Image(Integer textureId) {
        cachedColors = Textures.cachedColors[textureId];
    }

    void drawImage(Graphics g, Polygon p) {
        generateGrid(p, 16);

        for (int i = 0; i < quads.size(); i++) {
            Polygon poly = quads.get(i);
            Color c = cachedColors[i];

            g.setColor(c);
            g.fillPolygon(poly);
        }

        //g.setColor(Color.black);
        //g.drawPolygon(p);
    }//end drawPolygon

    public static void generateGrid(Polygon outer, int tilesPerSide) {
        quads.clear();

        Point p0 = new Point(outer.xpoints[0], outer.ypoints[0]); // top-left
        Point p1 = new Point(outer.xpoints[1], outer.ypoints[1]); // bottom-left
        Point p2 = new Point(outer.xpoints[2], outer.ypoints[2]); // bottom-right
        Point p3 = new Point(outer.xpoints[3], outer.ypoints[3]); // top-right

        for (int y = 0; y < tilesPerSide; y++) {
            double ty = (double) y / tilesPerSide;
            double ty2 = (double) (y + 1) / tilesPerSide;

            for (int x = 0; x < tilesPerSide; x++) {
                double tx = (double) x / tilesPerSide;
                double tx2 = (double) (x + 1) / tilesPerSide;

                Point a = interpolate(p0, p3, tx);
                Point b = interpolate(p1, p2, tx);
                Point c = interpolate(p0, p3, tx2);
                Point d = interpolate(p1, p2, tx2);

                Point topLeft = interpolate(a, b, ty);
                Point topRight = interpolate(c, d, ty);
                Point bottomLeft = interpolate(a, b, ty2);
                Point bottomRight = interpolate(c, d, ty2);

                Polygon quad = new Polygon();
                quad.addPoint(topLeft.x, topLeft.y);
                quad.addPoint(bottomLeft.x, bottomLeft.y);
                quad.addPoint(bottomRight.x, bottomRight.y);
                quad.addPoint(topRight.x, topRight.y);

                quads.add(quad);
            }
        }
    }//end generateGrid

    private static Point interpolate(Point a, Point b, double t) {
        return new Point((int)(a.x + t * (b.x - a.x)), (int)(a.y + t * (b.y - a.y)));
    }//end Interpolate
}//end Image class
