package app;

import neuralnetwork.NeuralNetwork;
import neuralnetwork.Transform;
import neuralnetwork.loader.Loader;
import neuralnetwork.loader.MetaData;
import neuralnetwork.loader.image.ImageLoader;
import neuralnetwork.loader.test.TestLoader;

import java.io.File;
import java.lang.reflect.Member;

public class App {
    public static void main(String[] args) {
        final String fileName = "mnistNeural1.net";

        if (args.length == 0){
            System.out.println("Usage: [app] <MNIST DATA DIRECTORY>");
            return;
        }

        String directory = args[0];
        if (!new File(directory).isDirectory()){
            System.out.println("'"+directory+"' is not a directory");
        }

//        try {
//            System.out.println(new File(args[0]).getCanonicalPath());
//        } catch (Exception e){}

        final String trainImages = String.format("%s%s%s", directory, File.separator, "train-images.idx3-ubyte");
        final String trainLabels = String.format("%s%s%s", directory, File.separator, "train-labels.idx1-ubyte");
        final String testImages = String.format("%s%s%s", directory, File.separator, "t10k-images.idx3-ubyte");
        final String testLabels = String.format("%s%s%s", directory, File.separator, "t10k-labels.idx1-ubyte");

        Loader trainLoader = new ImageLoader(trainImages, trainLabels, 32);
        Loader testLoader = new ImageLoader(testImages, testLabels, 32);

        MetaData metaData = trainLoader.open();
        int inputSize = metaData.getInputSize();
        int outputSize = metaData.getExpectedSize();
        trainLoader.close();

        NeuralNetwork neuralNetwork = NeuralNetwork.load(fileName);

        if (neuralNetwork == null) {
            System.out.println("Unable to load neural network from saved, creating a new one");

            neuralNetwork = new NeuralNetwork();
            neuralNetwork.setScaleInitialWeights(0.02);
            neuralNetwork.setThreads(4);
            neuralNetwork.setEpochs(50);
            neuralNetwork.setLearningRates(0.02, 0.001);

            neuralNetwork.add(Transform.DENSE, 200, inputSize);
            neuralNetwork.add(Transform.RELU);
            neuralNetwork.add(Transform.DENSE, outputSize);
            neuralNetwork.add(Transform.SOFTMAX);

        } else {
            System.out.println("Loaded from " + fileName);
        }

        System.out.println(neuralNetwork);

        neuralNetwork.fit(trainLoader, testLoader);

        if (neuralNetwork.save(fileName)) {
            System.out.println("Saved to " + fileName);
        } else {
            System.out.println("Unable to save to " + fileName);
        }
    }
}
