package matrix;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class MatrixTest {

    private Random random = new Random();

    @Test
    public void testAverageColumns(){
        int row = 3;
        int col = 4;

        Matrix matrix = new Matrix(row, col, index -> index * 1.5 - 2);
        double averageIndex = (col - 1) / 2.0;

        Matrix expected = new Matrix(row, 1);
        expected.modify((rows, columns, value) -> (rows*col +averageIndex) * 1.5 - 2);

        Matrix result = matrix.averageColumn();

        System.out.println(matrix);
        System.out.println(expected);
        System.out.println(result);

        assertEquals(result,expected);
    }

    @Test
    public void testTranspose(){
        Matrix m = new Matrix(2, 3, i -> i);
        System.out.println(m);
        Matrix result = m.transpose();
        System.out.println(result);

        double [] expectedValues = {0,3,1,4,2,5};
        Matrix expected = new Matrix(3, 2, i -> expectedValues[i]);
        assertEquals(result, expected);
    }

    @Test
    public void testAddIncrement(){
        Matrix output = new Matrix(5, 6, i -> random.nextDouble());

        int row = 3;
        int col = 2;
        final double increment = 10;

        Matrix result = output.addIncrement(row, col, increment);

        System.out.println(output);
        System.out.println(result);
    }

    @Test
    public void testSoftmax(){
        Matrix output = new Matrix(3, 2, i -> random.nextDouble());

        System.out.println(output);
        Matrix result = output.softmax();

        System.out.println(result);
    }

    @Test
    public void testSumColumns(){
        Matrix m = new Matrix(4, 5, i -> i);

        Matrix result = m.sumColumns();

        System.out.println(m);
        System.out.println(result);
    }

    @Test
    public void testFormMatrix(){
        Matrix m1 = new Matrix(200,3000, i -> i+1);
        Matrix m2 = new Matrix(3000,400, i -> i*2);

        //System.out.println(m1);
        //System.out.println(m2);

        //double[] expectedValues = {64, 76, 88, 100, 136, 166, 196, 226};
        //Matrix expectedResult = new Matrix(2, 4 , i -> expectedValues[i]);

//        for (int i = 0; i < matrix.length; i++){     //print result
//            for (int j = 0; j <matrix[0].length; j++){
//                System.out.print(matrix[i][j] + "  ");
//
//                if ((j + 1) % matrix[0].length == 0) {
//                    System.out.println("\n");
//                }
//            }
//        }

        long start = System.currentTimeMillis();
        Matrix result = m1.multiply(m2);
        long end = System.currentTimeMillis();

        System.out.printf("Matrix multiplication time is %dms", end - start);

        //assertEquals(result, expectedResult);
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
