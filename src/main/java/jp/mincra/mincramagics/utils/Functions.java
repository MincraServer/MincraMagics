package jp.mincra.mincramagics.utils;

public class Functions {
    /**
     * Logistic function. f(x) = l / (1 + exp(-k * (x - x0)))
     * @param x input value
     * @param k steepness of the curve
     * @param x0 x-value of the sigmoid's midpoint
     * @return the logistic function value at x
     */
    public static double logistic(double x, double l, double k, double x0) {
        return l / (1 + Math.exp(-k * (x - x0)));
    }
}
