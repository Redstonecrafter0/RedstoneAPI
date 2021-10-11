package net.redstonecraft.redstoneapi.bungeecord.manager;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.redstonecraft.redstoneapi.bungeecord.obj.User;
import net.redstonecraft.redstoneapi.core.mojangapi.MojangAPI;
import net.redstonecraft.redstoneapi.core.mojangapi.MojangProfile;

import java.util.UUID;

/**
 * Bungeecord UserManager to manage offline players
 *
 * @author Redstonecrafter0
 * @since 1.1
 * */
public class UserManager {

    private boolean fetchOnMissing;
    private int persistance;

    /**
     * Create a new UserManager instance
     *
     * @param sql SQL instance to safe and load from / to
     * @param fetchOnMissing whether to fetch from the {@link MojangAPI} when the user is not registered
     * @param persistance how long the username is valid in MILLISECONDS
     * */
    public UserManager(Object sql, boolean fetchOnMissing, int persistance) {
//        this.sql = sql;
        this.fetchOnMissing = fetchOnMissing;
//        this.sql.update("CREATE TABLE IF NOT EXISTS user (uuid text, playername text, validtime text)");
    }

    /**
     * Get a User from the playername
     *
     * @param playerName the players name
     * @return the user object
     * */
    public User getUser(String playerName) {
//        PreparedStatement ps = sql.prepareStatement("SELECT * FROM user WHERE LOWER(playername) = ?");
//        try {
//            ps.setString(1, playerName.toLowerCase());
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                if (fetchOnMissing) {
//                    if (Long.parseLong(rs.getString("validtime")) > System.currentTimeMillis()) {
//                        return new User(UUID.fromString(rs.getString("uuid")), rs.getString("playername"));
//                    } else {
//                        return fetchUser(playerName);
//                    }
//                } else {
//                    return new User(UUID.fromString(rs.getString("uuid")), rs.getString("playername"));
//                }
//            } else {
//                return fetchOnMissing ? fetchUser(playerName) : null;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    /**
     * Get a User from the {@link UUID}
     *
     * @param uuid the players uuid
     * @return the user object
     * */
    public User getUser(UUID uuid) {
//        PreparedStatement ps = sql.prepareStatement("SELECT * FROM user WHERE uuid = ?");
//        try {
//            ps.setString(1, uuid.toString());
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                if (fetchOnMissing) {
//                    if (Long.parseLong(rs.getString("validtime")) > System.currentTimeMillis()) {
//                        return new User(UUID.fromString(rs.getString("uuid")), rs.getString("playername"));
//                    } else {
//                        return fetchUser(uuid);
//                    }
//                } else {
//                    return new User(UUID.fromString(rs.getString("uuid")), rs.getString("playername"));
//                }
//            } else {
//                return fetchOnMissing ? fetchUser(uuid) : null;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    /**
     * Get a User from the {@link ProxiedPlayer} instance
     *
     * @param player the players name
     * @return the user object
     * */
    public User getUser(ProxiedPlayer player) {
        updateUser(player.getUniqueId(), player.getName());
        return new User(player.getUniqueId(), player.getName());
    }

    /**
     * Get a User from a {@link PendingConnection} instance
     *
     * @param player the players name
     * @return the user object
     * */
    public User getUser(PendingConnection player) {
        updateUser(player.getUniqueId(), player.getName());
        return new User(player.getUniqueId(), player.getName());
    }

    /**
     * Check if a User is registered. Maybe unnecessary
     *
     * @param user an User instance
     * @return whether the User is registered
     * */
    public Boolean isRegistered(User user) {
//        PreparedStatement ps = sql.prepareStatement("SELECT * FROM user WHERE uuid = ?");
//        try {
//            ps.setString(1, user.getUniqueId().toString());
//            ResultSet rs = ps.executeQuery();
//            return rs.next();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    /**
     * Check if a User is registered by his {@link UUID}
     *
     * @param uuid the players uuid
     * @return whether the User is registered
     * */
    public Boolean isRegistered(UUID uuid) {
//        PreparedStatement ps = sql.prepareStatement("SELECT * FROM user WHERE uuid = ?");
//        try {
//            ps.setString(1, uuid.toString());
//            ResultSet rs = ps.executeQuery();
//            return rs.next();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    /**
     * Check if a User is registered by his playername
     *
     * @param playerName the players name
     * @return whether the User is registered
     * */
    public Boolean isRegistered(String playerName) {
//        PreparedStatement ps = sql.prepareStatement("SELECT * FROM user WHERE LOWER(playername) = ?");
//        try {
//            ps.setString(1, playerName.toLowerCase());
//            ResultSet rs = ps.executeQuery();
//            return rs.next();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    /**
     * Register a new {@link User} if he doesn't exist yet else update his playername
     *
     * @param uuid the players uuid
     * @param playerName the players name
     * */
    public void updateUser(UUID uuid, String playerName) {
//        PreparedStatement ps = sql.prepareStatement("SELECT * FROM user WHERE uuid = ?");
//        try {
//            ps.setString(1, uuid.toString());
//            ResultSet rs = ps.executeQuery();
//            PreparedStatement ps1;
//            if (rs.next()) {
//                ps1 = sql.prepareStatement("UPDATE user SET playername = ?, validtime = ? WHERE uuid = ?");
//                ps1.setString(1, playerName);
//                ps1.setString(2, String.valueOf(System.currentTimeMillis() + persistance));
//                ps1.setString(3, uuid.toString());
//            } else {
//                ps1 = sql.prepareStatement("INSERT INTO user VALUES (?, ?, ?)");
//                ps1.setString(1, uuid.toString());
//                ps1.setString(2, playerName);
//                ps1.setString(3, String.valueOf(System.currentTimeMillis() + persistance));
//            }
//            ps1.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Fetch the User from the {@link MojangAPI} by the playername and update him
     *
     * @param playername the players name
     * @return the user object
     * */
    private User fetchUser(String playername) {
        UUID uuid = MojangAPI.getUniqueIdByName(playername);
        if (uuid != null) {
            updateUser(uuid, playername);
            return new User(uuid, playername);
        } else {
            return null;
        }
    }

    /**
     * Fetch the User from the {@link MojangAPI} by the players {@link UUID} and update him
     *
     * @param uuid the players uuid
     * @return the user object
     * */
    private User fetchUser(UUID uuid) {
        MojangProfile profile = MojangAPI.getProfile(uuid);
        if (profile != null) {
            updateUser(profile.getUniqueId(), profile.getName());
            return new User(profile.getUniqueId(), profile.getName());
        } else {
            return null;
        }
    }

    /**
     * Whether the UserManager is fetching from the {@link MojangAPI} when necessary
     *
     * @return whether its enabled
     * */
    public boolean isFetchOnMissing() {
        return fetchOnMissing;
    }

    /**
     * Set whether to fetch the {@link User} from the {@link MojangAPI} when necessary
     *
     * @param fetchOnMissing the value to set
     * */
    public void setFetchOnMissing(boolean fetchOnMissing) {
        this.fetchOnMissing = fetchOnMissing;
    }

    /**
     * How long the playername is valid
     *
     * @return value
     * */
    public int getPersistance() {
        return persistance;
    }

    /**
     * Set the valid time for playernames
     *
     * @param persistance valid time im MILLISECONDS
     * */
    public void setPersistance(int persistance) {
        this.persistance = persistance;
    }

}
