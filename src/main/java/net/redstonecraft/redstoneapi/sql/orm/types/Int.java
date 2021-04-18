package net.redstonecraft.redstoneapi.sql.orm.types;

import net.redstonecraft.redstoneapi.sql.orm.SQLNumber;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class Int extends SQLNumber<Integer> {

    public Int() {
        super();
    }

    public Int(Integer value) {
        super(value);
    }

    @Override
    public void serializeSql(PreparedStatement ps, int pos) throws SQLException {
        if (getValue() == null) {
            ps.setNull(pos, Types.INTEGER);
        } else {
            ps.setInt(pos, getValue());
        }
    }

    @Override
    public void deserializeSql(ResultSet rs) throws SQLException {
        int val = rs.getInt(getKey());
        setValue(rs.wasNull() ? null : val);
    }

    @Override
    public String getSqlName() {
        return "INTEGER";
    }

}
