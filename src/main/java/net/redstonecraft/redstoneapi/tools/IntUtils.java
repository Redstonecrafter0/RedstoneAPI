package net.redstonecraft.redstoneapi.tools;

/**
 * A small utils class for numbers
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class IntUtils {

    /**
     * Shortcut for a random integer between 2 numbers
     *
     * @param a a number
     * @param b b number
     *
     * @return random number between min and max
     * */
    public static int random(int a, int b) {
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        return min + (int) (Math.random() * (max - min));
    }

    /**
     * Small utility for rounding a number to a specifix position
     *
     * @param num number to round
     * @param d position to round
     *
     * @return rounded number
     * */
    public static double round(double num, int d) {
        return Math.round(num * (double) Math.pow(10, d)) / (double) Math.pow(10, d);
    }

}
