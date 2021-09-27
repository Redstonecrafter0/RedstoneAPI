package net.redstonecraft.redstoneapi.bukkit.listeners;

import net.redstonecraft.redstoneapi.info.RedstoneAPI;
import net.redstonecraft.redstoneapi.bukkit.plugin.RedstoneAPIBukkit;
import net.redstonecraft.redstoneapi.core.Version;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class UpdateListener implements Listener {

    private Version newVersion = null;

    private final RedstoneAPIBukkit plugin;

    public UpdateListener(RedstoneAPIBukkit plugin) {
        this.plugin = plugin;
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    RedstoneAPI.Update update = RedstoneAPI.getUpdate();
                    if (update.getState().equals(RedstoneAPI.Update.State.NEW_VERSION_AVAILABLE)) {
                        newVersion = update.getVersion();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(this.plugin, 0, 20 * 60 * 60 * 24);
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getPlayer().hasPermission("redstoneapi.admin.notifyupdate") && newVersion != null) {
                    event.getPlayer().sendMessage(RedstoneAPIBukkit.PREFIX + RedstoneAPIBukkit.renderColors("&aThere is a newer version [" + newVersion + "] of this plugin available. Current version is [" + RedstoneAPI.getVersion().toString() + "]."));
                }
            }
        }.runTaskLater(plugin, 3);
    }

}
