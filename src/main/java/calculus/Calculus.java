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

    public static double func3(double y1, double y2) {
        return y1 * y2 + 4.7 * y1;
    }

    public static double func4(double x) {
        return func1(x) * func2(x) + 4.7 * func1(x);
    }

    /**we want to take change in output and divide it
     by change in X, and it gives us the measure of gradient of the function at that particular point*/
    public static double differentiate (DoubleFunction<Double> func, double x){ //не двойная функция, а функция из дабла

        double output1 = func.apply(x);
        double output2 = func.apply(x + INC);

        return (output2 - output1) / INC;
    }

    public static void main(String[] args) {

        double x = 2.78;
        double y1 = func1(x);
        double y2 = func2(x);
        double z = func3(y1, y2);

        double dy1dx = differentiate(Calculus::func1, x);   //rate of change
        double dy2dx = differentiate(Calculus::func2, x);   //rate of change
        double dzdy1 = differentiate(y -> func3(y, y2), y1);
        double dzdy2 = differentiate(y -> func3(y1, y), y2);

        double dzdxCalculated = (dzdy1 * dy1dx) + (dzdy2 * dy2dx);
        double dzdxApproximated = differentiate(Calculus::func4, x);

        System.out.println(dzdxCalculated);
        System.out.println(dzdxApproximated);



/***
 *      Single variable chain rule example
        double dydx = differentiate(Calculus::func1, x);   //rate of change
        double dzdy = differentiate(Calculus::func2, y);
        double dzdx = differentiate(Calculus::func3, x);

        System.out.println(dydx);
        System.out.println(dzdy);
        System.out.println(dzdx);

        System.out.println(dzdy * dydx);*/




        /*for (double x = -2; x < 2; x += 0.1){

            //double gradient = differentiate(Calculus::func1, x);   //for linear function func1 you will see sema gradient
            double gradient = differentiate(Calculus::func2, x); //just a random rule: gradient of x^2 is 2x

            System.out.printf("%.2f\t%.2f\n", x, gradient);
        }*/
    }
}
