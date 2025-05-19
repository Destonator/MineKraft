import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image {
    static BufferedImage image;

    static {
        try {
            image = ImageIO.read(new File("textures/blocks/dirt.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //java.awt.Polygon p;
    static java.util.List<Polygon> quads = new java.util.ArrayList<>();

    Color[] cachedColors = new Color[256];


    public Image() {
        for (int i = 0; i < 256; i++) {
            int x = i % image.getWidth();
            int y = i / image.getWidth();
            cachedColors[i] = new Color(image.getRGB(x, y));
        }
    }

    void drawImage(Graphics g, Polygon p) {

        quads.clear();
        recursiveSubdivide(p, 4); // 4 levels → 16 x 16

        for (int i = 0; i < quads.size(); i++) {
            Polygon poly = quads.get(i);
            Color c = cachedColors[(i % image.getWidth()) + (image.getWidth() * ((i / image.getWidth()) % image.getHeight()))];
            g.setColor(c);
            g.fillPolygon(poly);
        }

        g.setColor(Color.black);
        g.drawPolygon(p);

    }//end drawPolygon

    private static Point midpoint(Point a, Point b) {
        return new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
    }

    public static void recursiveSubdivide(Polygon p, int depth) {
        if (depth == 0) {
            quads.add(p);
            return;
        }

        Point p0 = new Point(p.xpoints[0], p.ypoints[0]); // top-left
        Point p1 = new Point(p.xpoints[1], p.ypoints[1]); // bottom-left
        Point p2 = new Point(p.xpoints[2], p.ypoints[2]); // bottom-right
        Point p3 = new Point(p.xpoints[3], p.ypoints[3]); // top-right

        Point m01 = midpoint(p0, p1);
        Point m12 = midpoint(p1, p2);
        Point m23 = midpoint(p2, p3);
        Point m30 = midpoint(p3, p0);
        Point center = midpoint(m01, m23);

        // 4 smaller quads
        Polygon tl = new Polygon();
        tl.addPoint(p0.x, p0.y);
        tl.addPoint(m01.x, m01.y);
        tl.addPoint(center.x, center.y);
        tl.addPoint(m30.x, m30.y);

        Polygon bl = new Polygon();
        bl.addPoint(m01.x, m01.y);
        bl.addPoint(p1.x, p1.y);
        bl.addPoint(m12.x, m12.y);
        bl.addPoint(center.x, center.y);

        Polygon br = new Polygon();
        br.addPoint(center.x, center.y);
        br.addPoint(m12.x, m12.y);
        br.addPoint(p2.x, p2.y);
        br.addPoint(m23.x, m23.y);

        Polygon tr = new Polygon();
        tr.addPoint(m30.x, m30.y);
        tr.addPoint(center.x, center.y);
        tr.addPoint(m23.x, m23.y);
        tr.addPoint(p3.x, p3.y);

        // Recurse
        recursiveSubdivide(tl, depth - 1);
        recursiveSubdivide(bl, depth - 1);
        recursiveSubdivide(br, depth - 1);
        recursiveSubdivide(tr, depth - 1);
    }

}//end Image class
