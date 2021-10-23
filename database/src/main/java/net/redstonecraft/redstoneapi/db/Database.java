package net.redstonecraft.redstoneapi.db;

import net.redstonecraft.redstoneapi.db.paradigms.Paradigm;

import java.io.IOException;
import java.sql.SQLException;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public abstract class Database implements AutoCloseable {

    public abstract Paradigm getParadigm();

    @Override
    public abstract void close() throws IOException, SQLException;

}
