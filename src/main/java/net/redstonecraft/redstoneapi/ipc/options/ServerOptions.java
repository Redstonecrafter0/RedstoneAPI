package net.redstonecraft.redstoneapi.ipc.options;

import net.redstonecraft.redstoneapi.sql.MySQL;
import net.redstonecraft.redstoneapi.sql.SQLite;

public class ServerOptions {

    public Class<?> tokenStorageType;
    public String mySqlHost;
    public int mySqlPort;
    public String mySqlDatabase;
    public String mySqlTable;
    public String mySqlUsername;
    public String mySqlPassword;
    public String sqLiteName;

    public ServerOptions(Class<?> tokenStorageType,
                         String mySqlHost,
                         int mySqlPort,
                         String mySqlDatabase,
                         String mySqlTable,
                         String mySqlUsername,
                         String mySqlPassword,
                         String sqLiteName) {
        this.tokenStorageType = tokenStorageType;
        this.mySqlHost = mySqlHost;
        this.mySqlPort = mySqlPort;
        this.mySqlDatabase = mySqlDatabase;
        this.mySqlTable = mySqlTable;
        this.mySqlUsername = mySqlUsername;
        this.mySqlPassword = mySqlPassword;
        this.sqLiteName = sqLiteName;
    }

    public static ServerOptions getDefaultMySql() {
        return new ServerOptions(MySQL.class, "localhost", 3306,
                "redstoneIpcTokens", "tokens", "", "", "");
    }

    public static ServerOptions getDefaultSqLite() {
        return new ServerOptions(SQLite.class,
                null, 0, null, null, null, null,
                "tokens.db");
    }

    public ServerOptions setTokenStorageType(Class<?> tokenStorageType) {
        this.tokenStorageType = tokenStorageType;
        return this;
    }

    public ServerOptions setMySqlHost(String mySqlHost) {
        this.mySqlHost = mySqlHost;
        return this;
    }

    public ServerOptions setMySqlPort(int mySqlPort) {
        this.mySqlPort = mySqlPort;
        return this;
    }

    public ServerOptions setMySqlDatabase(String mySqlDatabase) {
        this.mySqlDatabase = mySqlDatabase;
        return this;
    }

    public ServerOptions setMySqlTable(String mySqlTable) {
        this.mySqlTable = mySqlTable;
        return this;
    }

    public ServerOptions setMySqlUsername(String mySqlUsername) {
        this.mySqlUsername = mySqlUsername;
        return this;
    }

    public ServerOptions setMySqlPassword(String mySqlPassword) {
        this.mySqlPassword = mySqlPassword;
        return this;
    }

    public ServerOptions setSqLiteName(String sqLiteName) {
        this.sqLiteName = sqLiteName;
        return this;
    }
}
