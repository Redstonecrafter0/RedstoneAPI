package net.redstonecraft.redstoneapi.sql;

import net.redstonecraft.redstoneapi.sql.orm.*;
import net.redstonecraft.redstoneapi.sql.orm.types.Int;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL class to control a MySQL or MariaDB database
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class MySQL implements SQL {

    private final Connection connection;
    private final Statement statement;

    private static final SQLDialectRenderer sqlDialectRenderer = new SQLDialectRenderer() {
        @Override
        public PreparedStatement createTable(SQL sql, Class<? extends TableBase> table, List<BaseType> columns, List<Column> columnData, List<Boolean> primaryKeyData, List<Boolean> notNullData, List<Boolean> uniqueData, List<Boolean> hideOnJsonData, SQLNumber primaryKey) {
            if (columns.size() != columnData.size() && columnData.size() != primaryKeyData.size() && primaryKeyData.size() != notNullData.size() && notNullData.size() != uniqueData.size() && uniqueData.size() != hideOnJsonData.size()) {
                throw new IllegalArgumentException("Size not matching");
            }
            List<String> list = new ArrayList<>();
            for (int i = 0; i < columns.size(); i++) {
                list.add("`" + columns.get(i).getKey() + "` " + columns.get(i).getSqlName() + (columnData.get(i).notnull() || notNullData.get(i) ? " NOT NULL" : "") + (columnData.get(i).unique() || uniqueData.get(i) ? " UNIQUE" : "") + (columnData.get(i).primaryKey() || primaryKeyData.get(i) ? " AUTO_INCREMENT" : ""));
            }
            return sql.prepareStatement("CREATE TABLE IF NOT EXISTS `" + table.getSimpleName() + "` (" + String.join(", ", list) + ", PRIMARY KEY (`" + primaryKey.getKey() + "`))");
        }

        private List<String> escapeStrings(List<String> strings) {
            List<String> list = new ArrayList<>();
            for (String i : strings) {
                list.add("`" + i + "`");
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
            PreparedStatement ps = sql.prepareStatement("INSERT INTO `" + table.getSimpleName() + "` (" + String.join(", ", escapeStrings(keys)) + ") VALUES (" + String.join(", ", placeholders) + ")");
            for (int i = 0; i < values.size(); i++) {
                values.get(i).serializeSql(ps, i + 1);
            }
            return ps;
        }

        @Override
        public PreparedStatement update(SQL sql, Class<? extends TableBase> table, List<BaseType> values, Int primaryKey) throws SQLException {
            List<String> list = new ArrayList<>();
            for (BaseType i : values) {
                list.add("`" + i.getKey() + "` = ?");
            }
            PreparedStatement ps = sql.prepareStatement("UPDATE `" + table.getSimpleName() + "` SET " + String.join(", ", list) + " WHERE " + primaryKey.getKey() + " = " + primaryKey.getValue());
            for (int i = 0; i < values.size(); i++) {
                values.get(i).serializeSql(ps, i + 1);
            }
            return ps;
        }

        @Override
        public PreparedStatement delete(SQL sql, Class<? extends TableBase> table, Filter filter) throws SQLException {
            PreparedStatement ps = sql.prepareStatement("DELETE FROM `" + table.getSimpleName() + "` WHERE " + filter.getQueryString());
            return renderFilter(filter, ps);
        }

        @Override
        public PreparedStatement select(SQL sql, Class<? extends TableBase> table, Filter filter, Order... orders) throws SQLException {
            if (orders.length == 0) {
                PreparedStatement ps = sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "` WHERE " + filter.getQueryString());
                return renderFilter(filter, ps);
            } else {
                List<String> list = new ArrayList<>();
                for (Order i : orders) {
                    list.add(i.getKey().getKey() + " " + i.getDirection().getIdentifier());
                }
                PreparedStatement ps = sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "` WHERE " + filter.getQueryString() + " ORDER BY " + String.join(", ", list));
                return renderFilter(filter, ps);
            }
        }

        @Override
        public PreparedStatement select(SQL sql, Class<? extends TableBase> table, Order... orders) {
            if (orders.length == 0) {
                return sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "`");
            } else {
                List<String> list = new ArrayList<>();
                for (Order i : orders) {
                    list.add(i.getKey().getKey() + " " + i.getDirection().getIdentifier());
                }
                return sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "` ORDER BY " + String.join(", ", list));
            }
        }

        @Override
        public PreparedStatement select(SQL sql, Class<? extends TableBase> table, Filter filter, int limit, Order... orders) throws SQLException {
            if (orders.length == 0) {
                PreparedStatement ps = sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "` WHERE " + filter.getQueryString() + " LIMIT " + limit);
                return renderFilter(filter, ps);
            } else {
                List<String> list = new ArrayList<>();
                for (Order i : orders) {
                    list.add(i.getKey().getKey() + " " + i.getDirection().getIdentifier());
                }
                PreparedStatement ps = sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "` WHERE " + filter.getQueryString() + " ORDER BY " + String.join(", ", list) + " LIMIT " + limit);
                return renderFilter(filter, ps);
            }
        }

        @Override
        public PreparedStatement select(SQL sql, Class<? extends TableBase> table, int limit, Order... orders) {
            if (orders.length == 0) {
                return sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "` LIMIT " + limit);
            } else {
                List<String> list = new ArrayList<>();
                for (Order i : orders) {
                    list.add(i.getKey().getKey() + " " + i.getDirection().getIdentifier());
                }
                return sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "` ORDER BY " + String.join(", ", list) + " LIMIT " + limit);
            }
        }

        @Override
        public PreparedStatement select(SQL sql, Class<? extends TableBase> table, Filter filter, int limit, int offset, Order... orders) throws SQLException {
            if (orders.length == 0) {
                PreparedStatement ps = sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "` WHERE " + filter.getQueryString() + " LIMIT " + limit + " OFFSET " + offset);
                return renderFilter(filter, ps);
            } else {
                List<String> list = new ArrayList<>();
                for (Order i : orders) {
                    list.add(i.getKey().getKey() + " " + i.getDirection().getIdentifier());
                }
                PreparedStatement ps = sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "` WHERE " + filter.getQueryString() + " ORDER BY " + String.join(", ", list) + " LIMIT " + limit + " OFFSET " + offset);
                return renderFilter(filter, ps);
            }
        }

        @Override
        public PreparedStatement select(SQL sql, Class<? extends TableBase> table, int limit, int offset, Order... orders) {
            if (orders.length == 0) {
                return sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "` LIMIT " + limit + " OFFSET " + offset);
            } else {
                List<String> list = new ArrayList<>();
                for (Order i : orders) {
                    list.add(i.getKey().getKey() + " " + i.getDirection().getIdentifier());
                }
                return sql.prepareStatement("SELECT * FROM `" + table.getSimpleName() + "` ORDER BY " + String.join(", ", list) + " LIMIT " + limit + " OFFSET " + offset);
            }
        }

        @Override
        public PreparedStatement count(SQL sql, Class<? extends TableBase> table) {
            return sql.prepareStatement("SELECT COUNT(*) AS total FROM `" + table.getSimpleName() + "`");
        }

        @Override
        public PreparedStatement count(SQL sql, Class<? extends TableBase> table, Filter filter) throws SQLException {
            PreparedStatement ps = sql.prepareStatement("SELECT COUNT(*) AS total FROM `" + table.getSimpleName() + "` WHERE " + filter.getQueryString());
            return renderFilter(filter, ps);
        }

        @Override
        public PreparedStatement avg(SQL sql, Class<? extends TableBase> table, SQLNumber column) {
            return sql.prepareStatement("SELECT AVG(`" + column.getKey() + "`) AS total FROM `" + table.getSimpleName() + "`");
        }

        @Override
        public PreparedStatement avg(SQL sql, Class<? extends TableBase> table, SQLNumber column, Filter filter) throws SQLException {
            PreparedStatement ps = sql.prepareStatement("SELECT AVG(`" + column.getKey() + "`) AS total FROM `" + table.getSimpleName() + "` WHERE " + filter.getQueryString());
            return renderFilter(filter, ps);
        }

        @Override
        public PreparedStatement sum(SQL sql, Class<? extends TableBase> table, SQLNumber column) {
            return sql.prepareStatement("SELECT SUM(`" + column.getKey() + "`) AS total FROM `" + table.getSimpleName() + "`");
        }

        @Override
        public PreparedStatement sum(SQL sql, Class<? extends TableBase> table, SQLNumber column, Filter filter) throws SQLException {
            PreparedStatement ps = sql.prepareStatement("SELECT SUM(`" + column.getKey() + "`) AS total FROM `" + table.getSimpleName() + "` WHERE " + filter.getQueryString());
            return renderFilter(filter, ps);
        }
    };

    /**
     * Constructor to create a MySQL connection.
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
    public MySQL(String host, int port, String database, String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
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

    @Override
    public SQLDialectRenderer getSyntaxRenderer() {
        return sqlDialectRenderer;
    }
}
