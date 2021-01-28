package net.redstonecraft.redstoneapi.tools;

import java.awt.*;

public enum MinecraftColors {
    
    BLACK("0", "black", Color.decode("#000000"), Color.decode("#000000")),
    DARK_BLUE("1", "dark_blue", Color.decode("#0000AA"), Color.decode("#00002A")),
    DARK_GREEN("2", "dark_green", Color.decode("#00AA00"), Color.decode("#002A00")),
    DARK_AQUA("3", "dark_aqua", Color.decode("#00AAAA"), Color.decode("#002A2A")),
    DARK_RED("4", "dark_red", Color.decode("#AA0000"), Color.decode("#2A0000")),
    DARK_PURPLE("5", "dark_purple", Color.decode("#AA00AA"), Color.decode("#2A002A")),
    GOLD("6", "gold", Color.decode("#FFAA00"), Color.decode("#2A2A00")),
    GRAY("7", "gray", Color.decode("#AAAAAA"), Color.decode("#2A2A2A")),
    DARK_GRAY("8", "dark_gray", Color.decode("#555555"), Color.decode("#151515")),
    BLUE("9", "blue", Color.decode("#5555FF"), Color.decode("#15153F")),
    GREEN("a", "green", Color.decode("#55FF55"), Color.decode("#153F15")),
    AQUA("b", "aqua", Color.decode("#55FFFF"), Color.decode("#153F3F")),
    RED("c", "red", Color.decode("#FF5555"), Color.decode("#3F1515")),
    LIGHT_PURPLE("d", "light_purple", Color.decode("#FF55FF"), Color.decode("#3F153F")),
    YELLOW("e", "yellow", Color.decode("#FFFF55"), Color.decode("#3F3F15")),
    WHITE("f", "white", Color.decode("#FFFFFF"), Color.decode("#3F3F3F")),
    NONE("", "", Color.decode("#000000"), Color.decode("#000000"));

    public final String key;
    public final String name;
    public final Color foregroundColor;
    public final Color backgroundColor;

    MinecraftColors(String key, String name, Color foregroundColor, Color backgroundColor) {
        this.key = key;
        this.name = name;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
    }

    /**
     * Get the color from the full name
     *
     * @param name full name of the color
     *
     * @return color object
     * @since 1.0
     * */
    public static MinecraftColors getColorByName(String name) {
        for (MinecraftColors i : MinecraftColors.values()) {
            if (name.equals(i.name)) {
                return i;
            }
        }
        return MinecraftColors.NONE;
    }

    /**
     * Get the color from the color key
     *
     * @param key key of the color
     *
     * @return color object
     * @since 1.0
     * */
    public static MinecraftColors getColorByKey(String key) {
        for (MinecraftColors i : MinecraftColors.values()) {
            if (key.equals(i.key)) {
                return i;
            }
        }
        return MinecraftColors.NONE;
    }

}
