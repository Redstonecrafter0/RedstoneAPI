package net.redstonecraft.redstoneapi.spigot.listeners;

import net.redstonecraft.redstoneapi.spigot.events.PlayerJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class JumpListener implements Listener {

    private final HashMap<Player, Boolean> onGround = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (onGround.get(event.getPlayer()) != null) {
            if (onGround.get(event.getPlayer()) && !((Entity) event.getPlayer()).isOnGround() && event.getFrom().getY() < event.getTo().getY()) {
                Bukkit.getPluginManager().callEvent(new PlayerJumpEvent(event.getPlayer(), event.getFrom(), event.getTo()));
            }
        }
        onGround.put(event.getPlayer(), event.getPlayer().isOnGround());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        onGround.remove(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        onGround.remove(event.getPlayer());
    }

}
