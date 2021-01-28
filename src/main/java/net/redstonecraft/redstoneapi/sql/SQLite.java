package net.redstonecraft.redstoneapi.sql;

import java.sql.*;

/**
 * SQLite class to control a SQLite database
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class SQLite implements SQL {

    /**
     * DBName for a database in memory
     */
    public static final String MEMORY = ":memory:";

    private Connection connection;
    private Statement statement;

    /**
     * Contructor to create the database connection to a sqlite database by filename
     *
     * @param dbname filename of the database
     */
    public SQLite(String dbname) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
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
