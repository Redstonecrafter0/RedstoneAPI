package net.redstonecraft.redstoneapi.db.paradigms.relational.internal.base;

import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.Filter;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.FilterBuilder;

import static net.redstonecraft.redstoneapi.db.paradigms.relational.internal.base.SQLStatementBuilder.escapeColumn;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public abstract class SQLFilterBuilder implements FilterBuilder {

    @Override
    public Filter notEqual(String column, Object value) {
        return new SQLFilter(escapeColumn(column) + " != ?", value);
    }

    @Override
    public Filter lessThan(String column, Object value) {
        return new SQLFilter(escapeColumn(column) + " < ?", value);
    }

    @Override
    public Filter lessThanOrEqual(String column, Object value) {
        return new SQLFilter(escapeColumn(column) + " <= ?", value);
    }

    @Override
    public Filter greaterThan(String column, Object value) {
        return new SQLFilter(escapeColumn(column) + " > ?", value);
    }

    @Override
    public Filter greaterThanOrEqual(String column, Object value) {
        return new SQLFilter(escapeColumn(column) + " >= ?", value);
    }

    @Override
    public Filter like(String column, String pattern) {
        return new SQLFilter(escapeColumn(column) + " LIKE ?", pattern);
    }

    @Override
    public Filter notLike(String column, String pattern) {
        return new SQLFilter(escapeColumn(column) + " NOT LIKE ?", pattern);
    }

    @Override
    public Filter between(String column, Number first, Number second) {
        return new SQLFilter(escapeColumn(column) + " BETWEEN ? AND ?", first, second);
    }

    @Override
    public Filter notBetween(String column, Number first, Number second) {
        return new SQLFilter(escapeColumn(column) + " NOT BETWEEN ? AND ?", first, second);
    }

}
