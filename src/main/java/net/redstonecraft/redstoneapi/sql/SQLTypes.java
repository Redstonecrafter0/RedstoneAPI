package net.redstonecraft.redstoneapi.sql;

/**
 * This can be used for parsing a config to define the type of database used.
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public enum SQLTypes {

    MYSQL(MySQL.class),
    POOLED_MYSQL(PooledMySQL.class),
    SQLITE(SQLite.class);

    public final Class<? extends SQL> clazz;

    SQLTypes(Class<? extends SQL> clazz) {
        this.clazz = clazz;
    }

}
