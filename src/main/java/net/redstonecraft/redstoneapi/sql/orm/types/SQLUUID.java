package net.redstonecraft.redstoneapi.sql.orm.types;

import net.redstonecraft.redstoneapi.sql.orm.BaseType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLUUID extends BaseType<UUID> {

    public SQLUUID() {
    }

    public SQLUUID(UUID value) {
        super(value);
    }

    @Override
    public String getSqlName() {
        return "VARCHAR(36)";
    }

    @Override
    public void serializeSql(PreparedStatement ps, int pos) throws SQLException {
        ps.setString(pos, getValue().toString());
    }

    @Override
    public void deserializeSql(ResultSet rs) throws SQLException {
        setValue(UUID.fromString(rs.getString(getKey())));
    }

}
