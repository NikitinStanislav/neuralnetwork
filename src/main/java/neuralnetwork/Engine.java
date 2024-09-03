package neuralnetwork;

import matrix.Matrix;

import java.util.LinkedList;
import java.util.Random;

public class Engine {
    private LinkedList<Transform> transforms = new LinkedList<>();
    private LinkedList<Matrix> weights = new LinkedList<>();
    private LinkedList<Matrix> biases = new LinkedList<>();

    private LossFunction lossFunction = LossFunction.CROSS_ENTROPY;

    private boolean storeInputError = false;

    private Random random = new Random();

    public void adjust(BatchResult batchResult, double learningRate){
        var weightsInput = batchResult.getWeightInputs();
        var weightErrors = batchResult.getWeightErrors();

        assert weightsInput.size() == weightErrors.size();
        assert weightsInput.size() == weights.size();

        for (int i = 0; i < weights.size(); i++) {
            var weight = weights.get(i);
            var bias = biases.get(i);
            var error = weightErrors.get(i);
            var input = weightsInput.get(i);

            assert weight.getColumns() == input.getRows();

            var weightAdjust = error.multiply(input.transpose());
            var biasAdjust = error.averageColumn();

            double rate = learningRate/weight.getColumns();

            //System.out.println(weight);
            weight.modify((index, value) -> value - rate*weightAdjust.get(index));
            //System.out.println(weight);
            bias.modify(((rows, columns, value) -> value - learningRate * biasAdjust.get(rows)));
        }

    }

    public void evaluate(BatchResult batchResult, Matrix expected){
        if (lossFunction != LossFunction.CROSS_ENTROPY){
            throw new UnsupportedOperationException("YOU'RE WRONG HERE! only Cross entropy is supported");
        }

        double averageLoss = LossFunctions.crossEntropy(expected, batchResult.getOutput()).averageColumn().get(0);

        Matrix predictions = batchResult.getOutput().getGreatestRowNumbers();
        Matrix actual = expected.getGreatestRowNumbers();
        
        int correct = 0;
        for (int i = 0; i < actual.getColumns(); i++) {
            if ((int)actual.get(i) == (int)predictions.get(i)){
                ++correct;
            }
        }

        double percentCorrect = 100 * correct / actual.getColumns();

        batchResult.setAverageLoss(averageLoss);
        batchResult.setPercentCorrect(percentCorrect);
    }

    public BatchResult runForwards(Matrix input){

        BatchResult batchResult = new BatchResult();
        Matrix output = input;

        int denseIndex = 0;

        batchResult.addIo(output);

        for (var tr: transforms){
            if (tr == Transform.DENSE){

                batchResult.addWeightInput(output);

                Matrix weight = weights.get(denseIndex);
                Matrix bias = biases.get(denseIndex);

                output = weight.multiply(output).modify((rows, cols, value) -> (value + bias.get(rows)));
                denseIndex++;

            } else if (tr == Transform.RELU ){
                output = output.modify(value -> value > 0 ? value : 0);

            } else if (tr == Transform.SOFTMAX){
                output = output.softmax();
            }

            batchResult.addIo(output);
        }
        return batchResult;

    }

    public void runBackwards(BatchResult batchResult, Matrix expected){

        var transformsIterator = transforms.descendingIterator();

        if (lossFunction != LossFunction.CROSS_ENTROPY || transforms.getLast() != Transform.SOFTMAX) {
            throw new UnsupportedOperationException("Last loss function must be a cross entropy, must transform must be a softmax");
        }

        var ioIterator = batchResult.getIo().descendingIterator();
        var weightIterator = weights.descendingIterator();

        Matrix softmaxOutput = ioIterator.next();
        Matrix error = softmaxOutput.apply((index, value) -> value - expected.get(index));

        while (transformsIterator.hasNext()){
            Transform transform = transformsIterator.next();

            Matrix input = ioIterator.next();

            switch (transform){
                case DENSE :
                    Matrix weight = weightIterator.next();

                    batchResult.addWeightErrors(error);

                    if (weightIterator.hasNext() || storeInputError) {
                        error = weight.transpose().multiply(error);
                    }
                    break;
                case RELU :
                    error = error.apply(((index, value) -> input.get(index) > 0 ? value : 0));
                    break;
                case SOFTMAX : break;
                default : throw new UnsupportedOperationException("Not implemented");
            }

            //System.out.println(transform);
        }

        if (storeInputError){
            batchResult.setInputError(error);
        }
    }

    public void add(Transform transform, double...params){  // ... любое число аргументов

        if (transform == Transform.DENSE){
            int numberNeurons = (int)params[0];  //если DENSE то придет сюда первый аргумент params, логично? - логично

            /**   я тут мало что понял честно говоря.
             * The number of weigth per neuron for this layer is the number of rows of the previous layer
             * что тоже логично на самом деле, смотри схематичеки */
            int weightsPerNeron = weights.isEmpty() ? (int)params[1] : weights.getLast().getRows();

            Matrix weight = new Matrix(numberNeurons, weightsPerNeron, i -> random.nextGaussian());
            Matrix bias = new Matrix(numberNeurons, 1, i -> random.nextGaussian());

            weights.add(weight);
            biases.add(bias);
        }
        transforms.add(transform);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        int weigthIndex = 0;
        for (var tr : transforms){
            sb.append(tr);

            if(tr == Transform.DENSE){
                sb.append(" ").append(weights.get(weigthIndex).toString(false));

                weigthIndex++;
            }

            sb.append("\n");
        }

        return sb.toString();
    }


    public void setStoreInputError(boolean storeInputError) {
        this.storeInputError = storeInputError;
    }
}
