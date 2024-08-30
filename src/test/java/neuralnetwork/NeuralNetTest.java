package neuralnetwork;

import matrix.Matrix;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NeuralNetTest {

    private Random random = new Random();

    @Test
    public void testWeightGradient() {
        int inputRows = 3;
        int outputRows = 4;

        Matrix weights = Util.generateWeights(outputRows, inputRows); //because multiplication is coming
        Matrix input = Util.generateInputMatrix(inputRows, 1);  //vector here
        Matrix expected = Util.generateExpectedMatrix(outputRows, 1);

        Matrix output = weights.multiply(input).softmax();

        Matrix loss = LossFunctions.crossEntropy(expected, output);

        System.out.println(input);
        System.out.println(expected);
        System.out.println(weights);
        System.out.println("output\n"+output);
        System.out.println("loss\n"+loss);

        Matrix calculatedError = output.apply((index, value) -> value - expected.get(index));

        System.out.println(calculatedError);

        Matrix calculatedWeightGradient = calculatedError.multiply(input.transpose()); //то что в конспекте и 120 видео

        System.out.println(calculatedWeightGradient);

        Matrix approximatedWeightGradient = Approximator.weightGradient(weights, w -> {
            Matrix out = w.multiply(input).softmax();
            return LossFunctions.crossEntropy(expected, out);
        });

        System.out.println(approximatedWeightGradient);

        calculatedWeightGradient.setTolerance(0.001);

        assertEquals(calculatedWeightGradient, approximatedWeightGradient);

    }

    @Test
    public void testBackProp(){

        interface NeuralNet {
            Matrix apply(Matrix m);
        }

        final int INPUT_ROW = 4;
        final int COL = 5;

        final int OUTPUT_ROW = 4;

        Matrix input = new Matrix(INPUT_ROW, COL, i -> random.nextGaussian());
        Matrix expected = new Matrix(OUTPUT_ROW, COL, i -> 0);
        Matrix weights = new Matrix(OUTPUT_ROW, INPUT_ROW, i -> random.nextGaussian());
        Matrix biases = new Matrix(OUTPUT_ROW, 1, i -> random.nextGaussian());


        System.out.println("input\n\n" + input);
        System.out.println("weights\n\n" + weights);
        System.out.println("biases\n\n" + biases);
        System.out.println("expected\n\n" + expected);

        for (int i = 0; i < COL; i++) {                      //generating random expected
            expected.set(random.nextInt(OUTPUT_ROW), i, 1);
        }

        NeuralNet neuralNet = m -> {
            Matrix out = m.apply((index, value) -> value > 0 ? value : 0);
            out = weights.multiply(out); // weights
            out.modify((rows, columns, value) -> value + biases.get(rows)); //biases
            out = out.softmax(); //softmax

            return out;
        };

        Matrix softmaxOutput = neuralNet.apply(input);

        Matrix approximatedResult = Approximator.gradient(input, in -> { //input is used by gradient to create in
            Matrix out = neuralNet.apply(in);
            return LossFunctions.crossEntropy(expected, out);
        });

        Matrix calculatedResult = softmaxOutput.apply((index, value) -> value - expected.get(index));
        calculatedResult = weights.transpose().multiply(calculatedResult);
        calculatedResult = calculatedResult.apply(((index, value) -> input.get(index) > 0 ? value : 0));

        System.out.println("calculatedResult\n"+calculatedResult);
        System.out.println("approximatedResult\n"+approximatedResult);

        assertEquals(calculatedResult, approximatedResult); //if not, add more zeros to approximator's INC
    }

    @Test
    public void testBackPropWeights(){

        interface NeuralNet {
            Matrix apply(Matrix m);
        }

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

        NeuralNet neuralNet = m -> weights.multiply(m).modify((rows, columns, value) -> value + biases.get(rows)).softmax();

        Matrix softmaxOutput = neuralNet.apply(input);

        Matrix approximatedResult = Approximator.gradient(input, in -> { //input is used by gradient to create in
            Matrix out = neuralNet.apply(in);
            return LossFunctions.crossEntropy(expected, out);
        });

        Matrix calculatedResult = softmaxOutput.apply((index, value) -> value - expected.get(index));
        calculatedResult = weights.transpose().multiply(calculatedResult);

        System.out.println("calculatedResult\n"+calculatedResult);
        System.out.println("approximatedResult\n"+approximatedResult);

        assertEquals(calculatedResult, approximatedResult); //if not, add more zeros to approximator's INC
    }

    @Test
    public void testSoftmaxCrossEntropyGradient(){ //то есть вот это все безобразие, это уже было backprop перед softmax?

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

        System.out.println("softmaxOutput\n"+softmaxOutput);

        Matrix result = Approximator.gradient(input, in -> {
            return LossFunctions.crossEntropy(expected, in.softmax());

            /** softmax() перенесли из 116 строчки сюда
            раньше result был кучей нулей и высокое отрицательное значение там где надо
            сейчас result идентичен input.softmax(), но нужные значения == input.softmax()-1
            ИЛИ же, сумма всех остальных значений в колонке * (-1)

            softmaxOutput
              +0.22026  +0.28813  +0.06658  +0.01550  +0.20845
              +0.05136  +0.30480  +0.69911  +0.28812  +0.11858
              +0.59924  +0.29922  +0.04646  +0.09695  +0.32916
              +0.12914  +0.10785  +0.18784  +0.59943  +0.34381

             old result

             +0.00000  +0.00000 -15.01758  +0.00000  +0.00000
             -19.46832  -3.28075  +0.00000  +0.00000  -8.43263
             +0.00000  +0.00000  +0.00000  +0.00000  +0.00000
             +0.00000  +0.00000  +0.00000  -1.66825  +0.00000

             new result

             +0.22026  +0.28813  -0.93342  +0.01550  +0.20845
             -0.94864  -0.69520  +0.69911  +0.28812  -0.88142
             +0.59924  +0.29922  +0.04646  +0.09695  +0.32916
             +0.12914  +0.10785  +0.18784  -0.40057  +0.34381 */

             });

        System.out.println("result\n\n"+result);

        result.forEach((index, value) -> {

            double expectedValue = expected.get(index);
            double softmaxValue = softmaxOutput.getValue(index);

            //System.out.println(value+" value, "+softmaxValue+" softmax value, "+expectedValue+" expected value");
            assertTrue(Math.abs(value - (softmaxValue - expectedValue)) < 0.001);
        });

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
            return LossFunctions.crossEntropy(expected, in);
        });

        input.forEach((index, value) -> {
            double resultValue = result.get(index);
            double expectedValue = expected.get(index);

            if (expectedValue < 0.001){
                assertTrue(Math.abs(resultValue)<0.001);
            }
            else {
                assertTrue(Math.abs(resultValue + 1.0/value) < 0.01); //shouldn't put too much zeros, i didn't get why
                //because derivative of ln (1/value) = -1/value; initially there was (resultValue - (-1.0/value))
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

        Matrix result = LossFunctions.crossEntropy(expected, actual);
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
        int inputRows = 5;
        int columns = 6;
        int outputRows = 3; //should be the same as last Transform.Dense

        Engine engine = new Engine();

        engine.add(Transform.DENSE, 8, 5);
        engine.add(Transform.RELU);
        engine.add(Transform.DENSE, 6);
        engine.add(Transform.RELU);
        engine.add(Transform.DENSE, 3);
        engine.add(Transform.SOFTMAX);
        engine.setStoreInputError(true);

        Matrix input = Util.generateInputMatrix(inputRows, columns);
        Matrix expected = Util.generateExpectedMatrix(outputRows, columns);

        Matrix approximatedError = Approximator.gradient(input, in -> { //it's a rate of change of the losses with respect to the elements in the input matrix
            BatchResult batchResult = engine.runForwards(in);
            return LossFunctions.crossEntropy(expected, batchResult.getOutput());
        });

        BatchResult batchResult = engine.runForwards(input);
        engine.runBackwards(batchResult, expected);

        Matrix calculatedError = batchResult.getInputError();

        System.out.println(approximatedError);
        System.out.println(calculatedError);

        calculatedError.setTolerance(0.0001);

        assertEquals(approximatedError, calculatedError);

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
