package neuralnetwork;

import matrix.Matrix;

import java.util.function.Function;

public class Approximator {


    public static Matrix gradient(Matrix input, Function<Matrix, Matrix> transform) {

        final double INC = 0.00001;
        Matrix loss1 = transform.apply(input); //Function<T, R>, <T> – the type of the input to the function <R> – the type of the result of the function

        assert loss1.getColumns() == input.getColumns() : "Input/loss columns not equal";
        assert loss1.getRows() == 1 : "Transform doesn't return one single row";

        //System.out.println("input\n\n"+input);
        System.out.println("loss1\n\n"+loss1);

        Matrix result = new Matrix(input.getRows(), input.getColumns(), i -> 0);

        input.forEach((row, column, index, value) -> {
           Matrix incremented = input.addIncrement(row, column, INC);   //one single value was incremented, rest of the matrix the same

            Matrix loss2 = transform.apply(incremented);
            //если инкреметировано не ожидаемое значение, то кросс энтропия не изменится от этого,
            // мы ж все левые значения умножаем на 0

            //System.out.println("loss2\n\n"+loss2);

            double rate =  (loss2.get(column) - loss1.get(column))/INC;
            //если инкреметировано не ожидаемое значение, то кросс энтропия не изменится от этого,
            // мы ж все левые значения умножаем на 0
            // в этом случае loss1 == loss2 -> rate == 0

            result.set(row, column, rate);
        });

//
//        System.out.println("input2\n\n"+input);
//        System.out.println("loss2\n\n"+loss1);

           return result;
        }

        public static Matrix weightGradient(Matrix weights, Function<Matrix, Matrix> transform) { //если не понятно смотри выше

            final double INC = 0.00001;
            Matrix loss1 = transform.apply(weights);

            System.out.println("loss1\n"+loss1);

            Matrix result = new Matrix(weights.getRows(), weights.getColumns(), i -> 0);

            weights.forEach((row, column, index, value) -> {
                Matrix incremented = weights.addIncrement(row, column, INC);   //one single value was incremented, rest of the matrix the same

                Matrix loss2 = transform.apply(incremented);

                double rate =  (loss2.get(0) - loss1.get(0))/INC; //только это изменилось судя по всему

                result.set(row, column, rate);
            });

            return result;
        }
}
