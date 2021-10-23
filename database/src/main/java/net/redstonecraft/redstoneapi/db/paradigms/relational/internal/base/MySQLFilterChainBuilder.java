package net.redstonecraft.redstoneapi.db.paradigms.relational.internal.base;

import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.Filter;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.FilterChainBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class MySQLFilterChainBuilder implements FilterChainBuilder {

    private String sql;
    private final List<Object> values;

    public MySQLFilterChainBuilder(Filter filter) {
        sql = filter.asString();
        values = new ArrayList<>(filter.getValues());
    }

    @Override
    public FilterChainBuilder and(Filter filter) {
        sql = "(" + sql + " AND " + filter.asString() + ")";
        values.addAll(filter.getValues());
        return this;
    }

    @Override
    public FilterChainBuilder or(Filter filter) {
        sql = "(" + sql + " OR " + filter.asString() + ")";
        values.addAll(filter.getValues());
        return this;
    }

    @Override
    public Filter build() {
        return new SQLFilter(sql, values);
    }

}
