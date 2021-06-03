package net.redstonecraft.redstoneapi.sql.orm.types;

import net.redstonecraft.redstoneapi.sql.orm.BaseType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class Bool extends BaseType<Boolean> implements Comparable<Bool> {

    public Bool() {
        super();
    }

    public Bool(Boolean value) {
        super(value);
    }

    @Override
    public void serializeSql(PreparedStatement ps, int pos) throws SQLException {
        if (getValue() == null) {
            ps.setNull(pos, Types.INTEGER);
        } else {
            ps.setInt(pos, getValue() ? 1 : 0);
        }
    }

    @Override
    public void deserializeSql(ResultSet rs) throws SQLException {
        int val = rs.getInt(getKey());
        setValue(rs.wasNull() ? null : (val != 0));
    }

    @Override
    public String getSqlName() {
        return "INT";
    }

    @Override
    public int compareTo(Bool o) {
        return getValue().compareTo(o.getValue());
    }

}
