package net.redstonecraft.redstoneapi.db.paradigms.relational.internal.mysql;

import net.redstonecraft.redstoneapi.db.paradigms.relational.Model;
import net.redstonecraft.redstoneapi.db.paradigms.relational.RelationalDB;
import net.redstonecraft.redstoneapi.db.paradigms.relational.annotations.*;
import net.redstonecraft.redstoneapi.db.paradigms.relational.internal.base.MySQLFilterChainBuilder;
import net.redstonecraft.redstoneapi.db.paradigms.relational.internal.base.SQLStatementBuilder;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.*;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class MySQL extends RelationalDB {

    private final Connection connection;
    
    public MySQL(String host, int port, String database, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
    }

    public int update(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        int updated = statement.executeUpdate(sql);
        statement.close();
        return updated;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public <T extends Model> StatementBuilder<T> createStatement(Class<T> clazz) {
        return new MySQLStatementBuilder<>();
    }

    @Override
    public FilterBuilder createFilter() {
        return new MySQLFilterBuilder();
    }

    @Override
    public FilterChainBuilder createFilterChainBuilder(Filter filter) {
        return new MySQLFilterChainBuilder(filter);
    }

    public static String renderColumn(Field field) {
        String type = field.getType().getName();
        String sType = switch (type) {
            case "Integer" -> "INT";
            case "Long" -> "BIGINT";
            case "Double" -> "DOUBLE";
            case "Float" -> "FLOAT";
            case "Boolean" -> "BOOLEAN";
            case "String" -> field.isAnnotationPresent(VarChar.class) ? "VARCHAR(" + field.getAnnotation(VarChar.class).value() + ")" : "TEXT";
            case "Character" -> "CHAR(1)";
            case "[B" -> "BLOB";
            default -> throw new IllegalArgumentException();
        };
        return SQLStatementBuilder.escapeColumn(field.getAnnotation(Column.class).value()) + " " + sType +
                (field.isAnnotationPresent(PrimaryKey.class) ? " PRIMARY KEY" : "") +
                (field.isAnnotationPresent(AutoIncrement.class) ? " AUTO_INCREMENT" : "") +
                (field.isAnnotationPresent(NotNull.class) ? " NOT NULL" : "");
    }

}
