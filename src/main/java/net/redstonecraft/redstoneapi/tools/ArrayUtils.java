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

}
