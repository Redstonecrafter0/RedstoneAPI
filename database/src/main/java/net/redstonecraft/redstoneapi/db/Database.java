package net.redstonecraft.redstoneapi.db;

import net.redstonecraft.redstoneapi.db.paradigms.Paradigm;

import java.io.Closeable;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public abstract class Database implements Closeable {

    public abstract Paradigm getParadigm();

}
