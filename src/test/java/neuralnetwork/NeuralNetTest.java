package neuralnetwork;

import matrix.Matrix;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NeuralNetTest {

    private Random random = new Random();

    @Test
    public void testBackPropWeights(){

        final int INPUT_ROW = 4;
        final int COL = 5;

        final int OUTPUT_ROW = 4;


        Matrix input = new Matrix(INPUT_ROW, COL, i -> random.nextGaussian());

        Matrix expected = new Matrix(OUTPUT_ROW, COL, i -> 0);

        Matrix weights = new Matrix(OUTPUT_ROW, INPUT_ROW, i -> random.nextGaussian());

        Matrix biases = new Matrix(OUTPUT_ROW, 1, i -> random.nextGaussian());

        for (int i = 0; i < COL; i++) {                      //generating random expected
            expected.set(random.nextInt(OUTPUT_ROW), i, 1);
        }

        System.out.println("input\n\n" + input);
        System.out.println("weights\n\n" + weights);
        System.out.println("biases\n\n" + biases);

        System.out.println("expected\n\n" + expected);

        Matrix softmaxOutput = weights.multiply(input).modify((rows, columns, value) -> value + biases.get(rows)).softmax();

        Matrix approximatedResult = Approximator.gradient(input, in -> { //input is used by gradient to create in
            in = weights.multiply(in).modify((rows, columns, value) -> value + biases.get(rows));
            return LossFunction.crossEntropy(expected, in.softmax());
        });

        Matrix calculatedResult = softmaxOutput.apply((index, value) -> value - expected.get(index));
        calculatedResult = weights.transpose().multiply(calculatedResult);

        System.out.println("calculatedResult\n"+calculatedResult);
        System.out.println("approximatedResult\n"+approximatedResult);

        assertEquals(calculatedResult, approximatedResult); //if not, add more zeros to approximator's INC
    }

    @Test
    public void testSoftmaxCrossEntropyGradient(){

        final int ROW = 4;
        final int COL = 5;
        Matrix input = new Matrix(ROW, COL, i -> random.nextGaussian());

        Matrix expected = new Matrix(ROW, COL, i -> 0);

        for (int i = 0; i < COL; i++){                      //generating random expected
            expected.set(random.nextInt(ROW), i, 1);
        }

        System.out.println("input\n\n"+input);

        System.out.println("expected\n\n"+expected);

        Matrix softmaxOutput = input.softmax();

        Matrix result = Approximator.gradient(input, in -> {
            return LossFunction.crossEntropy(expected, in.softmax());
        });

        result.forEach((index, value) -> {

            double expectedValue = expected.get(index);
            double softmaxValue = softmaxOutput.getValue(index);

            System.out.println(value+" value, "+softmaxValue+" softmax value, "+expectedValue+" expected value");
            assertTrue(Math.abs(value - (softmaxValue - expectedValue)) < 0.001);
        });

        System.out.println("result\n\n"+result);
    }

    @Test
    public void testAproximator(){

        final int ROW = 4;
        final int COL = 5;
        Matrix input = new Matrix(ROW, COL, i -> random.nextGaussian()).softmax();

        Matrix expected = new Matrix(ROW, COL, i -> 0);

        for (int i = 0; i < COL; i++){                      //generating random expected
            expected.set(random.nextInt(ROW), i, 1);
        }

        System.out.println("input\n\n"+input);

        System.out.println("expected\n\n"+expected);


        Matrix result = Approximator.gradient(input, in -> {
            return LossFunction.crossEntropy(expected, in);
        });

        input.forEach((index, value) -> {
            double resultValue = result.get(index);
            double expectedValue = expected.get(index);

            if (expectedValue < 0.001){
                assertTrue(Math.abs(resultValue)<0.001);
            }
            else {
                assertTrue(Math.abs(resultValue + 1.0/value) < 0.01); //shouldn't put too much zeros, i didn't get why
            }

        });

        System.out.println("result\n\n"+result);
    }

    @Test
    public void crossEntropy(){
        double [] expectedValues = {1, 0, 0, 0, 0, 1, 0, 1, 0};
        Matrix expected = new Matrix(3, 3, i -> expectedValues[i]);

        System.out.println(expected);

        Matrix actual = new Matrix(3, 3, i -> 0.05*i*i).softmax();

        System.out.println(actual);

        Matrix result = LossFunction.crossEntropy(expected, actual);
        System.out.println("cross entropy\n"+result);


        actual.forEach((row, column, index, value) -> {
            double expectedValue = expected.get(index);

            double loss = result.get(column);

            if (expectedValue == 1){
                assertTrue(Math.abs(-Math.log(value)) - loss < 0.001);
            }
        });
    }

    @Test
    public void testEngine(){
        Engine engine = new Engine();

        engine.add(Transform.DENSE, 8, 5);
        engine.add(Transform.RELU);
        engine.add(Transform.DENSE, 6);
        engine.add(Transform.RELU);
        engine.add(Transform.DENSE, 3);
        engine.add(Transform.SOFTMAX);

        Matrix  input = new Matrix(5,13, i -> random.nextGaussian());
        Matrix output = engine.runForwards(input);

        System.out.println(engine);
        System.out.println(output);
    }

    @Test
    public void testTemp(){
        int inputSize = 5; // amount of inputs: pixel, sound samples etc.
        int layer1Size = 6; //number of neuron in each layer
        int layer2Size = 4;

        Matrix input = new Matrix(inputSize, 4, i -> random.nextGaussian());

        Matrix layer1Weights = new Matrix(layer1Size, input.getRows(), i -> random.nextGaussian());
        Matrix layer1Biases = new Matrix(layer1Size, 1, i -> random.nextGaussian());

        Matrix layer2Weights = new Matrix(layer2Size, layer1Weights.getRows(), i -> random.nextGaussian());
        Matrix layer2Biases = new Matrix(layer2Size, 1, i -> random.nextGaussian());

        System.out.println("\tInput\n"+input);
        var output = input;   //set up initial input

        output = layer1Weights.multiply(input);   // weigths x inputs       почему не наоборот? бо тогда 1х6 а не 5х5, ширина первой х высоту второй
        System.out.println(output);

        output = output.modify((rows, cols, value) -> (value + layer1Biases.get(rows)));  //added biases
        System.out.println(output);

        output = output.modify(value -> value > 0 ? value : 0);   // modified ny RelU
        System.out.println(output);

        /***  layer 2:  ***/

        output = layer2Weights.multiply(output);
        System.out.println(output);

        output = output.modify((rows, cols, value) -> (value + layer2Biases.get(rows)));  //added biases
        System.out.println(output);

        output = output.softmax();   // modified by Softmax as activation of final layer
        System.out.println("softmax\n"+output);
    }

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
