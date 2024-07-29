package matrix;

import org.junit.Test;

import static org.junit.Assert.*;

public class MatrixTest {

    @Test
    public void testFormMatrix(){
        Matrix m1 = new Matrix(3,4, i -> i+1);
        Matrix m2 = new Matrix(4,5, i -> i*2);

        System.out.println(m1);
        System.out.println(m2);


        double[][] matrix = Matrix.multiply(m1, m2) ;

        for (int i = 0; i < matrix.length; i++){     //print result
            for (int j = 0; j <matrix[0].length; j++){
                System.out.print(matrix[i][j] + "  ");

                if ((j + 1) % matrix[0].length == 0) {
                    System.out.println("\n");
                }
            }
        }

        Matrix result = Matrix.formMatrixFromArray(matrix);
        System.out.println(result);
    }

    @Test
    public void testAddMatrices(){
        Matrix m1 = new Matrix(2,2, i ->  i);
        Matrix m2 = new Matrix(2,2, i ->  i * (-1.5));

        Matrix expected = new Matrix(2, 2, i -> i * (-0.5));

        Matrix result = m1.apply((index, value) -> value + m2.getValue(index));

        System.out.println(m1);
        System.out.println(m2);
        System.out.println(expected);
        System.out.println(result);

        assertEquals(result, expected);
    }

    @Test
    public void testEquals(){
        Matrix m1 = new Matrix(3,4,i -> 2*(i-6));
        Matrix m2 = new Matrix(3,4,i -> 2*(i-6));
        Matrix m3 = new Matrix(3,4,i -> 2*(i-4));

        assertTrue(m1.equals(m2));
        assertFalse(m1.equals(m3));
    }

    @Test
    public void testMultiplyDouble(){
        Matrix m = new Matrix(3,4,i -> 2*(i-6));

        double x = 0.5;

        Matrix result = m.apply(((index, value) -> x * value));

        System.out.println(m);
        System.out.println(result);
    }

    @Test
    public void matrixCreate() {
        Matrix m = new Matrix(3, 4, i -> i*2);
        System.out.println(m.toString());

        String text = m.toString();

        double[] expected = new double[12];

        for(int i = 0; i < expected.length; i++) {
            expected[i] = i * 2;
        }

        var rows = text.split("\n");

        assertTrue(rows.length == 3);

        int index = 0;

        for(var row: rows) {
            var values = row.split("\\s+");

            for(var textValue: values) {

                if(textValue.length() == 0) {
                    continue;
                }

                var doubleValue = Double.valueOf(textValue);

                assertTrue(Math.abs(doubleValue - expected[index]) < 0.0001);

                index++;
            }
        }
    }

}
