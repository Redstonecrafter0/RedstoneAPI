package net.redstonecraft.redstoneapi.sql.orm.types;

import net.redstonecraft.redstoneapi.sql.orm.SQLNumber;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SQLDouble extends SQLNumber<Double> implements Comparable<SQLDouble> {

    public SQLDouble() {
        super();
    }

    public SQLDouble(Double value) {
        super(value);
    }

    @Override
    public void serializeSql(PreparedStatement ps, int pos) throws SQLException {
        if (getValue() == null) {
            ps.setNull(pos, Types.DOUBLE);
        } else {
            ps.setDouble(pos, getValue());
        }
    }

    @Override
    public void deserializeSql(ResultSet rs) throws SQLException {
        double val = rs.getDouble(getKey());
        setValue(rs.wasNull() ? null : val);
    }

    @Override
    public String getSqlName() {
        return "DOUBLE";
    }

    @Override
    public int compareTo(SQLDouble o) {
        return getValue().compareTo(o.getValue());
    }

}
