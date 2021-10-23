package net.redstonecraft.redstoneapi.db.paradigms.relational.statement;

import net.redstonecraft.redstoneapi.core.tuple.Pair;
import net.redstonecraft.redstoneapi.db.paradigms.relational.Model;

import java.util.List;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public interface StatementBuilder<T extends Model> {

    StatementBuilder<T> create(Class<T> model);
    StatementBuilder<T> insert(T model);
    StatementBuilder<T> update(T model);
    StatementBuilder<T> delete(Class<T> model);
    StatementBuilder<T> drop(Class<T> model);
    StatementBuilder<T> select(Class<T> model, String... columns);
    StatementBuilder<T> sum(Class<T> model, String column);
    StatementBuilder<T> avg(Class<T> model, String column);
    StatementBuilder<T> count(Class<T> model, String column);

    StatementBuilder<T> where(Filter filter);

    StatementBuilder<T> orderBy(Order... orders);
    StatementBuilder<T> limit(int limit);
    StatementBuilder<T> offset(int offset);

    Pair<String, List<Object>> build();

}
