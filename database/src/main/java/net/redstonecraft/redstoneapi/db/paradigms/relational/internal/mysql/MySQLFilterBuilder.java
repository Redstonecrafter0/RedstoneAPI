package net.redstonecraft.redstoneapi.db.paradigms.relational.internal.mysql;

import net.redstonecraft.redstoneapi.db.paradigms.relational.internal.base.SQLFilter;
import net.redstonecraft.redstoneapi.db.paradigms.relational.internal.base.SQLFilterBuilder;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.Filter;

import static net.redstonecraft.redstoneapi.db.paradigms.relational.internal.base.SQLStatementBuilder.escapeColumn;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class MySQLFilterBuilder extends SQLFilterBuilder {

    @Override
    public Filter equal(String column, Object value) {
        return new SQLFilter(escapeColumn(column) + " <=> ?", value);
    }

}
