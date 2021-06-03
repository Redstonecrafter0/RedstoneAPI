package net.redstonecraft.redstoneapi.sql.orm;

import net.redstonecraft.redstoneapi.json.JSONArray;
import net.redstonecraft.redstoneapi.json.JSONObject;
import net.redstonecraft.redstoneapi.sql.orm.exceptions.ClosedQueryException;
import net.redstonecraft.redstoneapi.sql.orm.exceptions.ConstructException;
import net.redstonecraft.redstoneapi.sql.orm.exceptions.InvalidStructureException;
import net.redstonecraft.redstoneapi.sql.orm.exceptions.NoSuchTableException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is returned by the {@link Session} query methods.
 * You can get the first entry or all as {@link List} or {@link Array}.
 * You also can use {@link Query#getAsJsonObject(BaseType)} or {@link Query#getAsJsonArray()} to simplify the use on the {@link net.redstonecraft.redstoneapi.webserver.WebServer}.
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class Query<T extends TableBase> {

    private final Session session;
    private final Session.TableFields<T> table;
    private boolean done = false;
    private final PreparedStatement ps;

    Query(Session session, Session.TableFields<T> table, Order... orders) throws SQLException {
        this.session = session;
        this.table = table;
        ps = this.session.getSql().getSyntaxRenderer().select(session.getSql(), this.table.clazz, orders);
    }

    Query(Session session, Session.TableFields<T> table, Filter filter, Order... orders) throws SQLException {
        this.session = session;
        this.table = table;
        ps = this.session.getSql().getSyntaxRenderer().select(session.getSql(), this.table.clazz, filter, orders);
    }

    Query(Session session, Session.TableFields<T> table, int limit, Order... orders) throws SQLException {
        this.session = session;
        this.table = table;
        ps = this.session.getSql().getSyntaxRenderer().select(session.getSql(), this.table.clazz, limit, orders);
    }

    Query(Session session, Session.TableFields<T> table, Filter filter, int limit, Order... orders) throws SQLException {
        this.session = session;
        this.table = table;
        ps = this.session.getSql().getSyntaxRenderer().select(session.getSql(), this.table.clazz, filter, limit, orders);
    }

    Query(Session session, Session.TableFields<T> table, int limit, int offset, Order... orders) throws SQLException {
        if (offset < 0) {
            throw new IndexOutOfBoundsException("The offset can't be lower than 0");
        }
        this.session = session;
        this.table = table;
        ps = this.session.getSql().getSyntaxRenderer().select(session.getSql(), this.table.clazz, limit, offset, orders);
    }

    Query(Session session, Session.TableFields<T> table, Filter filter, int limit, int offset, Order... orders) throws SQLException {
        if (offset < 0) {
            throw new IndexOutOfBoundsException("The offset can't be lower than 0");
        }
        this.session = session;
        this.table = table;
        ps = this.session.getSql().getSyntaxRenderer().select(session.getSql(), this.table.clazz, filter, limit, offset, orders);
    }

    public T getFirst() throws ClosedQueryException, ConstructException, SQLException {
        if (done) {
            throw new ClosedQueryException("Query is already completed");
        }
        done = true;
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            T obj;
            try {
                obj = (T) table.clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
                throw new ConstructException(e.getMessage());
            }
            for (Field i : table.fields) {
                try {
                    ((BaseType) i.get(obj)).deserializeSql(rs);
                } catch (IllegalAccessException e) {
                    throw new ConstructException(e.getMessage());
                }
            }
            return obj;
        } else {
            return null;
        }
    }

    public List<T> getAllAsList() throws ClosedQueryException, ConstructException, SQLException {
        if (done) {
            throw new ClosedQueryException("Query is already completed");
        }
        done = true;
        ResultSet rs = ps.executeQuery();
        List<T> list = new ArrayList<>();
        while (rs.next()) {
            T obj;
            try {
                obj = (T) table.clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
                throw new ConstructException(e.getMessage());
            }
            for (Field i : table.fields) {
                try {
                    ((BaseType) i.get(obj)).deserializeSql(rs);
                } catch (IllegalAccessException e) {
                    throw new ConstructException(e.getMessage());
                }
            }
            list.add(obj);
        }
        return list;
    }

    public T[] getAll() throws ClosedQueryException, ConstructException, SQLException {
        return getAllAsList().toArray((T[]) Array.newInstance(table.clazz, 0));
    }

    /**
     * Since a {@link JSONObject} contains key value pairs you need to specify the column that should by used as the key
     *
     * @param key the key to use
     *
     * @return a to {@link JSONObject} parsed table entry
     *
     * @throws ClosedQueryException when the query was already completed
     * @throws ConstructException when it fails to create an object
     * @throws SQLException when there is a sql exception
     * @throws NoSuchTableException if the table does not exist
     * @throws InvalidStructureException if the structure of the table doesn't fit
     * */
    public JSONObject getAsJsonObject(BaseType key) throws ClosedQueryException, ConstructException, SQLException, NoSuchTableException, InvalidStructureException {
        try {
            T model = table.clazz.newInstance();
            Field keyField = null;
            for (Field i : table.fields) {
                if (((BaseType) i.get(model)).getKey().equals(key.getKey())) {
                    keyField = i;
                    break;
                }
            }
            if (keyField == null) {
                throw new NoSuchTableException("Key not found in table");
            }
            if (!(keyField.getAnnotation(Column.class).primaryKey() || keyField.isAnnotationPresent(PrimaryKey.class)) && !(keyField.getAnnotation(Column.class).unique() || keyField.isAnnotationPresent(Unique.class))) {
                throw new InvalidStructureException("The key must be either the primary key or unique");
            }
            JSONObject obj = new JSONObject();
            List<T> list = getAllAsList();
            for (T i : list) {
                obj.put((((BaseType) keyField.get(i)).getValue()).toString(), i.toJsonObject());
            }
            return obj;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConstructException(e.getMessage());
        }
    }

    public JSONArray getAsJsonArray() throws ClosedQueryException, ConstructException, SQLException {
        JSONArray arr = new JSONArray();
        List<T> list = getAllAsList();
        for (T i : list) {
            arr.add(i.toJsonObject());
        }
        return arr;
    }

}
