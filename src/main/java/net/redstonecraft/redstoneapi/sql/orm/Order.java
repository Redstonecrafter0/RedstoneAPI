package net.redstonecraft.redstoneapi.sql.orm;

/**
 * This class is used to sort a SQL query
 *
 * @author Redstonecrafter0
 * */
public class Order {

    private final BaseType key;
    private final Direction direction;

    public Order(BaseType key, Direction direction) {
        this.key = key;
        this.direction = direction;
    }

    public BaseType getKey() {
        return key;
    }

    public Direction getDirection() {
        return direction;
    }

    public enum Direction {

        ASCENDING("ASC"),
        DESCENDING("DESC");

        private final String identifier;

        Direction(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }
    }

}
