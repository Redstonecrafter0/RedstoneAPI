package net.redstonecraft.redstoneapi.tools;

public class StringUtils {

    public static String sameChar(char a, int c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < c; i++) {
            sb.append(a);
        }
        return sb.toString();
    }

}
