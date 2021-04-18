package net.redstonecraft.redstoneapi.bungee.obj;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Objects;
import java.util.UUID;

/**
 * User object to get the {@link UUID} and playername
 *
 * @author Redstonecrafter0
 * @since 1.1
 * */
public class User {

    private final UUID uuid;
    private final String playerName;

    public User(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    /**
     * Get the players uuid
     *
     * @return players uuid
     * */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Get the playername
     *
     * @return playername
     * */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Check if the player is online
     *
     * @return whether the player is online
     * */
    public boolean isOnline() {
        return ProxyServer.getInstance().getPlayer(uuid) != null;
    }

    /**
     * Get a ProxiedPlayer from the User instance
     *
     * @return ProxiedPlayer instance
     * */
    public ProxiedPlayer getOnlinePlayer() {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(uuid, user.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + uuid.toString() +
                ", playerName='" + playerName + '\'' +
                '}';
    }
}
