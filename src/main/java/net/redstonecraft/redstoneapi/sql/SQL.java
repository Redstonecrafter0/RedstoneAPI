package net.redstonecraft.redstoneapi.sql;

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
     */
    public PreparedStatement prepareStatement(String sql);

    /**
     * Method to close the connection
     */
    public void close();

}
