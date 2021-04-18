package net.redstonecraft.redstoneapi.sql.orm;

import net.redstonecraft.redstoneapi.sql.SQL;
import net.redstonecraft.redstoneapi.sql.orm.types.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Since SQL has dialects it is necessary for sql commands to be rendered for the used database (e.g. SQLite or MySQL)
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class SQLDialectRenderer {

    public abstract PreparedStatement createTable(SQL sql, Class<? extends TableBase> table, List<BaseType> columns, List<Column> columnData, Int primaryKey) throws SQLException;

    public abstract PreparedStatement insert(SQL sql, Class<? extends TableBase> table, List<BaseType> values) throws SQLException;

    public abstract PreparedStatement update(SQL sql, Class<? extends TableBase> table, List<BaseType> values, Int primaryKey) throws SQLException;

    public abstract PreparedStatement delete(SQL sql, Class<? extends TableBase> table, Filter filter) throws SQLException;

    public abstract PreparedStatement select(SQL sql, Class<? extends TableBase> table, Filter filter) throws SQLException;

    public abstract PreparedStatement select(SQL sql, Class<? extends TableBase> table) throws SQLException;
    
    public abstract PreparedStatement count(SQL sql, Class<? extends TableBase> table) throws SQLException;

    public abstract PreparedStatement count(SQL sql, Class<? extends TableBase> table, Filter filter) throws SQLException;

    protected static PreparedStatement renderFilter(Filter filter, PreparedStatement ps) throws SQLException {
        for (int i = 0; i < filter.getValues().size(); i++) {
            Object o = filter.getValues().get(i);
            if (o instanceof Long) {
                new BigInt((Long) o).serializeSql(ps, i + 1);
            } else if (o instanceof byte[]) {
                new Blob((byte[]) o).serializeSql(ps, i + 1);
            } else if (o instanceof Boolean) {
                new Bool((Boolean) o).serializeSql(ps, i + 1);
            } else if (o instanceof Integer) {
                new Int((Integer) o).serializeSql(ps, i + 1);
            } else if (o instanceof Double) {
                new SQLDouble((Double) o).serializeSql(ps, i + 1);
            } else if (o instanceof Float) {
                new SQLFloat((Float) o).serializeSql(ps, i + 1);
            } else if (o instanceof String) {
                new Text((String) o).serializeSql(ps, i + 1);
            } else if (o instanceof UUID) {
                new SQLUUID((UUID) o).serializeSql(ps, i + 1);
            } else {
                throw new IllegalArgumentException("Invalid query type");
            }
        }
        return ps;
    }

}
