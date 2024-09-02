package neuralnetwork;

import matrix.Matrix;

import java.util.LinkedList;

public class BatchResult {  //class for storing inputs and outputs

    private LinkedList<Matrix> io = new LinkedList<>(); //inputs gonna be an outputs eventually, except the first one
    private LinkedList<Matrix> weightErrors = new LinkedList<>();
    private Matrix inputError;
    private double averageLoss = 0.0;

    public LinkedList<Matrix> getIo(){
        return io;
    }

    public void addIo(Matrix matrix){
        io.add(matrix);
    }

    public Matrix getOutput(){
        return io.getLast();
    }

    public LinkedList<Matrix> getWeightErrors() {
        return weightErrors;
    }

    public void addWeightErrors(Matrix m) {
        weightErrors.addFirst(m);
    }

    public Matrix getInputError() {
        return inputError;
    }

    public void setInputError(Matrix inputError) {
        this.inputError = inputError;
    }

    public void setAverageLoss(double averageLoss) {
        this.averageLoss = averageLoss;
     }

    public double getAverageLoss() {
        return averageLoss;
    }
}
