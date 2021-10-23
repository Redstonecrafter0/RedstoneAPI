package net.redstonecraft.redstoneapi.db.paradigms.relational.internal.base;

import net.redstonecraft.redstoneapi.core.tuple.Pair;
import net.redstonecraft.redstoneapi.db.paradigms.relational.Model;
import net.redstonecraft.redstoneapi.db.paradigms.relational.annotations.Column;
import net.redstonecraft.redstoneapi.db.paradigms.relational.annotations.PrimaryKey;
import net.redstonecraft.redstoneapi.db.paradigms.relational.annotations.Table;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.Filter;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.Order;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.StatementBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public abstract class SQLStatementBuilder<T extends Model> implements StatementBuilder<T> {

    protected final StringBuilder sb = new StringBuilder();
    protected final List<Object> data = new ArrayList<>();

    public static String escapeColumn(String column) {
        return "`" + column.replace("`", "``") + "`";
    }

    @Override
    public StatementBuilder<T> insert(T model) {
        List<Pair<String, Object>> list = model.getDatafields().stream().map(i -> {
            try {
                return new Pair<>(i.getAnnotation(Column.class).value(), i.get(model));
            } catch (IllegalAccessException e) {
                return null;
            }
        }).filter(i -> i.second() != null).collect(Collectors.toList());
        sb.append("INSERT INTO ")
                .append(escapeColumn(model.getTablename()))
                .append(" (")
                .append(list.stream().map(i -> escapeColumn(i.first())).collect(Collectors.joining(",")))
                .append(") VALUES (")
                .append(list.stream().map(i -> "?").collect(Collectors.joining(",")))
                .append(")");
        data.addAll(list.stream().map(Pair::second).toList());
        return this;
    }

    @Override
    public StatementBuilder<T> update(T model) {
        List<Pair<String, Object>> list = model.getDatafields().stream().filter(i -> !i.isAnnotationPresent(PrimaryKey.class)).map(i -> {
            try {
                return new Pair<>(i.getAnnotation(Column.class).value(), i.get(model));
            } catch (IllegalAccessException e) {
                return null;
            }
        }).collect(Collectors.toList());
        sb.append("UPDATE ")
                .append(escapeColumn(model.getTablename()))
                .append(" SET ")
                .append(list.stream().map(i -> escapeColumn(i.first()) + " = ?").collect(Collectors.joining(",")));
        data.addAll(list.stream().map(Pair::second).toList());
        return this;
    }

    @Override
    public StatementBuilder<T> delete(Class<T> model) {
        sb.append("DELETE FROM ")
                .append(escapeColumn(model.getAnnotation(Table.class).value()));
        return this;
    }

    @Override
    public StatementBuilder<T> drop(Class<T> model) {
        sb.append("DROP TABLE IF EXISTS ")
                .append(escapeColumn(model.getAnnotation(Table.class).value()));
        return this;
    }

    @Override
    public StatementBuilder<T> select(Class<T> model, String... columns) {
        sb.append("SELECT ")
                .append(Arrays.stream(columns).map(i -> "`" + i.replace("`", "``")).collect(Collectors.joining(",")))
                .append(" FROM ")
                .append(escapeColumn(model.getAnnotation(Table.class).value()));
        return this;
    }

    @Override
    public StatementBuilder<T> sum(Class<T> model, String column) {
        return null;
    }

    @Override
    public StatementBuilder<T> avg(Class<T> model, String column) {
        return null;
    }

    @Override
    public StatementBuilder<T> count(Class<T> model, String column) {
        return null;
    }

    @Override
    public StatementBuilder<T> where(Filter filter) {
        sb.append(" WHERE ").append(filter.asString());
        return this;
    }

    @Override
    public StatementBuilder<T> orderBy(Order... orders) {
        if (orders.length >= 1) {
            sb.append(" ORDER BY ").append(Arrays.stream(orders).map(i ->
                    escapeColumn(i.column()) + " " + i.getDirectionString()
            ).collect(Collectors.joining(",")));
        }
        return this;
    }

    @Override
    public StatementBuilder<T> limit(int limit) {
        sb.append(" LIMIT ").append(limit);
        return this;
    }

    @Override
    public StatementBuilder<T> offset(int offset) {
        sb.append(" OFFSET ").append(offset);
        return this;
    }

    @Override
    public Pair<String, List<Object>> build() {
        return new Pair<>(sb.toString(), data);
    }

}
