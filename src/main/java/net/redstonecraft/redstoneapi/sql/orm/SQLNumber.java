package net.redstonecraft.redstoneapi.sql.orm;

/**
 * This class is extended when the table type is a number.
 * Itself extends {@link BaseType}
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class SQLNumber<T extends Number> extends BaseType<T> {

    public SQLNumber() {
    }

    public SQLNumber(T value) {
        super(value);
    }

}
