package july;

import matrix.Matrix;
import org.junit.Test;

public class NeuralNetTest {

    @Test
    public void testAddBiases() {

        Matrix inputs = new Matrix(3, 3, i -> i + 1);
        Matrix weights = new Matrix(3, 3, i -> i + 1);
        Matrix biases = new Matrix(3, 1, i -> i + 1);

        Matrix result = weights.multiply(inputs);

        System.out.println(weights);
        System.out.println(inputs);
        System.out.println(biases);
        System.out.println(result);

        result = result.modify((rows, cols, value) -> (value + biases.get(rows)));
        System.out.println(result);
    }
}
