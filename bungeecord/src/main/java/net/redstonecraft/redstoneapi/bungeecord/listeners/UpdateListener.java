package net.redstonecraft.redstoneapi.bungeecord.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.redstonecraft.redstoneapi.info.RedstoneAPI;
import net.redstonecraft.redstoneapi.bungeecord.plugin.RedstoneAPIBungee;
import net.redstonecraft.redstoneapi.core.Version;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class UpdateListener implements Listener {

    private static Version newVersion = null;

    private final RedstoneAPIBungee plugin;

    public UpdateListener(RedstoneAPIBungee plugin) {
        this.plugin = plugin;
        ProxyServer.getInstance().getScheduler().schedule(this.plugin, () -> {
            try {
                RedstoneAPI.Update update = RedstoneAPI.getUpdate();
                if (update.getState().equals(RedstoneAPI.Update.State.NEW_VERSION_AVAILABLE)) {
                    UpdateListener.newVersion = update.getVersion();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.DAYS);
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
            if (event.getPlayer().hasPermission("redstoneapi.admin.notifyupdate") && newVersion != null) {
                event.getPlayer().sendMessage(RedstoneAPIBungee.prefix + RedstoneAPIBungee.renderColors("&aThere is a newer version [" + newVersion.toString() + "] of this plugin available. Current version is [" + RedstoneAPI.getVersion().toString() + "]."));
            }
        }, 1, TimeUnit.SECONDS);
    }

}
