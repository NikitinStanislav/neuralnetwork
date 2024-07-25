package matrix;

import java.util.Arrays;

public class Matrix {

    private static final String NUMBER_FORMAT = "%+10.2f";

    public interface Producer {
        double produce(int index);
    }

    public interface ValueProducer {
        double produce(int index, double value);
    }

    private double[] a;

    private int rows;
    private int columns;

    public Matrix(int rows, int columns){

        this.rows = rows;
        this.columns = columns;

        a = new double[rows * columns];
    }

    public Matrix(int rows, int columns, Producer producer){
        this (rows, columns);

        for (int i = 0; i<a.length; i++){
            a[i] = producer.produce(i);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int index = 0;

        for (int row = 0; row<rows; row++){
            for (int column = 0; column<columns; column++){
                sb.append(String.format(NUMBER_FORMAT, a[index]));
                index++;
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public Matrix apply(ValueProducer producer){
        Matrix result = new Matrix(rows, columns);

        for (int i = 0; i<a.length; i++){
            result.a[i] = producer.produce(i, a[i]);
        }

        return result;
    }

}
