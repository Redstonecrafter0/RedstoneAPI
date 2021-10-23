package net.redstonecraft.redstoneapi.db.paradigms.relational.statement;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public interface FilterChainBuilder {

    FilterChainBuilder and(Filter filter);
    FilterChainBuilder or(Filter filter);

    Filter build();

}
