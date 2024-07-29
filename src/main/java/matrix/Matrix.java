package matrix;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class Matrix {

    private static final String NUMBER_FORMAT = "%+10.2f";
    private static final Double TOLERANCE = 0.0001;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;

        for (int i = 0; i<a.length; i++){
            if (Math.abs(a[i] - matrix.a[i]) > TOLERANCE){
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rows, columns);
        result = 31 * result + Arrays.hashCode(a);
        return result;
    }

    public double[] getA() {
        return a;
    }

    public double getValue(int index) {
        return a[index];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Matrix apply(ValueProducer producer){
        Matrix result = new Matrix(rows, columns);

        for (int i = 0; i<a.length; i++){
            result.a[i] = producer.produce(i, a[i]);
        }

        return result;
    }

    public static double[][] multiply(Matrix m1, Matrix m2){
        //Matrix result = new Matrix(m1.getRows(), m2.getColumns());

        // выделить строки первой и колонки второй матрицы, использовать аплай для
        // поиска каждого значения и результаты добавлять в новую матрцу(там будет цикл)

        double[][] matrix1 = m1.formArrayMatrix();
        double[][] matrix2 = m2.formArrayMatrix();

        double[][] result = new double[m1.rows][m2.columns];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {


                    for (int counter= 0; counter<matrix2.length; counter++){
                        result[i][j] += matrix1[i][counter] * matrix2[counter][j];
                    }

            }
        }


        return result;
    }

    double[][] formArrayMatrix(){

        double[][] result = new double[this.rows][this.columns];
        int aCounter = 0;
        for (int i = 0; i < this.rows; i++){
            for (int j = 0; j <this.columns; j++){
                result [i][j] = this.a[aCounter];
                aCounter++;
            }
        }
        return result;
    }

    public static Matrix formMatrixFromArray(double[][] matrix) {
        Matrix result = new Matrix(matrix.length, matrix[0].length);

        int index = 0;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {

                result.a[index] = matrix[i][j];
                index++;
            }
        }

        return result;
    }
}

