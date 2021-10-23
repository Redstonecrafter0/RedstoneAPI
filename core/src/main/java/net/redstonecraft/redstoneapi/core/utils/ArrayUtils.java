package net.redstonecraft.redstoneapi.core.utils;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * A toolkit to create prefilled arrays of a given value.
 *
 * @author Redstonecrafter0
 * @since 1.4
 */
@SuppressWarnings("unused")
public class ArrayUtils {

    /**
     * Creates a new array of the given value with the given length filled with the given value.
     *
     * @param value to fill the array with
     * @param length of the array
     * @param <T> type of the array
     * @return an array of the given length filled with the given value
     */
    public static <T> T[] arrayOf(T value, int length) {
        return arrayOf((Class<T>) value.getClass(), value, length);
    }

    /**
     * Creates a new array of the given class with the given length filled with the given value.
     *
     * @param clazz the class[] of the array
     * @param value to fill the array with
     * @param length of the array
     * @param <T> type of the array
     * @return an array of the given length filled with the given value
     */
    public static <T> T[] arrayOf(Class<T> clazz, T value, int length) {
        T[] arr = (T[]) Array.newInstance(clazz, length);
        Arrays.fill(arr, value);
        return arr;
    }

    /**
     * Creates a new array of the given value with the given length filled with the given value.
     *
     * @param b value to fill the array with
     * @param length of the array
     * @return an array of the given length filled with the given value
     */
    public static boolean[] arrayOf(boolean b, int length) {
        boolean[] arr = new boolean[length];
        Arrays.fill(arr, b);
        return arr;
    }

    /**
     * Creates a new array of the given value with the given length filled with the given value.
     *
     * @param c value to fill the array with
     * @param length of the array
     * @return an array of the given length filled with the given value
     */
    public static char[] arrayOf(char c, int length) {
        char[] arr = new char[length];
        Arrays.fill(arr, c);
        return arr;
    }

    /**
     * Creates a new array of the given value with the given length filled with the given value.
     *
     * @param b value to fill the array with
     * @param length of the array
     * @return an array of the given length filled with the given value
     */
    public static byte[] arrayOf(byte b, int length) {
        byte[] arr = new byte[length];
        Arrays.fill(arr, b);
        return arr;
    }

    /**
     * Creates a new array of the given value with the given length filled with the given value.
     *
     * @param i value to fill the array with
     * @param length of the array
     * @return an array of the given length filled with the given value
     */
    public static int[] arrayOf(int i, int length) {
        int[] arr = new int[length];
        Arrays.fill(arr, i);
        return arr;
    }

    /**
     * Creates a new array of the given value with the given length filled with the given value.
     *
     * @param f value to fill the array with
     * @param length of the array
     * @return an array of the given length filled with the given value
     */
    public static float[] arrayOf(float f, int length) {
        float[] arr = new float[length];
        Arrays.fill(arr, f);
        return arr;
    }

    /**
     * Creates a new array of the given value with the given length filled with the given value.
     *
     * @param l value to fill the array with
     * @param length of the array
     * @return an array of the given length filled with the given value
     */
    public static long[] arrayOf(long l, int length) {
        long[] arr = new long[length];
        Arrays.fill(arr, l);
        return arr;
    }

    /**
     * Creates a new array of the given value with the given length filled with the given value.
     *
     * @param d value to fill the array with
     * @param length of the array
     * @return an array of the given length filled with the given value
     */
    public static double[] arrayOf(double d, int length) {
        double[] arr = new double[length];
        Arrays.fill(arr, d);
        return arr;
    }

}
