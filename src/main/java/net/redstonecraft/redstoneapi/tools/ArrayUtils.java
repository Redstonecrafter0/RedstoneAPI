package net.redstonecraft.redstoneapi.tools;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * @author Redstonecrafter0
 * @since 1.4
 */
public class ArrayUtils {

    public static <T> T[] arrayOf(T value, int length) {
        return arrayOf((Class<T>) value.getClass(), value, length);
    }

    public static <T> T[] arrayOf(Class<T> clazz, T value, int length) {
        T[] arr = (T[]) Array.newInstance(clazz, length);
        Arrays.fill(arr, value);
        return arr;
    }

    public static boolean[] arrayOf(boolean b, int length) {
        boolean[] arr = new boolean[length];
        Arrays.fill(arr, b);
        return arr;
    }

    public static char[] arrayOf(char c, int length) {
        char[] arr = new char[length];
        Arrays.fill(arr, c);
        return arr;
    }

    public static byte[] arrayOf(byte b, int length) {
        byte[] arr = new byte[length];
        Arrays.fill(arr, b);
        return arr;
    }
    
    public static int[] arrayOf(int i, int length) {
        int[] arr = new int[length];
        Arrays.fill(arr, i);
        return arr;
    }

    public static float[] arrayOf(float f, int length) {
        float[] arr = new float[length];
        Arrays.fill(arr, f);
        return arr;
    }
    
    public static long[] arrayOf(long l, int length) {
        long[] arr = new long[length];
        Arrays.fill(arr, l);
        return arr;
    }

    public static double[] arrayOf(double d, int length) {
        double[] arr = new double[length];
        Arrays.fill(arr, d);
        return arr;
    }

}
