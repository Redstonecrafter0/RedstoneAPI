package net.redstonecraft.redstoneapi.db.paradigms.keyvalue;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class Redis extends KeyValueDB {

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
        jedis.set(name + key, data);
    }

    @Override
    public void set(String key, Boolean data) {
        jedis.set(name + key, String.valueOf(data));
    }

    @Override
    public void set(String key, Character data) {
        jedis.set(name + key, String.valueOf(data));
    }

    @Override
    public void set(String key, Byte data) {
        jedis.set(name + key, String.valueOf(data));
    }

    @Override
    public void set(String key, Short data) {
        jedis.set(name + key, String.valueOf(data));
    }

    @Override
    public void set(String key, Integer data) {
        jedis.set(name + key, String.valueOf(data));
    }

    @Override
    public void set(String key, Long data) {
        jedis.set(name + key, String.valueOf(data));
    }

    @Override
    public void set(String key, Float data) {
        jedis.set(name + key, String.valueOf(data));
    }

    @Override
    public void set(String key, Double data) {
        jedis.set(name + key, String.valueOf(data));
    }

    @Override
    public void set(String key, byte[] data) {
        jedis.set((name + key).getBytes(StandardCharsets.UTF_8), data);
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
