package net.redstonecraft.redstoneapi.bukkit.plugin;

import net.redstonecraft.redstoneapi.info.RedstoneAPI;
import net.redstonecraft.redstoneapi.bukkit.BukkitPlugin;
import net.redstonecraft.redstoneapi.bukkit.listeners.JumpListener;
import net.redstonecraft.redstoneapi.bukkit.listeners.UpdateListener;
import net.redstonecraft.redstoneapi.bukkit.manager.GuiInventoryManager;
import net.redstonecraft.redstoneapi.core.utils.StringUtils;
import org.bukkit.Bukkit;

import java.io.File;

/**
 * The spigot plugin of the RedstoneAPI for providing the events and managers
 *
 * @author Redstonecrafter0
 * @since 1.1
 * */
public class RedstoneAPIBukkit extends BukkitPlugin {

    public static final String PREFIX = renderColors("&7[&9Redstone&cAPI&7] &r");
    private static final int CURRENT_CONFIG_VERSION = 1;
    private static RedstoneAPIBukkit INSTANCE;
    private GuiInventoryManager guiInventoryManager = null;

    @Override
    public void onLoad() {
        INSTANCE = this;
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
//        if (!getConfig().getKeys(true).contains("version")) {
//            Bukkit.getConsoleSender().sendMessage(prefix + renderColors("&cThis version of the RedstoneAPI uses a new config version. The old config will be backed up and a new config will be created."));
//            try {
//                Files.copy(new File(getDataFolder(), "config.yml").toPath(), new File(getDataFolder(), "config.old").toPath());
//                Files.delete(new File(getDataFolder(), "config.yml").toPath());
//                saveDefaultConfig();
//                reloadConfig();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else if (getConfig().getInt("version") < CURRENT_CONFIG_VERSION) {
//            Bukkit.getConsoleSender().sendMessage(prefix + renderColors("&cThis version of the RedstoneAPI uses a new config version. The old config will be backed up and a new config will be created."));
//            try {
//                Files.copy(new File(getDataFolder(), "config.yml").toPath(), new File(getDataFolder(), "config.old").toPath());
//                Files.delete(new File(getDataFolder(), "config.yml").toPath());
//                saveDefaultConfig();
//                reloadConfig();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        try {
            Metrics metrics = new Metrics(this, 10145);
        } catch (Throwable ignored) {
            getLogger().warning(PREFIX + renderColors("&4Error while initializing bStats."));
        }
        registerListeners(new JumpListener());
        if (getConfig().getBoolean("update.notify.adminjoin")) {
            registerListeners(new UpdateListener(this));
        }
        if (getConfig().getBoolean("manager.guiInventory.enabled")) {
            guiInventoryManager = new GuiInventoryManager();
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

    public static RedstoneAPIBukkit getInstance() {
        return INSTANCE;
    }

    public GuiInventoryManager getInventoryManager() {
        return guiInventoryManager;
    }

}
