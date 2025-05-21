import java.awt.geom.Point2D;

public class Homography {
    public static double[][] computeHomography(Point2D[] src, Point2D[] dst) {
        double[][] a = new double[8][8];
        double[] b = new double[8];

        for (int i = 0; i < 4; i++) {
            double x = src[i].getX();
            double y = src[i].getY();
            double u = dst[i].getX();
            double v = dst[i].getY();

            a[i * 2] = new double[]{x, y, 1, 0, 0, 0, -u * x, -u * y};
            a[i * 2 + 1] = new double[]{0, 0, 0, x, y, 1, -v * x, -v * y};
            b[i * 2] = u;
            b[i * 2 + 1] = v;
        }

        // Solve Ax = b
        double[] h = gaussianElimination(a, b);
        return new double[][]{
                {h[0], h[1], h[2]},
                {h[3], h[4], h[5]},
                {h[6], h[7], 1}
        };
    }

    private static double[] gaussianElimination(double[][] A, double[] b) {
        int n = b.length;
        for (int p = 0; p < n; p++) {
            int max = p;
            for (int i = p + 1; i < n; i++)
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) max = i;

            double[] temp = A[p]; A[p] = A[max]; A[max] = temp;
            double t = b[p]; b[p] = b[max]; b[max] = t;

            for (int i = p + 1; i < n; i++) {
                double alpha = A[i][p] / A[p][p];
                b[i] -= alpha * b[p];
                for (int j = p; j < n; j++) A[i][j] -= alpha * A[p][j];
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = b[i];
            for (int j = i + 1; j < n; j++) sum -= A[i][j] * x[j];
            x[i] = sum / A[i][i];
        }
        return x;
    }
}//end class

