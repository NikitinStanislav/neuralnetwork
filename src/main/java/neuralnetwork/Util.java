package neuralnetwork;

import matrix.Matrix;

import java.util.Random;

public class Util {

    private static Random random = new Random();

    public static Matrix generateInputMatrix(int rows, int columns) {

        return new Matrix(rows, columns, i -> random.nextGaussian());
    }

    public static Matrix generateExpectedMatrix(int rows, int columns) {

        Matrix expected = new Matrix(rows, columns, i -> 0);

        for (int i = 0; i < columns; i++) {
            expected.set(random.nextInt(rows), i, 1);
        }

        return expected;
    }
}
