package net.redstonecraft.redstoneapi.db.paradigms.relational.internal.base;

import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.Filter;

import java.util.Arrays;
import java.util.List;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public record SQLFilter(String sql, Object... values) implements Filter {

    @Override
    public String asString() {
        return sql;
    }

    @Override
    public List<Object> getValues() {
        return Arrays.asList(values);
    }

}
