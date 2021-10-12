package net.redstonecraft.redstoneapi.core;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * A small utils class for numbers
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
@SuppressWarnings("unused")
public class NumberUtils {

    /**
     * Shortcut for a random integer between 2 numbers
     *
     * @param a a number
     * @param b b number
     * @return random number between min and max
     * */
    public static int random(int a, int b) {
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        return min + (int) (Math.random() * (max - min));
    }

    /**
     * Shortcut for a random integer between 2 numbers using {@link SecureRandom}
     *
     * @param a a number
     * @param b b number
     * @return random number between min and max
     * */
    public static int secureRandom(int a, int b) {
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        if (min == max) {
            return min;
        }
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            return random.nextInt(min, max + 1);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Small utility for rounding a number to a specific position
     *
     * @see java.text.DecimalFormat for displaying
     * @param num number to round
     * @param d position to round
     * @return rounded number
     * */
    public static double round(double num, int d) {
        return Math.round(num * Math.pow(10, d)) / Math.pow(10, d);
    }

    /**
     * @param inputValues the values to base on
     * @return an array of doubles that are between 0 and 1 calculated on base of the input
     * */
    public static double[] normalize(double... inputValues) {
        if (inputValues.length < 1) {
            return new double[0];
        }
        double[] values = new double[inputValues.length];
        System.arraycopy(inputValues, 0, values, 0, inputValues.length);
        for (int i = 0; i < values.length; i++) {
            values[i] = Math.abs(values[i]);
        }
        double maxValue = 0;
        for (double i : values) {
            if (i > maxValue) {
                maxValue = i;
            }
        }
        if (maxValue == 0) {
            return new double[values.length];
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i] / maxValue;
        }
        return values;
    }

    /**
     * @param s string to convert
     * @return the parsed number or null
     */
    public static Integer toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * @param s string to convert
     * @return the parsed number or null
     */
    public static Integer hexToInt(String s) {
        try {
            return Integer.parseInt(s, 16);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * @param s string to convert
     * @return the parsed number or null
     */
    public static Integer binToInt(String s) {
        try {
            return Integer.parseInt(s, 2);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * @param s string to convert
     * @return the parsed number or null
     */
    public static Long toLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * @param s string to convert
     * @return the parsed number or null
     */
    public static Long hexToLong(String s) {
        try {
            return Long.parseLong(s, 16);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * @param s string to convert
     * @return the parsed number or null
     */
    public static Long binToLong(String s) {
        try {
            return Long.parseLong(s, 2);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * @param s string to convert
     * @return the parsed number or null
     */
    public static Float toFloat(String s) {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * @param s string to convert
     * @return the parsed number or null
     */
    public static Double toDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

}
