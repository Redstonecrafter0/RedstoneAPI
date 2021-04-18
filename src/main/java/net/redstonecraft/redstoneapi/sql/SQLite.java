package net.redstonecraft.redstoneapi.sql;

import net.redstonecraft.redstoneapi.sql.orm.*;
import net.redstonecraft.redstoneapi.sql.orm.types.Int;
import org.apache.commons.lang.StringEscapeUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    private final String dbname;

    private static final SQLDialectRenderer sqlDialectRenderer = new SQLDialectRenderer() {
        @Override
        public PreparedStatement createTable(SQL sql, Class<? extends TableBase> table, List<BaseType> columns, List<Column> columnData, Int primaryKey) {
            if (columns.size() != columnData.size()) {
                throw new IllegalArgumentException("Size not matching");
            }
            List<String> list = new ArrayList<>();
            for (int i = 0; i < columns.size(); i++) {
                list.add("'" + StringEscapeUtils.escapeSql(columns.get(i).getKey()) + "' " + columns.get(i).getSqlName() + (columnData.get(i).notnull() ? " NOT NULL" : "") + (columnData.get(i).unique() ? " UNIQUE" : ""));
            }
            return sql.prepareStatement("CREATE TABLE IF NOT EXISTS '" + StringEscapeUtils.escapeSql(table.getSimpleName()) + "' (" + String.join(", ", list) + ", PRIMARY KEY ('" + StringEscapeUtils.escapeSql(primaryKey.getKey()) + "' AUTOINCREMENT))");
        }

        private List<String> escapeStrings(List<String> strings) {
            List<String> list = new ArrayList<>();
            for (String i : strings) {
                list.add("'" + StringEscapeUtils.escapeSql(i) + "'");
            }
            return list;
        }

        @Override
        public PreparedStatement insert(SQL sql, Class<? extends TableBase> table, List<BaseType> values) throws SQLException {
            List<String> keys = new ArrayList<>();
            List<String> placeholders = new ArrayList<>();
            for (BaseType i : values) {
                keys.add(i.getKey());
                placeholders.add("?");
            }
            PreparedStatement ps = sql.prepareStatement("INSERT INTO '" + StringEscapeUtils.escapeSql(table.getSimpleName()) + "' (" + String.join(", ", escapeStrings(keys)) + ") VALUES (" + String.join(", ", placeholders) + ")");
            for (int i = 0; i < values.size(); i++) {
                values.get(i).serializeSql(ps, i + 1);
            }
            return ps;
        }

        @Override
        public PreparedStatement update(SQL sql, Class<? extends TableBase> table, List<BaseType> values, Int primaryKey) throws SQLException {
            List<String> list = new ArrayList<>();
            for (BaseType i : values) {
                list.add("'" + StringEscapeUtils.escapeSql(i.getKey()) + "' = ?");
            }
            PreparedStatement ps = sql.prepareStatement("UPDATE '" + StringEscapeUtils.escapeSql(table.getSimpleName()) + "' SET " + String.join(", ", list) + " WHERE " + StringEscapeUtils.escapeSql(primaryKey.getKey()) + " = '" + StringEscapeUtils.escapeSql(String.valueOf(primaryKey.getValue())) + "'");
            for (int i = 0; i < values.size(); i++) {
                values.get(i).serializeSql(ps, i + 1);
            }
            return ps;
        }

        @Override
        public PreparedStatement delete(SQL sql, Class<? extends TableBase> table, Filter filter) throws SQLException {
            PreparedStatement ps = sql.prepareStatement("DELETE FROM '" + StringEscapeUtils.escapeSql(table.getSimpleName()) + "' WHERE " + filter.getQueryString());
            return renderFilter(filter, ps);
        }

        @Override
        public PreparedStatement select(SQL sql, Class<? extends TableBase> table, Filter filter) throws SQLException {
            PreparedStatement ps = sql.prepareStatement("SELECT * FROM '" + StringEscapeUtils.escapeSql(table.getSimpleName()) + "' WHERE " + filter.getQueryString());
            return renderFilter(filter, ps);
        }

        @Override
        public PreparedStatement select(SQL sql, Class<? extends TableBase> table) {
            return sql.prepareStatement("SELECT * FROM '" + StringEscapeUtils.escapeSql(table.getSimpleName()) + "'");
        }

        @Override
        public PreparedStatement count(SQL sql, Class<? extends TableBase> table) {
            return sql.prepareStatement("SELECT count(*) AS total FROM '" + StringEscapeUtils.escapeSql(table.getSimpleName()) + "'");
        }

        @Override
        public PreparedStatement count(SQL sql, Class<? extends TableBase> table, Filter filter) throws SQLException {
            PreparedStatement ps = sql.prepareStatement("SELECT count(*) AS total FROM '" + StringEscapeUtils.escapeSql(table.getSimpleName()) + "' WHERE " + filter.getQueryString());
            return renderFilter(filter, ps);
        }
    };

    /**
     * Contructor to create the database connection to a sqlite database by filename
     *
     * @param dbname filename of the database
     *
     * @throws SQLException when an sql exception occurs
     * @throws ClassNotFoundException when the driver is missing (should by included)
     */
    public SQLite(String dbname) throws SQLException, ClassNotFoundException {
        this.dbname = dbname;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbname);
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

    public String getDbName() {
        return dbname;
    }

    @Override
    public SQLDialectRenderer getSyntaxRenderer() {
        return sqlDialectRenderer;
    }
}
