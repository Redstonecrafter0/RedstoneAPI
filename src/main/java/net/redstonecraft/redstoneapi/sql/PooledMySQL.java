package net.redstonecraft.redstoneapi.sql;

import com.zaxxer.hikari.HikariDataSource;
import net.redstonecraft.redstoneapi.sql.orm.*;
import net.redstonecraft.redstoneapi.sql.orm.types.Int;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The same as {@link MySQL} but with multiple parallel connections.
 *
 * @author Redstonecrafter0
 * @since 1.5
 */
public class PooledMySQL implements SQL {

    private final Connection connection;
    private final HikariDataSource db = new HikariDataSource();

    /**
     * Constructor to create a pooled MySQL connection.
     *
     * @param host hostname of the mysql server
     * @param port port of the mysql server
     * @param database database name to use
     * @param username username for the connection
     * @param password password for the username
     *
     * @throws SQLException when an sql exception occurs
     * @throws ClassNotFoundException when the driver is missing (should by included)
     */
    public PooledMySQL(String host, int port, String database, String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        db.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        db.setUsername(username);
        db.setPassword(password);
        connection = db.getConnection();
    }

    public void update(String sql) {
        try {
            connection.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet query(String sql) {
        try {
            return connection.createStatement().executeQuery(sql);
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
            if (db != null) {
                db.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SQLDialectRenderer getSyntaxRenderer() {
        return MySQL.sqlDialectRenderer;
    }

}
