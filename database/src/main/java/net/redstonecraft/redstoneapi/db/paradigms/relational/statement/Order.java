package net.redstonecraft.redstoneapi.db.paradigms.relational.statement;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public record Order(String column, OrderDirection direction) {

    public String getDirectionString() {
        return direction.getRepr();
    }

}
