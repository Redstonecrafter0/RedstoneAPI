package net.redstonecraft.redstoneapi.db.connections.internal;

import net.redstonecraft.redstoneapi.db.connections.KeyValueConnection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class Redis implements KeyValueConnection {

    private final Jedis jedis;
    private final String name;

    public Redis(String name) {
        this(Protocol.DEFAULT_HOST, name);
    }

    public Redis(String host, String name) {
        this(host, Protocol.DEFAULT_PORT, name);
    }

    public Redis(String host, int port, String name) {
        jedis = new Jedis(host, port);
        this.name = "redstoneapi:" + name + ":";
    }

    @Override
    public void set(String key, String data) {
    }

    @Override
    public void set(String key, Boolean data) {
    }

    @Override
    public void set(String key, Character data) {
    }

    @Override
    public void set(String key, Byte data) {
    }

    @Override
    public void set(String key, Short data) {
    }

    @Override
    public void set(String key, Integer data) {
    }

    @Override
    public void set(String key, Long data) {
    }

    @Override
    public void set(String key, Float data) {
    }

    @Override
    public void set(String key, Double data) {
    }

    @Override
    public void set(String key, byte[] data) {
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public Boolean getBoolean(String key) {
        return null;
    }

    @Override
    public Character getCharacter(String key) {
        return null;
    }

    @Override
    public Byte getByte(String key) {
        return null;
    }

    @Override
    public Short getShort(String key) {
        return null;
    }

    @Override
    public Integer getInteger(String key) {
        return null;
    }

    @Override
    public Long getLong(String key) {
        return null;
    }

    @Override
    public Float getFloat(String key) {
        return null;
    }

    @Override
    public Double getDouble(String key) {
        return null;
    }

    @Override
    public byte[] getData(String key) {
        return new byte[0];
    }

    @Override
    public Collection<String> getKeys() {
        return jedis.keys(name
                .replace("\\", "\\\\")
                .replace("?", "\\?")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]") + "*");
    }

    @Override
    public boolean hasKey(String key) {
        return jedis.exists((name + key)
                .replace("\\", "\\\\")
                .replace("?", "\\?")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
        );
    }

    @Override
    public void close() {
        jedis.close();
    }

}
