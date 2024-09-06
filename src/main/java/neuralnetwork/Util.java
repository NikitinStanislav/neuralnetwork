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

    public static Matrix generateTrainableExpectedMatrix(int outputRows, Matrix input) { //not random but with respect to actual input?
        Matrix expected = new Matrix(outputRows, input.getColumns());
        //System.out.println(input);
        Matrix columnSum = input.sumColumns(); //single row matrix containing the column sums of input
        //System.out.println(columnSum);

        columnSum.forEach((row, column, value) -> {
            double rowIndex = (Math.sin(value) + 1) / 2.0; //when +1 -> values 0.0..2.0; when /2 -> values 0.0..1.0
            //System.out.println(rowIndex);
            rowIndex *= outputRows; // that's how you designate the row which will be a 1
            //System.out.println((int)rowIndex);
            expected.set((int)rowIndex, column, 1);
        });
        //System.out.println(expected);
        return expected;
    }

    public static TrainingMatrices generateTrainingMatrices(int inputRows, int outputRows, int columns){
        var io = generateTrainingArrays(inputRows, outputRows, columns);

        Matrix input = new Matrix(inputRows, columns, io.getInput());
        Matrix output = new Matrix(outputRows, columns, io.getOutput());
        
        return new TrainingMatrices(input, output);
    }

    public static TrainingArrays generateTrainingArrays(int inputSize, int outputSize, int numberItems){
        double[] input = new double[inputSize * numberItems];
        double[] output = new double[outputSize * numberItems];

        int inputPos = 0;
        int outputPos = 0;


        for (int col = 0; col < numberItems; col++) {  //each column is coordinate of n-dimensional sphere, where n==rows
            int radius = random.nextInt(outputSize); //0..(outputRows-1)

            double[] values = new double[inputSize];

            double initialRadius = 0;

            for (int row = 0; row < inputSize; row++) {
                double value = random.nextGaussian(); //0..1, keep in mind
                values[row] = value;
                initialRadius += value * value;
            }

            initialRadius = Math.sqrt(initialRadius); //x^2 + y^2 + z^2 = R^2 formula

            for (int row = 0; row < inputSize; row++) {
                input[inputPos++] = values[row] * radius / initialRadius;
            }

            output[outputPos + radius] = 1;

            outputPos += outputSize;
        }

        return new TrainingArrays(input, output);
    }

    /*public static TrainingMatrices generateTrainingMatrices(int inputRows, int outputRows, int columns){
        Matrix input = new Matrix(inputRows, columns);
        Matrix output = new Matrix(outputRows, columns);

        for (int col = 0; col < columns; col++) {  //each column is coordinate of n-dimensional sphere, where n==rows
            int radius = random.nextInt(outputRows); //0..(outputRows-1)

            double[] values = new double[inputRows];

            double initialRadius = 0;

            for (int row = 0; row < inputRows; row++) {
                double value = random.nextGaussian(); //0..1, keep in mind
                values[row] = value;
                initialRadius += value * value;
            }

            initialRadius = Math.sqrt(initialRadius); //x^2 + y^2 + z^2 = R^2 formula

            for (int row = 0; row < inputRows; row++) {
                input.set(row, col, values[row]*radius/initialRadius);
            }

            output.set(radius, col, 1); //generating expected
        }

        return new TrainingMatrices(input, output);
    }*/
}
