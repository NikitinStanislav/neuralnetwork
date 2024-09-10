package app;

import neuralnetwork.NeuralNetwork;
import neuralnetwork.Transform;
import neuralnetwork.loader.Loader;
import neuralnetwork.loader.test.TestLoader;

public class App {
    public static void main(String[] args) {
        String fileName = "neural.net";

        NeuralNetwork neuralNetwork = NeuralNetwork.load(fileName);

        if (neuralNetwork == null) {
            System.out.println("Unable to load neural network from saved, creating a new one");

            int inputRows = 10;
            int outputRows = 3;

            neuralNetwork = new NeuralNetwork();
            neuralNetwork.add(Transform.DENSE, 100, inputRows);
            neuralNetwork.add(Transform.RELU);
            neuralNetwork.add(Transform.DENSE, 50);
            neuralNetwork.add(Transform.RELU);
            neuralNetwork.add(Transform.DENSE, outputRows);
            neuralNetwork.add(Transform.SOFTMAX);

            neuralNetwork.setThreads(4);
            neuralNetwork.setEpochs(50);
            neuralNetwork.setLearningRates(0.02, 0.001);
        } else {
            System.out.println("Loaded from " + fileName);
        }

        System.out.println(neuralNetwork);

        Loader trainLoader = new TestLoader(60_000, 32);
        Loader testLoader = new TestLoader(10_000, 32); //to test out training result

        neuralNetwork.fit(trainLoader, testLoader);

        if (neuralNetwork.save(fileName)) {
            System.out.println("Saved to " + fileName);
        } else {
            System.out.println("Unable to save to " + fileName);
        }

    }
}
