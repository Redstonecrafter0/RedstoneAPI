package net.redstonecraft.redstoneapi.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class SQL {

    public abstract void update(String sql);

    public abstract ResultSet query(String sql);

    public abstract PreparedStatement prepareStatement(String sql);

    public abstract void close();

}
