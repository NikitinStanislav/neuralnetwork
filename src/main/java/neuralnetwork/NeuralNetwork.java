package neuralnetwork;

import matrix.Matrix;
import neuralnetwork.loader.BatchData;
import neuralnetwork.loader.Loader;
import neuralnetwork.loader.MetaData;

import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NeuralNetwork implements Serializable {
    private Engine engine;
    private int epochs = 20;
    transient private double learningRate;
    private double initialLearningRate = 0.01;
    private double finalLearningRate = 0.001;
    transient private Object lock = new Object();
    private int threads = 2;

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public NeuralNetwork(){
        engine = new Engine();
    }

    public void setScaleInitialWeights(double scaleInitialWeights) {
        engine.setScaleInitialWeights(scaleInitialWeights);
    }

    public void setLearningRates(double initialLearningRate, double finalLearningRate){
        this.initialLearningRate = initialLearningRate;
        this.finalLearningRate = finalLearningRate;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public double[] predict(double[] inputData){
        Matrix input = new Matrix(inputData.length, 1, i->inputData[i]);
        BatchResult batchResult = engine.runForwards(input);
        return batchResult.getOutput().getA();
    }

    public void fit(Loader trainLoader, Loader evalLoader) { //it trains the network
        learningRate = initialLearningRate;

        for (int epoch = 0; epoch < epochs; epoch++) {
            System.out.printf("Epoch %3d ", epoch+1);

            runEpoch(trainLoader, true);

            if (evalLoader != null) {
                runEpoch(evalLoader, false);
            }

            System.out.println();
            learningRate -= (initialLearningRate - finalLearningRate)/epochs;
        }
    }

    private void runEpoch(Loader trainLoader, boolean isTrainingMode) {
        trainLoader.open();

        var queue = createBatchTasks(trainLoader, isTrainingMode);
        consumeBatchTasks(queue, isTrainingMode);

        trainLoader.close();
    }

    private void consumeBatchTasks(LinkedList<Future<BatchResult>> batches, boolean isTrainingMode) {
        int numberBathes = batches.size();
        int index= 0;

        double averageLoss = 0;
        double averagePercentCorrect = 0;

        for (var batch : batches) {
            try {
                var batchResult = batch.get();

                if (!isTrainingMode){
                    averageLoss+=batchResult.getAverageLoss();
                    averagePercentCorrect+=batchResult.getPercentCorrect();
                }
            } catch (Exception e) {
                 throw new RuntimeException(e.getMessage());
            }

            int printDot = numberBathes/30;

            if (isTrainingMode && index++ % printDot == 0){
                System.out.print(".");
            }
        }

        if(!isTrainingMode){
            averageLoss /= batches.size();
            averagePercentCorrect /= batches.size();
            System.out.printf("Loss: %.3f -- Percent correct: %.2f", averageLoss, averagePercentCorrect);
        }
    }

    private LinkedList<Future<BatchResult>> createBatchTasks(Loader trainLoader, boolean isTrainingMode) {
        LinkedList<Future<BatchResult>> batches = new LinkedList<>();

        MetaData metaData = trainLoader.getMetaData();
        int numberBatches = metaData.getNumberBathes();

        var executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < numberBatches; i++) {
            batches.add(executor.submit(()->runBatch(trainLoader, isTrainingMode)));
        }

        executor.shutdown();

        return batches;
    }

    private BatchResult runBatch(Loader trainLoader, boolean isTrainingMode) {
        MetaData metaData = trainLoader.getMetaData();

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
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Epochs: %d\n", epochs));
        sb.append(String.format("Initial Learning Rate: %.3f\n", initialLearningRate));
        sb.append(String.format("Final Learning Rate: %.3f\n", finalLearningRate));
        sb.append(String.format("Threads: %d\n", threads));
        sb.append("\nEngine configuration:\n");
        sb.append(engine);

        return sb.toString();
    }

    public boolean save(String fileName) {

        try(var ds = new ObjectOutputStream(new FileOutputStream(fileName))){
            ds.writeObject(this);
        } catch (IOException ex){
            System.err.println("Unable to save to "+fileName+"\n");
            System.out.println(ex);
            return false;
        }

        return true;
    }

    public static NeuralNetwork load(String fileName) {
        NeuralNetwork neuralNetwork = null;

        try(var ds = new ObjectInputStream(new FileInputStream(fileName))){
           neuralNetwork = (NeuralNetwork)ds.readObject();
        } catch (Exception ex){
            System.err.println("Unable to load from "+fileName+"\n");
            System.out.println(ex);
        }

        return neuralNetwork;
    }

    @Serial
    public Object readResolve() {
        this.lock = new Object();
        return this;
    }
}
