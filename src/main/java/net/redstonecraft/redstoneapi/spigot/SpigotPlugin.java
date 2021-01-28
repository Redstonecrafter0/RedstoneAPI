package net.redstonecraft.redstoneapi.spigot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class SpigotPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        onEnabled();
        new BukkitRunnable() {
            @Override
            public void run() {
                onServerLoaded();
            }
        }.runTaskLater(this, 1);
    }

    public void onEnabled() {
    }

    public void onServerLoaded() {
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
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

}
