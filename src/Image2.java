import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

public class Image2 {
    static BufferedImage src;
    public Image2(BufferedImage src) {
        this.src = src;
    }

    public void drawImage(Graphics g, Polygon dest) {
        Point2D[] destPoints = new Point2D[dest.npoints];
        destPoints[0] = new Point2D.Double(dest.xpoints[0], dest.ypoints[0]);
        destPoints[1] = new Point2D.Double(dest.xpoints[1], dest.ypoints[1]);
        destPoints[2] = new Point2D.Double(dest.xpoints[2], dest.ypoints[2]);
        destPoints[3] = new Point2D.Double(dest.xpoints[3], dest.ypoints[3]);

        int width = src.getWidth();
        int height = src.getHeight();

        // Source rectangle (image corners)
        Point2D[] srcPoints = {
                new Point2D.Double(-0.5, -0.5),
                new Point2D.Double(width - 0.5, -0.5),
                new Point2D.Double(width - 0.5, height - 0.5),
                new Point2D.Double(-0.5, height - 0.5),
        };

        // Compute forward homography (source → dest)
        double[][] H = Homography.computeHomography(srcPoints, destPoints);

        // Invert homography for dest → source mapping
        double[][] Hinv = invertHomography(H);

        // Get bounding box of the destination quad
        Rectangle bounds = computeBoundingBox(destPoints);

        BufferedImage output = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < bounds.height; y++) {
            for (int x = 0; x < bounds.width; x++) {
                // Map (x + bounds.x, y + bounds.y) to source using Hinv
                double[] srcCoord = applyHomography(Hinv, x + bounds.x, y + bounds.y);

                int sx = (int) Math.round(srcCoord[0]);
                int sy = (int) Math.round(srcCoord[1]);

                if (sx >= 0 && sy >= 0 && sx < width && sy < height) {
                    int rgb = src.getRGB(sx, sy);
                    output.setRGB(x, y, rgb);
                }
            }
        }

        g.drawImage(output, bounds.x, bounds.y, null);
        g.drawPolygon(dest);
    }

    private static double[] applyHomography(double[][] H, double x, double y) {
        double denom = H[2][0] * x + H[2][1] * y + H[2][2];
        double u = (H[0][0] * x + H[0][1] * y + H[0][2]) / denom;
        double v = (H[1][0] * x + H[1][1] * y + H[1][2]) / denom;
        return new double[]{u, v};
    }

    private static Rectangle computeBoundingBox(Point2D[] points) {
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;

        for (Point2D p : points) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
        }

        int x = (int) Math.floor(minX);
        int y = (int) Math.floor(minY);
        int w = (int) Math.ceil(maxX - minX);
        int h = (int) Math.ceil(maxY - minY);

        if(w <= 0 || h <=0 || w > 10000 || h > 10000) {
            return new Rectangle(0, 0, 1, 1);
        }

        return new Rectangle(x, y, w, h);
    }

    private static double[][] invertHomography(double[][] H) {
        // Use matrix inversion via cofactors and determinant
        double[][] inv = new double[3][3];
        double det = H[0][0] * (H[1][1] * H[2][2] - H[2][1] * H[1][2]) -
                H[0][1] * (H[1][0] * H[2][2] - H[2][0] * H[1][2]) +
                H[0][2] * (H[1][0] * H[2][1] - H[2][0] * H[1][1]);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int i1 = (i + 1) % 3, i2 = (i + 2) % 3;
                int j1 = (j + 1) % 3, j2 = (j + 2) % 3;
                inv[j][i] = (H[i1][j1] * H[i2][j2] - H[i1][j2] * H[i2][j1]) / det;
            }
        }
        return inv;
    }
}//end class
