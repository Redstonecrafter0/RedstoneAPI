package net.redstonecraft.redstoneapi.sql.orm;

import net.redstonecraft.redstoneapi.sql.SQL;
import net.redstonecraft.redstoneapi.sql.orm.types.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Since SQL has dialects it is necessary for sql commands to be rendered for the used database (e.g. SQLite or MySQL)
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class SQLDialectRenderer {

    private static Map<Class<?>, Class<? extends BaseType>> filterRenderer;

    static {
        filterRenderer = new HashMap<>();
        addFilterRenderClass(Long.class, BigInt.class);
        addFilterRenderClass(long.class, BigInt.class);
        addFilterRenderClass(byte[].class, Blob.class);
        addFilterRenderClass(Boolean.class, Bool.class);
        addFilterRenderClass(boolean.class, Bool.class);
        addFilterRenderClass(Integer.class, Int.class);
        addFilterRenderClass(int.class, Int.class);
        addFilterRenderClass(Double.class, SQLDouble.class);
        addFilterRenderClass(double.class, SQLDouble.class);
        addFilterRenderClass(Float.class, SQLFloat.class);
        addFilterRenderClass(float.class, SQLFloat.class);
        addFilterRenderClass(String.class, Text.class);
        addFilterRenderClass(UUID.class, SQLUUID.class);
    }

    public abstract PreparedStatement createTable(SQL sql, Class<? extends TableBase> table, List<BaseType> columns, List<Column> columnData, List<Boolean> primaryKeyData, List<Boolean> notNullData, List<Boolean> uniqueData, List<Boolean> hideOnJsonData, SQLNumber primaryKey) throws SQLException;

    public abstract PreparedStatement insert(SQL sql, Class<? extends TableBase> table, List<BaseType> values) throws SQLException;

    public abstract PreparedStatement update(SQL sql, Class<? extends TableBase> table, List<BaseType> values, Int primaryKey) throws SQLException;

    public abstract PreparedStatement delete(SQL sql, Class<? extends TableBase> table, Filter filter) throws SQLException;

    public abstract PreparedStatement select(SQL sql, Class<? extends TableBase> table, Filter filter, Order... orders) throws SQLException;

    public abstract PreparedStatement select(SQL sql, Class<? extends TableBase> table, Order... orders) throws SQLException;

    public abstract PreparedStatement select(SQL sql, Class<? extends TableBase> table, Filter filter, int limit, Order... orders) throws SQLException;

    public abstract PreparedStatement select(SQL sql, Class<? extends TableBase> table, int limit, Order... orders) throws SQLException;

    public abstract PreparedStatement select(SQL sql, Class<? extends TableBase> table, Filter filter, int limit, int offset, Order... orders) throws SQLException;

    public abstract PreparedStatement select(SQL sql, Class<? extends TableBase> table, int limit, int offset, Order... orders) throws SQLException;

    public abstract PreparedStatement count(SQL sql, Class<? extends TableBase> table) throws SQLException;

    public abstract PreparedStatement count(SQL sql, Class<? extends TableBase> table, Filter filter) throws SQLException;

    public abstract PreparedStatement avg(SQL sql, Class<? extends TableBase> table, SQLNumber column) throws SQLException;

    public abstract PreparedStatement avg(SQL sql, Class<? extends TableBase> table, SQLNumber column, Filter filter) throws SQLException;

    public abstract PreparedStatement sum(SQL sql, Class<? extends TableBase> table, SQLNumber column) throws SQLException;

    public abstract PreparedStatement sum(SQL sql, Class<? extends TableBase> table, SQLNumber column, Filter filter) throws SQLException;

    /**
     * Set the classes according to your custom sql class so the {@link Filter} knows what is what
     *
     * @param type the raw type
     * @param sqlType the sql type
     * */
    public static void addFilterRenderClass(Class<?> type, Class<? extends BaseType> sqlType) {
        filterRenderer.put(type, sqlType);
    }

    protected static PreparedStatement renderFilter(Filter filter, PreparedStatement ps) throws SQLException {
        for (int i = 0; i < filter.getValues().size(); i++) {
            Object o = filter.getValues().get(i);
            Class<? extends BaseType> type = filterRenderer.get(o.getClass());
            if (type != null) {
                try {
                    type.getConstructor(o.getClass()).newInstance(o).serializeSql(ps, i + 1);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
                    throw new IllegalAccessError("Invalid constructor for " + type.getSimpleName());
                }
            } else {
                throw new IllegalArgumentException("Invalid query type");
            }
        }
        return ps;
    }

}
