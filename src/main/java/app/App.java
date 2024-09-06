package app;

import neuralnetwork.NeuralNetwork;
import neuralnetwork.Transform;

public class App {
    public static void main(String[] args) {
        int inputRows = 5;
        int outputRows = 7;

        NeuralNetwork neuralNetwork = new NeuralNetwork();

        neuralNetwork.add(Transform.DENSE, 100, inputRows);
        neuralNetwork.add(Transform.RELU);
        neuralNetwork.add(Transform.DENSE, outputRows);
        neuralNetwork.add(Transform.SOFTMAX);

        System.out.println(neuralNetwork);
    }
}
