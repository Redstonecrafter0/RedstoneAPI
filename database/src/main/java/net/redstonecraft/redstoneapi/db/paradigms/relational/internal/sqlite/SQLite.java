package net.redstonecraft.redstoneapi.db.paradigms.relational.internal.sqlite;

import net.redstonecraft.redstoneapi.db.paradigms.relational.Model;
import net.redstonecraft.redstoneapi.db.paradigms.relational.RelationalDB;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.Filter;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.FilterBuilder;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.FilterChainBuilder;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.StatementBuilder;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class SQLite extends RelationalDB {

    private final Connection connection;

    public SQLite(File file) throws IOException, SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + file.getCanonicalPath());
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public <T extends Model> StatementBuilder<T> createStatement(Class<T> clazz) {
        return null;
    }

    @Override
    public FilterBuilder createFilter() {
        return null;
    }

    @Override
    public FilterChainBuilder createFilterChainBuilder(Filter filter) {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return null;
    }

    @Override
    public int update(String sql) throws SQLException {
        return 0;
    }

}
