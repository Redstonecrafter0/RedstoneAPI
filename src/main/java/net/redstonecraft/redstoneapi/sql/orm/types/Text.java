package net.redstonecraft.redstoneapi.sql.orm.types;

import net.redstonecraft.redstoneapi.sql.orm.BaseType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class Text extends BaseType<String> {

    public Text() {
        super();
    }

    public Text(String value) {
        super(value);
    }

    @Override
    public void serializeSql(PreparedStatement ps, int pos) throws SQLException {
        if (getValue() == null) {
            ps.setNull(pos, Types.VARCHAR);
        } else {
            ps.setString(pos, getValue());
        }
    }

    @Override
    public void deserializeSql(ResultSet rs) throws SQLException {
        setValue(rs.getString(getKey()));
    }

    @Override
    public String getSqlName() {
        return "TEXT";
    }

}
