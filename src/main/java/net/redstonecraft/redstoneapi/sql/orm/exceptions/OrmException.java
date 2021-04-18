package net.redstonecraft.redstoneapi.sql.orm.exceptions;

/**
 * The base class for every orm exception
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class OrmException extends Exception {

    public OrmException(String message) {
        super(message);
    }

}
