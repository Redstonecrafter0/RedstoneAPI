package net.redstonecraft.redstoneapi.db.paradigms.relational.internal.mysql;

import net.redstonecraft.redstoneapi.db.paradigms.relational.Model;
import net.redstonecraft.redstoneapi.db.paradigms.relational.annotations.Column;
import net.redstonecraft.redstoneapi.db.paradigms.relational.annotations.Table;
import net.redstonecraft.redstoneapi.db.paradigms.relational.internal.base.SQLStatementBuilder;
import net.redstonecraft.redstoneapi.db.paradigms.relational.statement.StatementBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class MySQLStatementBuilder<T extends Model> extends SQLStatementBuilder<T> {

    @Override
    public StatementBuilder<T> create(Class<T> model) {
        sb.append("CREATE TABLE ")
                .append(escapeColumn(model.getAnnotation(Table.class).value()))
                .append(" IF NOT EXISTS (")
                .append(Arrays.stream(model.getFields()).filter(i -> i.isAnnotationPresent(Column.class)).map(MySQL::renderColumn).collect(Collectors.joining(",")))
                .append(")");
        return this;
    }

}
