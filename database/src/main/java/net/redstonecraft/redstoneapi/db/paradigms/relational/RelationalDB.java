package net.redstonecraft.redstoneapi.db.paradigms.relational;

import net.redstonecraft.redstoneapi.core.tools.Enumerate;
import net.redstonecraft.redstoneapi.core.tuple.Pair;
import net.redstonecraft.redstoneapi.db.Database;
import net.redstonecraft.redstoneapi.db.paradigms.Paradigm;
import net.redstonecraft.redstoneapi.db.paradigms.relational.annotations.Column;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.*;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public abstract class RelationalDB extends Database {

    @Override
    public final Paradigm getParadigm() {
        return Paradigm.RELATIONAL;
    }

    public abstract <T extends Model> StatementBuilder<T> createStatement(Class<T> clazz);
    public abstract FilterBuilder createFilter();
    public abstract FilterChainBuilder createFilterChainBuilder(Filter filter);

    public abstract PreparedStatement prepareStatement(String sql) throws SQLException;

    public <T extends Model> void createTable(Class<T> clazz) throws SQLException {
        update(createStatement(clazz).create(clazz));
    }

    public <T extends Model> void drop(Class<T> clazz) throws SQLException {
        update(createStatement(clazz).drop(clazz));
    }

    public <T extends Model> void insert(T entity) throws SQLException {
        update(((StatementBuilder<T>) createStatement(entity.getClass())).insert(entity));
    }

    public <T extends Model> void update(T entity) throws SQLException {
        try {
            update(((StatementBuilder<T>) createStatement(entity.getClass())).update(entity).where(createFilter().equal(entity.getPrimaryKey().getAnnotation(Column.class).value(), entity.getPrimaryKey().get(entity))));
        } catch (IllegalAccessException ignored) {
        }
    }

    public <T extends Model> void delete(T entity) throws SQLException {
        try {
            update(((StatementBuilder<T>) createStatement(entity.getClass())).delete((Class<T>) entity.getClass()).where(createFilter().equal(entity.getPrimaryKey().getAnnotation(Column.class).value(), entity.getPrimaryKey().get(entity))));
        } catch (IllegalAccessException ignored) {
        }
    }

    public abstract int update(String sql) throws SQLException;

    public int update(StatementBuilder<?> statementBuilder) throws SQLException {
        Pair<String, List<Object>> statement = statementBuilder.build();
        PreparedStatement preparedStatement = prepareStatement(statement.first());
        for (Enumerate.Item<Object> i : new Enumerate<>(statement.second())) {
            switch (i.value()) {
                case Integer v -> preparedStatement.setInt(i.position() + 1, v);
                case Long v -> preparedStatement.setLong(i.position() + 1, v);
                case Double v -> preparedStatement.setDouble(i.position() + 1, v);
                case Float v -> preparedStatement.setFloat(i.position() + 1, v);
                case Boolean v -> preparedStatement.setBoolean(i.position() + 1, v);
                case String v -> preparedStatement.setString(i.position() + 1, v);
                case Character v -> preparedStatement.setString(i.position() + 1, String.valueOf(v.charValue()));
                case byte[] v -> preparedStatement.setBlob(i.position() + 1, new ByteArrayInputStream(v));
                default -> preparedStatement.setNull(i.position() + 1, 0);
            }
        }
        return preparedStatement.executeUpdate();
    }

}
