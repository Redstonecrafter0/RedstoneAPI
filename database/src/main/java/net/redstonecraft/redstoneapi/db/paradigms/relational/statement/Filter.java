package net.redstonecraft.redstoneapi.db.paradigms.relational.statement;

import java.util.List;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public interface Filter {

    String asString();
    List<Object> getValues();

}
