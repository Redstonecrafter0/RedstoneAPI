package net.redstonecraft.redstoneapi.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for {@link String}
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class StringUtils {

    public static final char[] DEFAULT_WHITELISTED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"§$%&/()=?^°,.-;:_µ€@üöäÜÖÄ#+'*~<>| \\ß{[]}".toCharArray();

    /**
     * Get a {@link String} of a c long
     *
     * @param a char to use
     * @param c length
     *
     * @return {@link String} of same char
     * */
    public static String sameChar(char a, int c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < c; i++) {
            sb.append(a);
        }
        return sb.toString();
    }

    /**
     * Check if all chars of the input {@link String} are in the char array
     *
     * @param input the {@link String} to check
     * @param chars the array that contains all valid chars. There is a default list {@link StringUtils#DEFAULT_WHITELISTED_CHARS}
     *
     * @return the result
     * */
    public static boolean isValid(String input, char[] chars) {
        List<Character> list = arrayToList(chars);
        for (char i : input.toCharArray()) {
            if (!list.contains(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Internal method to work with on {@link StringUtils#isValid(String, char[])}
     *
     * @param arr char array to convert
     *
     * @return the array as {@link List<Character>}
     * */
    private static List<Character> arrayToList(char[] arr) {
        List<Character> list = new ArrayList<>();
        for (char i : arr) {
            list.add(i);
        }
        return list;
    }

}
