package net.redstonecraft.redstoneapi.tools;

public class IntUtils {

    public static int random(int min, int max) {
        return min + (int) (Math.random() * (max - min));
    }

    public static double round(double num, int d) {
        return Math.round(num * (double) Math.pow(10, d)) / (double) Math.pow(10, d);
    }

}
