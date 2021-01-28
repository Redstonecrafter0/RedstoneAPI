package net.redstonecraft.redstoneapi.sql;

import java.sql.*;

/**
 * MySQL class to control a MySQL database
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class MySQL implements SQL {

    private final Connection connection;
    private final Statement statement;

    /**
     * Constructor to create a MySQL connection.
     *
     * @param host hostname of the mysql server
     * @param port port of the mysql server
     * @param database database name to use
     * @param username username for the connection
     * @param password password for the username
     */
    public MySQL(String host, int port, String database, String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + host+ ":" + port + "/" + database + "?autoReconnect=true", username, password);
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
