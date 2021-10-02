package net.redstonecraft.redstoneapi.tools;

/**
 * This class changes the use of try catch to returning null when it can't be parsed.
 *
 * @author Redstonecrafter0
 * @since 1.5
 */
public class Parser {

    public static Integer toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Integer hexInt(String s) {
        try {
            return Integer.parseInt(s, 16);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Integer binInt(String s) {
        try {
            return Integer.parseInt(s, 2);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Long toLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Long hexLong(String s) {
        try {
            return Long.parseLong(s, 16);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Long binLong(String s) {
        try {
            return Long.parseLong(s, 2);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Float toFloat(String s) {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Double toDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

}
