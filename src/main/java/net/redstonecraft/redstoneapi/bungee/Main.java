package net.redstonecraft.redstoneapi.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.redstonecraft.redstoneapi.tools.StringUtils;

public class Main extends Plugin {

    @Override
    public void onLoad() {
        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 30 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cAPI&f-&6Bungee &fv" + getDescription().getVersion() + " &aloaded. &b║",
                "&b║" + StringUtils.sameChar(' ', 30 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 10 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 30 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            getLogger().info(renderColors(i));
        }
    }

    @Override
    public void onEnable() {
        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 31 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cAPI&f-&6Bungee &fv" + getDescription().getVersion() + " &aenabled. &b║",
                "&b║" + StringUtils.sameChar(' ', 31 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 11 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 31 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            getLogger().info(renderColors(i));
        }
    }

    @Override
    public void onDisable() {
        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 32 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cAPI&f-&6Bungee &fv" + getDescription().getVersion() + " &cdisabled. &b║",
                "&b║" + StringUtils.sameChar(' ', 32 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 12 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 32 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            getLogger().info(renderColors(i));
        }
    }

    public static String renderColors(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

}
