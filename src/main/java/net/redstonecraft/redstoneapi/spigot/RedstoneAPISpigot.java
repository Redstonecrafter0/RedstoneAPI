package net.redstonecraft.redstoneapi.spigot;

import net.redstonecraft.redstoneapi.RedstoneAPI;
import net.redstonecraft.redstoneapi.spigot.listeners.JumpListener;
import net.redstonecraft.redstoneapi.spigot.listeners.UpdateListener;
import net.redstonecraft.redstoneapi.tools.StringUtils;
import org.bukkit.Bukkit;

import java.io.File;

public class RedstoneAPISpigot extends SpigotPlugin {

    public static final String prefix = renderColors("&7[&9Redstone&cAPI&7] &r");

    @Override
    public void onLoad() {
        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 30 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cAPI&f-&6Spigot &fv" + getDescription().getVersion() + " &aloaded. &b║",
                "&b║" + StringUtils.sameChar(' ', 30 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 10 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 30 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            Bukkit.getConsoleSender().sendMessage(renderColors(i));
        }
    }

    @Override
    public void onEnabled() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        try {
            Metrics metrics = new Metrics(this, 10145);
        } catch (Exception ignored) {
            getLogger().warning(prefix + renderColors("&cError while initializing bStats."));
        }
        Bukkit.getPluginManager().registerEvents(new JumpListener(), this);
        if (getConfig().getBoolean("update.notify.adminjoin")) {
            Bukkit.getPluginManager().registerEvents(new UpdateListener(this), this);
        }
        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 31 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cAPI&f-&6Spigot &fv" + getDescription().getVersion() + " &aenabled. &b║",
                "&b║" + StringUtils.sameChar(' ', 31 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 11 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 31 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            Bukkit.getConsoleSender().sendMessage(renderColors(i));
        }
    }

    @Override
    public void onServerLoaded() {
        if (getConfig().getBoolean("update.notify.console")) {
            RedstoneAPI.main(new String[]{"nogui"});
        }
    }

    @Override
    public void onDisable() {
        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 32 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cAPI&f-&6Spigot &fv" + getDescription().getVersion() + " &cdisabled. &b║",
                "&b║" + StringUtils.sameChar(' ', 32 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 12 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 32 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            Bukkit.getConsoleSender().sendMessage(renderColors(i));
        }
    }

}
