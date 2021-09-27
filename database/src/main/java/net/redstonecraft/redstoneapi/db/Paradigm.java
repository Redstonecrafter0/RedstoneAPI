package net.redstonecraft.redstoneapi.db;

/**
 * redstoneapi
 *
 * @author Redstonecrafter0
 */
public enum Paradigm {

    KEY_VALUE(1),
    WIDE_COLUMN(2),
    DOCUMENT(3),
    RELATIONAL(4),
    GRAPH(5);

    private final int complexityLevel;

    Paradigm(int complexityLevel) {
        this.complexityLevel = complexityLevel;
    }

    public int getComplexityLevel() {
        return complexityLevel;
    }

}
