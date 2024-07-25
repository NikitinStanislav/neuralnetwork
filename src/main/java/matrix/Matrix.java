package matrix;

import java.util.Arrays;

public class Matrix {

    private static final String NUMBER_FORMAT = "%+10.2f";

    public interface Producer {
        double produce(int index);
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


}
