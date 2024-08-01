package july;

import matrix.Matrix;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class NeuralNetTest {

    private Random random = new Random();

    @Test
    public void testAddBiases() {

        Matrix inputs = new Matrix(2, 2, i -> i + 1);
        Matrix weights = new Matrix(1, 2, i -> i + 1);
        Matrix biases = new Matrix(1, 1, i -> i + 1);

        Matrix result = weights.multiply(inputs);

        System.out.println(weights);
        System.out.println(inputs);
        System.out.println(biases);
        System.out.println(result);

        result = result.modify((rows, cols, value) -> (value + biases.get(rows)));
        System.out.println(result);
    }

    @Test
    public void testReLu(){
        final int numberInput = 6; //кол-во интпутов, не для каждого нейрона а всего сколько пачек инпутов
        final int numberNeuron = 5;
        final int inputSize = 4;  // это уже сколько значений в одном инпуте; он же кол-во весов на нейрон

        Matrix inputs = new Matrix(inputSize, numberInput, i -> random.nextDouble());
        Matrix weights = new Matrix(numberNeuron, inputSize, i -> random.nextGaussian());   // я не понимаю что эта матрица из себя представляет
        Matrix biases = new Matrix(numberNeuron, 1, i -> random.nextGaussian());

        Matrix result = weights.multiply(inputs).modify((rows, cols, value) -> (value + biases.get(rows)));


        System.out.println(weights);
        System.out.println(inputs);
        System.out.println(biases);
        System.out.println(result);

//        result = result.modify((rows, cols, value) -> (value + biases.get(rows)));
//        System.out.println(result);

        Matrix result2 = result.modify(value -> value > 0 ? value : 0);

        System.out.println(result2);

        result2.forEach((index, value) ->{

            double originalValue = result.get(index);

            if (originalValue > 0){
                assertTrue(Math.abs(originalValue - value) < 0.000001);
            } else {
                assertTrue(Math.abs(value) < 0.000001);
            }

        });
    }
}
