package july;

/**
 * Input    AND     OR      XOR     NOR     NAND    XNOR    X-exclusive, if both -> 0  .
 * 00      0       0       0       1         1       1
 * 01      0       1       1       0         1       0
 * 10      0       1       1       0         1       0
 * 11      1       1       0       0         0       1
 */
public class App {
    public static void main(String[] args) {

        for (int i = 0; i < 4; i++) {
            double x1 = i / 2;
            double x2 = i % 2;

            double output = xnor(x1, x2);

            System.out.printf("%d%d\t%d\n", (int) x1, (int) x2, (int) output);
        }

    }

    static double and(double x1, double x2) {
        return neuron(new double[]{x1, x2}, new double[]{1, 1}, -1);
    }

    static double or(double x1, double x2) {
        return neuron(new double[]{x1, x2}, new double[]{1, 1}, 0);
    }

    static double nor(double x1, double x2) {
        return neuron(new double[]{x1, x2}, new double[]{-1, -1}, 1);
    }

    static double nand(double x1, double x2) {
        return neuron(new double[]{x1, x2}, new double[]{-1, -1}, 2);
    }

    static double xor(double x1, double x2) {
        return and(or(x1, x2), nand(x1, x2));
    }

    static double xnor(double x1, double x2) {
        return or(and(x1, x2), nor(x1, x2));
    }

    public static double neuron(double[] x, double[] w, double b) {

        /** weighted sum */
        double z = 0.0;

        for (int i = 0; i < x.length; i++) {
            z += x[i] * w[i];
        }

        z += b;

        /** activation function (если правильно понял, то это как раз R-элемент */
        return z > 0 ? 1.0 : 0.0;
    }
}
