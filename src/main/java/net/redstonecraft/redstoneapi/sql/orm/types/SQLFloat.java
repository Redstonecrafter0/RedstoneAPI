package net.redstonecraft.redstoneapi.sql.orm.types;

import net.redstonecraft.redstoneapi.sql.orm.SQLNumber;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SQLFloat extends SQLNumber<Float> implements Comparable<SQLFloat> {

    public SQLFloat() {
        super();
    }

    public SQLFloat(Float value) {
        super(value);
    }

    @Override
    public void serializeSql(PreparedStatement ps, int pos) throws SQLException {
        if (getValue() == null) {
            ps.setNull(pos, Types.FLOAT);
        } else {
            ps.setFloat(pos, getValue());
        }
    }

    @Override
    public void deserializeSql(ResultSet rs) throws SQLException {
        float val = rs.getFloat(getKey());
        setValue(rs.wasNull() ? null : val);
    }

    @Override
    public String getSqlName() {
        return "FLOAT";
    }

    @Override
    public int compareTo(SQLFloat o) {
        return getValue().compareTo(o.getValue());
    }

}
