package net.redstonecraft.redstoneapi.sql;

import java.sql.*;

public class MySQL {

    private final Connection connection;
    private final Statement statement;

    public MySQL(String host, int port, String database, String username, String password) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + host+ ":" + port + "/" + database, username, password);
        statement = connection.createStatement();
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
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
