package net.redstonecraft.redstoneapi.core.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for {@link String}s
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
@SuppressWarnings("unused")
public class StringUtils {

    @SuppressWarnings("SpellCheckingInspection")
    public static final char[] DEFAULT_WHITELISTED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"§$%&/()=?^°,.-;:_µ€@üöäÜÖÄ#+'*~<>| \\ß{[]}".toCharArray();

    /**
     * Get a {@link String} of a with the length of c
     *
     * @see String#repeat(int) to use instead
     * @param a char to use
     * @param c length
     * @return {@link String} of same char
     * */
    public static String sameChar(char a, int c) {
        return String.valueOf(a).repeat(Math.max(0, c));
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

    private static List<Character> arrayToList(char[] arr) {
        List<Character> list = new ArrayList<>();
        for (char i : arr) {
            list.add(i);
        }
        return list;
    }

    private static void add(List<String> list, String str) {
        if (!str.equals("")) {
            list.add(str);
        }
    }

    /**
     * Parse arguments split by spaces but joining them when used with quotation marks.
     *
     * @param str the string to parse
     *
     * @return the parsed args
     * */
    public static String[] parseArgs(String str) {
        List<String> list = new ArrayList<>();
        boolean longArg = false;
        boolean escaped = false;
        StringBuilder buffer = new StringBuilder(str.length());
        for (char i : str.toCharArray()) {
            switch (i) {
                case '"' -> {
                    if (escaped) {
                        buffer.append('"');
                        escaped = false;
                    } else {
                        add(list, buffer.toString());
                        buffer = new StringBuilder();
                        longArg = !longArg;
                    }
                }
                case ' ' -> {
                    if (longArg) {
                        buffer.append(i);
                    } else {
                        add(list, buffer.toString());
                        buffer = new StringBuilder();
                    }
                }
                case '\\' -> {
                    if (escaped) {
                        buffer.append('\\');
                    }
                    escaped = !escaped;
                }
                default -> buffer.append(i);
            }
        }
        add(list, buffer.toString());
        String[] converted = new String[list.size()];
        for (int i = 0; i < converted.length; i++) {
            converted[i] = list.get(i);
        }
        return converted;
    }

    /**
     * Get the Stacktrace as a {@link String} from any {@link Exception}
     *
     * @param exception the exception to get the stacktrace from
     *
     * @return the stacktrace as {@link String}
     * */
    public static String stringFromError(Throwable exception) {
        Writer w = new StringWriter();
        try {
            PrintWriter pw = new PrintWriter(w);
            exception.printStackTrace(pw);
            pw.flush();
            pw.close();
            w.close();
        } catch (IOException ignored) {
        }
        return w.toString();
    }

}
