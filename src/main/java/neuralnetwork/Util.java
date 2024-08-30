package neuralnetwork;

import matrix.Matrix;

import java.util.Random;

public class Util {

    private static Random random = new Random();

    private static Matrix generateMatrix(int rows, int columns){
        return new Matrix(rows, columns, i -> random.nextGaussian());
    }

    public static Matrix generateInputMatrix(int rows, int columns) {

        return generateMatrix(rows, columns);
    }

    public static Matrix generateExpectedMatrix(int rows, int columns) {

        Matrix expected = new Matrix(rows, columns, i -> 0);

        for (int i = 0; i < columns; i++) {
            expected.set(random.nextInt(rows), i, 1);
        }

        return expected;
    }

    public static Matrix generateWeights(int rows, int columns){
        return generateMatrix(rows, columns);
    }

    public static Matrix generateBiases(int rows){
        return generateMatrix(rows, 1);
    }
}
