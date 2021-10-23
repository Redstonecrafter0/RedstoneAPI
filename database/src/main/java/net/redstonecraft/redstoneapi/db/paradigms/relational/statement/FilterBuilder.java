package net.redstonecraft.redstoneapi.db.paradigms.relational.statement;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public interface FilterBuilder {

    Filter equal(String column, Object value);
    Filter notEqual(String column, Object value);
    Filter lessThan(String column, Object value);
    Filter lessThanOrEqual(String column, Object value);
    Filter greaterThan(String column, Object value);
    Filter greaterThanOrEqual(String column, Object value);
    Filter like(String column, String pattern);
    Filter notLike(String column, String pattern);
    Filter between(String column, Number first, Number second);
    Filter notBetween(String column, Number first, Number second);

}
