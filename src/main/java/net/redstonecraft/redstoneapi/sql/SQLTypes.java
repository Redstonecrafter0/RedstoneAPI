package net.redstonecraft.redstoneapi.sql;

public enum SQLTypes {

    MYSQL(MySQL.class),
    SQLITE(SQLite.class);

    public final Class<? extends SQL> clazz;

    SQLTypes(Class<? extends SQL> clazz) {
        this.clazz = clazz;
    }

}
