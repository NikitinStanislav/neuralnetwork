package neuralnetwork;

import matrix.Matrix;
import neuralnetwork.loader.BatchData;
import neuralnetwork.loader.Loader;
import neuralnetwork.loader.MetaData;
import neuralnetwork.loader.test.TestLoader;

public class NeuralNetwork {
    private Engine engine;
    private int epochs = 20;
    private double learningRate;
    private double initialLearningRate = 0.01;
    private double finalLearningRate = 0;
    private Object lock = new Object();

    public NeuralNetwork(){
        engine = new Engine();
    }

    public void setLearningRates(double initialLearningRate, double finalLearningRate){
        this.initialLearningRate = initialLearningRate;
        this.finalLearningRate = finalLearningRate;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public void fit(Loader trainLoader, Loader evalLoader) { //it trains the network
        learningRate = initialLearningRate;

        for (int epoch = 0; epoch < epochs; epoch++) {
            System.out.printf("Epoch %3d ", epoch);

            runEpoch(trainLoader, true);

            if (evalLoader != null) {
                runEpoch(evalLoader, false);
            }
            learningRate -= (initialLearningRate - finalLearningRate)/epochs;
        }
    }

    private void runEpoch(Loader trainLoader, boolean isTrainingMode) {
        trainLoader.open();

        var queue = createBatchTasks(trainLoader, isTrainingMode);
        consumeBatchTasks(queue, isTrainingMode);

        trainLoader.close();
    }

    private void consumeBatchTasks(Object queue, boolean isTrainingMode) {
    }

    private Object createBatchTasks(Loader trainLoader, boolean isTrainingMode) {
        MetaData metaData = trainLoader.getMetaData();
        int numberBatches = metaData.getNumberBathes();

        for (int i = 0; i < numberBatches; i++) {
            runBatch(trainLoader, isTrainingMode);
        }
        return null;
    }

    private BatchResult runBatch(Loader trainLoader, boolean isTrainingMode) {
        MetaData metaData = trainLoader.open();

        int numberItems = metaData.getNumberItems();

        BatchData batchData = trainLoader.readBatch();

        int itemsRead = metaData.getItemsRead();

        int inputSize = metaData.getInputSize();
        int expectedSize = metaData.getExpectedSize();

        Matrix input = new Matrix(inputSize, itemsRead, batchData.getInputBatch());
        Matrix expected = new Matrix(expectedSize, itemsRead, batchData.getExpectedBatch());

        BatchResult batchResult = engine.runForwards(input);

        if(isTrainingMode){
            engine.runBackwards(batchResult, expected);

            synchronized (lock) {
                engine.adjust(batchResult, learningRate);
            }
        } else {
            engine.evaluate(batchResult, expected);
        }
        return batchResult;
    }

    public void add(Transform transform, double...params){
        engine.add(transform, params);
    }

    @Override
    public String toString() {
        return engine.toString();
    }
}
