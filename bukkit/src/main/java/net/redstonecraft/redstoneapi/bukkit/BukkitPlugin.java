package net.redstonecraft.redstoneapi.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A better base class for spigot plugins.
 * Use {@link BukkitPlugin#onEnabled()} instead of {@link JavaPlugin#onEnable()} for {@link BukkitPlugin#onServerLoaded()} to function
 *
 * @author Redstonecrafter0
 * @since 1.1
 * */
public abstract class BukkitPlugin extends JavaPlugin {

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

    /**
     * This is called when the first server tick eplapsed
     * */
    public void onServerLoaded() {
    }

    /**
     * @deprecated use {@link BukkitPlugin#registerListeners(Listener...)} instead
     * @see BukkitPlugin#registerListeners(Listener...)
     * */
    @Deprecated
    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public void registerListeners(Listener... listener) {
        for (Listener i : listener) {
            registerListener(i);
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

}
