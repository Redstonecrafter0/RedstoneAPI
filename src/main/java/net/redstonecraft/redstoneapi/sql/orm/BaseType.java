package net.redstonecraft.redstoneapi.sql.orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The abstract class for types that can be stored in a database
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class BaseType<T> {

    public BaseType() {
    }

    public BaseType(T value) {
        this.value = value;
    }

    private String key;

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract String getSqlName();

    public abstract void serializeSql(PreparedStatement ps, int pos) throws SQLException;

    public abstract void deserializeSql(ResultSet rs) throws SQLException;

    void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        if (value != null) {
            return value.toString();
        } else {
            return "null";
        }
    }
}
