package net.redstonecraft.redstoneapi.db.paradigms.relational.statement;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public enum OrderDirection {

    ASCENT("ASC"),
    DESCEND("DESC");

    private final String repr;

    OrderDirection(String repr) {
        this.repr = repr;
    }

    public String getRepr() {
        return repr;
    }

}
