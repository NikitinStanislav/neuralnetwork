package matrix;

import java.util.Arrays;
import java.util.Objects;

public class Matrix {

    private static final String NUMBER_FORMAT = "%+10.3f";
    private static final Double TOLERANCE = 0.0001;

    public interface Producer {
        double produce(int index);
    }

    public interface IndexValueProducer {
        double produce(int index, double value);
    }

    public interface ValueProducer {
        double produce (double value);
    }

    public interface IndexValueConsumer {
        void consume (int index, double value);
    }

    public interface RowColumnsValueConsumer {
        void consume (int row, int column, double value);
    }

    public interface RowColumnsIndexValueConsumer {
        void consume (int row, int column, int index, double value);
    }

    public interface RowColumnsProducer {
        double produce(int rows, int columns, double value);
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

    public String toString(boolean showValues){
        if (showValues) {
            return toString();
        } else {
            return rows + "x" + columns;
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

    public double get(int index) {
        return a[index];
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

    public Matrix apply(IndexValueProducer producer) {
        Matrix result = new Matrix(rows, columns);

        for (int i = 0; i < a.length; i++) {
            result.a[i] = producer.produce(i, a[i]);
        }

        return result;
    }

    public Matrix modify (RowColumnsProducer producer){

        int index = 0;

        for (int row = 0; row < rows; row++){
            for (int cols = 0; cols < columns; cols++){

                a[index] = producer.produce(row, cols, a[index]);

                index++;
            }
        }

        return this;
    }

    public Matrix modify(ValueProducer producer){

        for (int i = 0; i <a.length; i++){
            a[i] = producer.produce(a[i]);
        }

        return this;
    }

    public void forEach (IndexValueConsumer consumer){
        for (int i = 0; i < a.length; i++){
            consumer.consume(i, a[i]);
        }
    }

    public void forEach(RowColumnsValueConsumer consumer) {
        int index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                consumer.consume(row, col, a[index++]);
            }
        }

    }

    public void forEach(RowColumnsIndexValueConsumer consumer) {
        int index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                consumer.consume(row, col, index, a[index++]);
            }
        }

    }



    public Matrix sumColumns() {
        Matrix result = new Matrix(1, columns);

        //int index = 0;
        for (int row = 0; row < rows; row++){
            for (int cols = 0; cols < columns; cols++){
                result.a[cols] += a[columns*row + cols];
                //result.a[cols] += a[index++];
            }
        }

        return result;
    }

    public Matrix softmax() {

        Matrix result = new Matrix(rows, columns, i -> Math.exp(a[i]));

        Matrix summed = result.sumColumns();

        /*int index = 0;

        for (int row = 0; row < rows; row++){
            for (int cols = 0; cols < columns; cols++){
                result.a[index] /= summed.a[cols];
                index++;
            }
        }*/

        result.modify((rows, columns, value) -> value/ summed.get(columns));

        return result;
    }


    public Matrix multiply(Matrix matrix) {

        assert columns == matrix.rows : "First matrix's length isn't equal to second matrix's width";

        Matrix result = new Matrix(rows, matrix.columns);


        for (int i = 0; i < rows; i++) {

            for (int counter = 0; counter < columns; counter++) {    //so apparently loops relocation improved the runtime 3 times faster

                for (int j = 0; j < matrix.columns; j++) {


                    result.a[i * result.columns + j] +=
                            a[i * columns + counter] * matrix.a[j + counter * matrix.columns];
                }
            }
        }

        return result;
    }

   /* public Matrix modify (Matrix bias){
        return this.apply((index, value) -> value - bias.getValue(index));
    }*/

}
