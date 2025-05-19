import java.awt.geom.AffineTransform;

public class TransformUtils {
    /**
     * Computes an AffineTransform that maps a rectangle (src) to a quadrilateral (dst)
     * Only works well when the quad is not heavily skewed.
     */
    public static AffineTransform getTextureTransform(
            double[] srcPts, // 8 values: x0, y0, x1, y1, x2, y2, x3, y3
            double[] dstPts  // 8 values: x0, y0, x1, y1, x2, y2, x3, y3
    ) {
        // AffineTransform requires only 3 points: map (0,0)->(x0,y0), (1,0)->(x1,y1), (0,1)->(x3,y3)
        // So we’ll base this on upper-left (0,0), upper-right (W,0), lower-left (0,H)

        double sx0 = srcPts[0], sy0 = srcPts[1];
        double sx1 = srcPts[2], sy1 = srcPts[3];
        double sx3 = srcPts[6], sy3 = srcPts[7];

        double dx0 = dstPts[0], dy0 = dstPts[1];
        double dx1 = dstPts[2], dy1 = dstPts[3];
        double dx3 = dstPts[6], dy3 = dstPts[7];

        // Solve for transform that maps src basis vectors to dst
        double scaleX = dx1 - dx0;
        double shearX = dx3 - dx0;
        double scaleY = dy1 - dy0;
        double shearY = dy3 - dy0;

        AffineTransform at = new AffineTransform(
                scaleX, scaleY,  // m00, m10
                shearX, shearY,  // m01, m11
                dx0, dy0         // m02, m12 (translate)
        );

        // Scale it so that it maps image coordinates (e.g., 0→width, 0→height)
        double iw = sx1 - sx0; // Image width
        double ih = sy3 - sy0; // Image height

        try {
            AffineTransform inv = new AffineTransform();
            inv.scale(iw, ih);
            inv.invert();
            at.preConcatenate(inv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return at;
    }
}
