package july;

/**
 * Input    AND     OR      XOR     NOR     NAND    XNOR    X-exclusive, if both -> 0
 *  00      0       0       0       1         1       1
 *  01      0       1       1       0         1       0
 *  10      0       1       1       0         1       0
 *  11      1       1       0       0         0       1
 */
public class App 
{
    public static void main(String[] args) {
        /** input */
        double[] x = {0, 1};

        /** weigh */
        double[] w = {0.5, 0.5};

        /** bias, оно же смещение */
        double b = 0.5;

    }

    public static double neuron (double [] x, double [] w, double b){

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
