package net.redstonecraft.redstoneapi.db.connections;

import java.util.Collection;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public interface KeyValueConnection extends Connection {

    void set(String key, String data);
    void set(String key, Boolean data);
    void set(String key, Character data);
    void set(String key, Byte data);
    void set(String key, Short data);
    void set(String key, Integer data);
    void set(String key, Long data);
    void set(String key, Float data);
    void set(String key, Double data);
    void set(String key, byte[] data);

    String getString(String key);
    Boolean getBoolean(String key);
    Character getCharacter(String key);
    Byte getByte(String key);
    Short getShort(String key);
    Integer getInteger(String key);
    Long getLong(String key);
    Float getFloat(String key);
    Double getDouble(String key);
    byte[] getData(String key);

    Collection<String> getKeys();
    boolean hasKey(String key);

}
