package net.redstonecraft.redstoneapi.bungee;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class BungeecordPlugin extends Plugin {

    private Configuration config;

    /**
     * Get the config
     *
     * @return the config
     * */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Load / reload the config
     * */
    public void loadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Render a {@link String} to get it colored for minecraft by key '&amp;'
     *
     * @param input input string with key '&amp;'
     *
     * @return colored string
     * */
    public static String renderColors(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    /**
     * Registere permissions for permission managers to autocomplete
     *
     * @param permission a array of all permissions
     * */
    public static void registerPermissions(String... permission) {
        for (String i : permission) {
            ProxyServer.getInstance().getConsole().hasPermission(i);
        }
    }

    /**
     * @deprecated use {@link BungeecordPlugin#registerListeners(Listener...)} instead
     * */
    @Deprecated
    public void registerCommand(Command command) {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, command);
    }

    public void registerCommands(Command... command) {
        for (Command i : command) {
            registerCommand(i);
        }
    }

    /**
     * @deprecated use {@link BungeecordPlugin#registerListeners(Listener...)} instead
     * */
    @Deprecated
    public void registerListener(Listener listener) {
        ProxyServer.getInstance().getPluginManager().registerListener(this, listener);
    }

    public void registerListeners(Listener... listener) {
        for (Listener i : listener) {
            registerListener(i);
        }
    }

    public void schedule(Runnable runnable, long wait, long timer, TimeUnit timeunit) {
        ProxyServer.getInstance().getScheduler().schedule(this, runnable, wait, timer, timeunit);
    }

    public void schedule(Runnable runnable, long wait, TimeUnit timeUnit) {
        ProxyServer.getInstance().getScheduler().schedule(this, runnable, wait, timeUnit);
    }

    public void sendConsoleMessage(String message) {
        ProxyServer.getInstance().getConsole().sendMessage(message);
    }

    /**
     * Like the spigot one but the check if the config exists already in included
     *
     * @param defaultConfigName the filename from the resources to copy from
     * */
    public void saveDefaultConfig(String defaultConfigName) {
        File file = new File(getDataFolder(), "config.yml");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                ByteStreams.copy(getResourceAsStream(defaultConfigName), new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
