package neuralnetwork;

import matrix.Matrix;

import java.util.function.Function;

public class Approximator {

    public static Matrix gradient(Matrix input, Function<Matrix, Matrix> transform) {

        input.forEach((row, column, index, value) -> {
            System.out.printf("%10.3f", value);

            if (column == input.getColumns() - 1){
                System.out.println();
            }

        });


           return null;
        }

}
