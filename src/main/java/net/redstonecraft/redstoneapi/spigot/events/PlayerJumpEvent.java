package net.redstonecraft.redstoneapi.spigot.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is fired when a player jumps
 *
 * @author Redstonecrafter0
 * @since 1.1
 * */
public class PlayerJumpEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Location from;
    private final Location to;

    public PlayerJumpEvent(Player player, Location from, Location to) {
        this.player = player;
        this.from = from;
        this.to = to;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

}
