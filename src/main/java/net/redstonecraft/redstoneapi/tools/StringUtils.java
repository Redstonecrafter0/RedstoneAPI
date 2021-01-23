package net.redstonecraft.redstoneapi.tools;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static final char[] DEFAULT_WHITELISTED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"§$%&/()=?^°,.-;:_µ€@üöäÜÖÄ#+'*~<>| \\ß{[]}".toCharArray();

    public static String sameChar(char a, int c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < c; i++) {
            sb.append(a);
        }
        return sb.toString();
    }

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

}
