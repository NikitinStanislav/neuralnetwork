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

    public Matrix(int rows, int columns) {

        this.rows = rows;
        this.columns = columns;

        a = new double[rows * columns];
    }

    public Matrix(int rows, int columns, Producer producer) {
        this(rows, columns);

        for (int i = 0; i < a.length; i++) {
            a[i] = producer.produce(i);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int index = 0;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
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

        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i] - matrix.a[i]) > TOLERANCE) {
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

    public Matrix apply(ValueProducer producer) {
        Matrix result = new Matrix(rows, columns);

        for (int i = 0; i < a.length; i++) {
            result.a[i] = producer.produce(i, a[i]);
        }

        return result;
    }


    public Matrix multiply(Matrix matrix) {

        assert columns == matrix.rows : "First matrix's length isn't equal to second matrix's width";

        Matrix result = new Matrix(rows, matrix.columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {

                for (int counter = 0; counter < columns; counter++) {
                    result.a[i * result.columns + j] +=
                            a[i * columns + counter] * matrix.a[j + counter * matrix.columns];
                }
            }
        }

        return result;
    }

}
