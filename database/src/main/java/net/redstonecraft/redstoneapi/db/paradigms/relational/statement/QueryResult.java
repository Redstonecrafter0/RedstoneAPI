package net.redstonecraft.redstoneapi.db.paradigms.relational.statement;

import net.redstonecraft.redstoneapi.db.paradigms.relational.Model;

import java.util.Iterator;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public interface QueryResult<T extends Model> extends Iterable<T>, Iterator<T> {

    @Override
    default Iterator<T> iterator() {
        return this;
    }

}
