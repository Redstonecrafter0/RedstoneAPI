package net.redstonecraft.redstoneapi.sql.orm.types;

import net.redstonecraft.redstoneapi.sql.orm.BaseType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VarChar extends BaseType<String> {

    private final int size;

    public VarChar(int size) {
        super();
        this.size = size;
    }

    public VarChar(int size, String value) {
        super(value);
        this.size = size;
    }

    @Override
    public String getSqlName() {
        return "VARCHAR(" + size + ")";
    }

    @Override
    public void serializeSql(PreparedStatement ps, int pos) throws SQLException {
        ps.setString(pos, getValue());
    }

    @Override
    public void deserializeSql(ResultSet rs) throws SQLException {
        setValue(rs.getString(getKey()));
    }

}
