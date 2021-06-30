package net.redstonecraft.redstoneapi.sql.orm;

import net.redstonecraft.redstoneapi.sql.SQL;
import net.redstonecraft.redstoneapi.sql.orm.exceptions.*;
import net.redstonecraft.redstoneapi.sql.orm.types.Int;
import net.redstonecraft.redstoneapi.tools.Pagination;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the most important class in the orm system.
 * Use it to register tables to enable adding, removing, updating and querying entries from a table.
 * Everything except querries arent executed until {@link Session#commit()} is executed.
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class Session {

    private final SQL sql;
    final Map<Class<? extends TableBase>, TableFields> tables = new HashMap<>();
    final List<PreparedStatement> waitingUpdates = new ArrayList<>();

    public Session(SQL sql) {
        this.sql = sql;
    }

    public void addTable(Class<? extends TableBase> table) throws InvalidStructureException, ConstructException, AlreadyExistsException, SQLException {
        if (tables.containsKey(table)) {
            throw new AlreadyExistsException("This table was already added");
        }
        Field primary = null;
        List<Field> fields = new ArrayList<>();
        for (Field i : table.getFields()) {
            if (i.isAnnotationPresent(Column.class) && BaseType.class.isAssignableFrom(i.getType())) {
                Column value = i.getAnnotation(Column.class);
                i.setAccessible(true);
                fields.add(i);
                if (value.primaryKey() || i.isAnnotationPresent(PrimaryKey.class)) {
                    if (value.unique() || i.isAnnotationPresent(Unique.class)) {
                        throw new InvalidStructureException("The primaryKey is unique");
                    }
                    if (value.notnull() || i.isAnnotationPresent(NotNull.class)) {
                        throw new InvalidStructureException("The primaryKey cannot be null");
                    }
                    if (primary == null) {
                        primary = i;
                    } else {
                        throw new InvalidStructureException("Exactly one primaryKey is required");
                    }
                }
            }
        }
        if (primary == null) {
            throw new InvalidStructureException("Exactly one primaryKey is required");
        }
        try {
            Object model = table.newInstance();
            List<BaseType> columns = new ArrayList<>();
            List<Column> columnData = new ArrayList<>();
            List<Boolean> primaryKeyData = new ArrayList<>();
            List<Boolean> notNullData = new ArrayList<>();
            List<Boolean> uniqueData = new ArrayList<>();
            List<Boolean> hideOnJsonData = new ArrayList<>();
            for (Field i : fields) {
                columns.add(((BaseType) i.get(model)));
                columnData.add(i.getAnnotation(Column.class));
                primaryKeyData.add(i.isAnnotationPresent(PrimaryKey.class));
                notNullData.add(i.isAnnotationPresent(NotNull.class));
                uniqueData.add(i.isAnnotationPresent(Unique.class));
                hideOnJsonData.add(i.isAnnotationPresent(HideOnJson.class));
            }
            waitingUpdates.add(sql.getSyntaxRenderer().createTable(sql, table, columns, columnData, primaryKeyData, notNullData, uniqueData, hideOnJsonData, (SQLNumber) primary.get(model)));
            tables.put(table, new TableFields(table, fields.toArray(new Field[0]), primary));
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConstructException(e.getMessage());
        }
    }

    public <T extends TableBase> Query<T> query(Class<T> table, Order... orders) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            return new Query<>(this, tables.get(table), orders);
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public <T extends TableBase> Query<T> query(Class<T> table, Filter filter, Order... orders) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            return new Query<>(this, tables.get(table), filter, orders);
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public <T extends TableBase> Query<T> query(Class<T> table, int limit, Order... orders) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            return new Query<>(this, tables.get(table), limit, orders);
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public <T extends TableBase> Query<T> query(Class<T> table, Filter filter, int limit, Order... orders) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            return new Query<>(this, tables.get(table), filter, limit, orders);
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public <T extends TableBase> Query<T> query(Class<T> table, int limit, int offset, Order... orders) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            return new Query<>(this, tables.get(table), limit, offset, orders);
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public <T extends TableBase> Query<T> query(Class<T> table, Filter filter, int limit, int offset, Order... orders) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            return new Query<>(this, tables.get(table), filter, limit, offset, orders);
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public <T extends TableBase> Pagination<T> paginate(Class<T> table, int pageSize, int page, Order... orders) throws NoSuchTableException, SQLException, ClosedQueryException, ConstructException {
        if (tables.containsKey(table)) {
            return new SQLPagination<>(new Query<>(this, tables.get(table), pageSize, pageSize * (page), orders).getAllAsList(), pageSize, page);
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public <T extends TableBase> Pagination<T> paginate(Class<T> table, int pageSize, int page, Filter filter, Order... orders) throws NoSuchTableException, SQLException, ClosedQueryException, ConstructException {
        if (tables.containsKey(table)) {
            return new SQLPagination<>(new Query<>(this, tables.get(table), filter, pageSize, pageSize * (page), orders).getAllAsList(), pageSize, page);
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public void add(TableBase entry) throws NoSuchTableException, InvalidStructureException, SQLException {
        if (tables.containsKey(entry.getClass())) {
            try {
                List<BaseType> values = new ArrayList();
                for (Field i : tables.get(entry.getClass()).fields) {
                    if ((i.getAnnotation(Column.class).notnull() || i.isAnnotationPresent(NotNull.class)) && ((BaseType) i.get(entry)).getValue() == null) {
                        throw new NullPointerException("Column " + i.getName() + " is declared as notnull but null was provided");
                    }
                    BaseType type = ((BaseType) i.get(entry));
                    if (type.getValue() != null) {
                        values.add(type);
                    }
                }
                waitingUpdates.add(sql.getSyntaxRenderer().insert(sql, entry.getClass(), values));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            throw new NoSuchTableException("Table " + entry.getClass().getName() + " does not exist");
        }
    }

    public void update(TableBase entry) throws NoSuchTableException, InvalidStructureException, SQLException {
        if (tables.containsKey(entry.getClass())) {
            try {
                Int primary = (Int) tables.get(entry.getClass()).primary.get(entry);
                if (primary == null) {
                    throw new NullPointerException("Provided id is null");
                }
                List<BaseType> values = new ArrayList<>();
                for (Field i : tables.get(entry.getClass()).fields) {
                    if (!(i.getAnnotation(Column.class).primaryKey() || i.isAnnotationPresent(PrimaryKey.class))) {
                        values.add((BaseType) i.get(entry));
                    }
                }
                waitingUpdates.add(sql.getSyntaxRenderer().update(sql, entry.getClass(), values, primary));
            } catch (IllegalAccessException e) {
                throw new InvalidStructureException(e.getMessage());
            }
        } else {
            throw new NoSuchTableException("Table " + entry.getClass().getName() + " does not exist");
        }
    }

    public void delete(Class<? extends TableBase> table, Filter filter) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            waitingUpdates.add(sql.getSyntaxRenderer().delete(sql, table, filter));
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public void delete(TableBase entry) throws NoSuchTableException, InvalidStructureException, SQLException {
        if (tables.containsKey(entry.getClass())) {
            try {
                Int primary = (Int) tables.get(entry.getClass()).primary.get(entry);
                waitingUpdates.add(sql.getSyntaxRenderer().delete(sql, entry.getClass(), Filter.equals(primary, primary.getValue())));
            } catch (IllegalAccessException e) {
                throw new InvalidStructureException(e.getMessage());
            }
        } else {
            throw new NoSuchTableException("Table " + entry.getClass().getName() + " does not exist");
        }
    }

    public Integer count(Class<? extends TableBase> table) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            PreparedStatement ps = sql.getSyntaxRenderer().count(sql, table);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("total") : null;
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public Integer count(Class<? extends TableBase> table, Filter filter) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            PreparedStatement ps = sql.getSyntaxRenderer().count(sql, table, filter);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("total") : null;
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public Double avg(Class<? extends TableBase> table, SQLNumber<? extends Number> column) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            PreparedStatement ps = sql.getSyntaxRenderer().avg(sql, table, column);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("total") : null;
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public Double avg(Class<? extends TableBase> table, SQLNumber<? extends Number> column, Filter filter) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            PreparedStatement ps = sql.getSyntaxRenderer().avg(sql, table, column, filter);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("total") : null;
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public Double sum(Class<? extends TableBase> table, SQLNumber<? extends Number> column) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            PreparedStatement ps = sql.getSyntaxRenderer().sum(sql, table, column);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("total") : null;
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public Double sum(Class<? extends TableBase> table, SQLNumber<? extends Number> column, Filter filter) throws NoSuchTableException, SQLException {
        if (tables.containsKey(table)) {
            PreparedStatement ps = sql.getSyntaxRenderer().sum(sql, table, column, filter);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("total") : null;
        } else {
            throw new NoSuchTableException("Table " + table.getName() + " does not exist");
        }
    }

    public void commit() throws SQLException {
        for (PreparedStatement i : waitingUpdates) {
            i.executeUpdate();
        }
        waitingUpdates.clear();
    }

    public SQL getSql() {
        return sql;
    }

    static class TableFields<T extends TableBase> {

        final Class<T> clazz;
        final Field[] fields;
        final Field primary;

        private TableFields(Class<T> clazz, Field[] fields, Field primary) {
            this.clazz = clazz;
            this.fields = fields;
            this.primary = primary;
        }

    }

}
