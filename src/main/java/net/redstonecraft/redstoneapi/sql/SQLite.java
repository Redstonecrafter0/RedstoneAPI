package net.redstonecraft.redstoneapi.sql;

import java.sql.*;

public class SQLite extends SQL {

    public static final String MEMORY = ":memory:";

    private Connection connection;
    private Statement statement;

    public SQLite(String dbname) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbname);
        statement = connection.createStatement();
        statement.setQueryTimeout(30);
    }

    public void update(String sql) {
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet query(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PreparedStatement prepareStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
