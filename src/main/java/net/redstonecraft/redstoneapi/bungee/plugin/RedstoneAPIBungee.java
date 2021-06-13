package net.redstonecraft.redstoneapi.bungee.plugin;

import net.md_5.bungee.api.ProxyServer;
import net.redstonecraft.redstoneapi.RedstoneAPI;
import net.redstonecraft.redstoneapi.bungee.BungeecordPlugin;
import net.redstonecraft.redstoneapi.bungee.listeners.UpdateListener;
import net.redstonecraft.redstoneapi.bungee.listeners.UserListener;
import net.redstonecraft.redstoneapi.bungee.manager.UserManager;
import net.redstonecraft.redstoneapi.sql.MySQL;
import net.redstonecraft.redstoneapi.sql.SQL;
import net.redstonecraft.redstoneapi.sql.SQLTypes;
import net.redstonecraft.redstoneapi.sql.SQLite;
import net.redstonecraft.redstoneapi.tools.StringUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * The RedstoneAPI BungeeCord plugin class
 *
 * @author Redstonecrafter0
 */
public class RedstoneAPIBungee extends BungeecordPlugin {

    public static final String prefix = renderColors("&7[&9Redstone&cAPI&7] &r");

    private static RedstoneAPIBungee instance;

    private SQL sql;
    private UserManager userManager;

    @Override
    public void onLoad() {
        instance = this;
        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 30 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cAPI&f-&6Bungee &fv" + getDescription().getVersion() + " &aloaded. &b║",
                "&b║" + StringUtils.sameChar(' ', 30 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 10 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 30 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            sendConsoleMessage(prefix + renderColors(i));
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig("bungeeconfig.yml");
        loadConfig();
        registerPermissions("redstoneapi.admin.notifyupdate");
        try {
            Metrics metrics = new Metrics(this, 10146);
            metrics.addCustomChart(new Metrics.SimplePie("usermanager_in_use", () -> String.valueOf(getConfig().getBoolean("usermanager.enabled"))));
        } catch (Exception ignored) {
            getLogger().warning(prefix + renderColors("&cError while initializing bStats."));
        }
        if (isUserManagerEnabled()) {
            switch (SQLTypes.valueOf(getConfig().getString("usermanager.storage.type"))) {
                case SQLITE:
                    try {
                        sql = new SQLite(new File(getDataFolder(), getConfig().getString("usermanager.storage.sqlite.name")).getPath());
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case MYSQL:
                    try {
                        sql = new MySQL(getConfig().getString("usermanager.storage.mysql.host"), getConfig().getInt("usermanager.storage.mysql.port"), getConfig().getString("usermanager.storage.mysql.database"), getConfig().getString("usermanager.storage.mysql.username"), getConfig().getString("usermanager.storage.mysql.password"));
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Check the database configuration.");
            }
            userManager = new UserManager(sql, getConfig().getBoolean("usermanager.fetchonmissing"), getConfig().getInt("usermanager.persistance") * 24 * 60 * 60 * 1000);
            registerListeners(new UserListener(userManager));
        }
        if (getConfig().getBoolean("update.notify.adminjoin")) {
            registerListeners(new UpdateListener(this));
        }
        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 31 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cAPI&f-&6Bungee &fv" + getDescription().getVersion() + " &aenabled. &b║",
                "&b║" + StringUtils.sameChar(' ', 31 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 11 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 31 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            ProxyServer.getInstance().getConsole().sendMessage(prefix + renderColors(i));
        }
        if (getConfig().getBoolean("update.notify.console")) {
            schedule(() -> RedstoneAPI.main(new String[]{"nogui"}), 1, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onDisable() {
        ProxyServer.getInstance().getScheduler().cancel(this);
        ProxyServer.getInstance().getPluginManager().unregisterCommands(this);
        ProxyServer.getInstance().getPluginManager().unregisterListeners(this);
        userManager = null;
        if (sql != null) {
            sql.close();
        }
        instance = null;
        String[] msg = {
                "&b╓" + StringUtils.sameChar('─', 32 + getDescription().getVersion().length()) + "╖",
                "&b║ &9Redstone&cAPI&f-&6Bungee &fv" + getDescription().getVersion() + " &cdisabled. &b║",
                "&b║" + StringUtils.sameChar(' ', 32 + getDescription().getVersion().length()) + "║",
                "&b║ &fby Redstonecrafter0" + StringUtils.sameChar(' ', 12 + getDescription().getVersion().length()) + "&b║",
                "&b╙" + StringUtils.sameChar('─', 32 + getDescription().getVersion().length()) + "╜"
        };
        for (String i : msg) {
            ProxyServer.getInstance().getConsole().sendMessage(prefix + renderColors(i));
        }
    }

    /**
     * Returns the instance of the RedstoneAPI
     *
     * @return instance of RedstoneAPI
     * */
    public static RedstoneAPIBungee getInstance() {
        return instance;
    }

    /**
     * Return the instance of the UserManager
     *
     * @return null if disabled by config
     * */
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * Return true if the UserManager is enabled
     *
     * @return false if disabled by config
     * */
    public boolean isUserManagerEnabled() {
        return getConfig().getBoolean("usermanager.enabled");
    }

}
