package net.redstonecraft.redstoneapi.sql;

import net.redstonecraft.redstoneapi.sql.orm.SQLDialectRenderer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * SQL interface for easy use
 *
 * @author Redstonecrafter0
 * @since 1.0
 */
public interface SQL {

    /**
     * Method used for unsafe updates
     *
     * @param sql SQL-string
     */
    public void update(String sql);

    /**
     * Method used for unsafe query
     *
     * @param sql SQL-string
     *
     * @return ResultSet
     */
    public ResultSet query(String sql);

    /**
     * Method used for typesafe querys and updates
     *
     * @param sql SQL-string
     *
     * @return a new prepared statement
     */
    public PreparedStatement prepareStatement(String sql);

    /**
     * Method to close the connection
     */
    public void close();

    /**
     * Get the sql renderer to fit the dialect
     *
     * @return a dialect renderer for sql commands
     * */
    public SQLDialectRenderer getSyntaxRenderer();

}
