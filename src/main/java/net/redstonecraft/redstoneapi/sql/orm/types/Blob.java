package net.redstonecraft.redstoneapi.sql.orm.types;

import net.redstonecraft.redstoneapi.sql.orm.BaseType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Blob extends BaseType<byte[]> {

    public Blob() {
        super();
    }

    public Blob(byte[] value) {
        super(value);
    }

    @Override
    public void serializeSql(PreparedStatement ps, int pos) throws SQLException {
        ps.setBytes(pos, getValue());
    }

    @Override
    public void deserializeSql(ResultSet rs) throws SQLException {
        setValue(rs.getBytes(getKey()));
    }

    @Override
    public String getSqlName() {
        return "BLOB";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{key='" + getKey() + "', value=" + (getValue() == null ? "null" : Arrays.toString(getValue())) + "}";
    }

}
