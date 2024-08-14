package neuralnetwork;

import matrix.Matrix;

import java.util.LinkedList;
import java.util.Random;

public class Engine {
    private LinkedList<Transform> transforms = new LinkedList<>();
    private LinkedList<Matrix> weights = new LinkedList<>();
    private LinkedList<Matrix> biases = new LinkedList<>();

    private Random random = new Random();

    public Matrix runForwards(Matrix input){
        Matrix output = input;

        int denseIndex = 0;

        for (var tr: transforms){
            if (tr == Transform.DENSE){
                Matrix weight = weights.get(denseIndex);
                Matrix bias = biases.get(denseIndex);

                output = weight.multiply(output).modify((rows, cols, value) -> (value + bias.get(rows)));
                denseIndex++;

            } else if (tr == Transform.RELU ){
                output = output.modify(value -> value > 0 ? value : 0);

            } else if (tr == Transform.SOFTMAX){
                output = output.softmax();
            }
        }
        return output;

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
}
