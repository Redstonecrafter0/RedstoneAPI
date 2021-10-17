package net.redstonecraft.redstoneapi.db.paradigms.keyvalue;

import net.redstonecraft.redstoneapi.db.Database;
import net.redstonecraft.redstoneapi.db.paradigms.Paradigm;

import java.util.Collection;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public abstract class KeyValueDB extends Database {

    @Override
    public final Paradigm getParadigm() {
        return Paradigm.KEY_VALUE;
    }

    public abstract void set(String key, String data);
    public abstract void set(String key, Boolean data);
    public abstract void set(String key, Character data);
    public abstract void set(String key, Byte data);
    public abstract void set(String key, Short data);
    public abstract void set(String key, Integer data);
    public abstract void set(String key, Long data);
    public abstract void set(String key, Float data);
    public abstract void set(String key, Double data);
    public abstract void set(String key, byte[] data);

    public abstract String getString(String key);
    public abstract Boolean getBoolean(String key);
    public abstract Character getCharacter(String key);
    public abstract Byte getByte(String key);
    public abstract Short getShort(String key);
    public abstract Integer getInteger(String key);
    public abstract Long getLong(String key);
    public abstract Float getFloat(String key);
    public abstract Double getDouble(String key);
    public abstract byte[] getData(String key);

    public abstract Collection<String> getKeys();
    public abstract boolean hasKey(String key);

}
