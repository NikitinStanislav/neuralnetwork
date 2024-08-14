package calculus;

import java.util.function.DoubleFunction;
import java.util.function.Function;

public class Calculus {

    private static final double INC = 0.00001;

    public static double func1(double x){
        return 3.7 * x + 5.3;
    }

    public static double func2(double x){
        return x * x;
    }


    /**we want to take change in output and divide it
     by change in X, and it gives us the measure of gradient of the function at that particular point*/
    public static double differentiate (DoubleFunction<Double> func, double x){ //не двойная функция, а функция из дабла

        double output1 = func.apply(x);
        double output2 = func.apply(x + INC);

        return (output2 - output1) / INC;
    }

    public static void main(String[] args) {
        for (double x = -2; x < 2; x += 0.1){

            //double gradient = differentiate(Calculus::func1, x);   //for linear function func1 you will see sema gradient
            double gradient = differentiate(Calculus::func2, x); //just a random rule: gradient of x^2 is 2x

            System.out.printf("%.2f\t%.2f\n", x, gradient);
        }
    }
}
