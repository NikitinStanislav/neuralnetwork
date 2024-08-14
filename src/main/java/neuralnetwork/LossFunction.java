package neuralnetwork;

import matrix.Matrix;

public class LossFunction {

    public static Matrix crossEntropy(Matrix expected, Matrix actual){
        return actual.apply((index, value) -> {
            return -expected.get(index) * Math.log(value);   //there is no log2 in Java, but log (which is log e) works the same
        }).sumColumns();
    }
}
