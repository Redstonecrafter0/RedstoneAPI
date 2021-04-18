package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.tools.HttpHeader;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Parsed arguments in an url
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class WebArgument {

    private final String key;
    private final String value;

    public WebArgument(String key, String value) {
        String key1;
        String value1;
        try {
            key1 = URLDecoder.decode(key, "UTF-8");
            value1 = URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            key1 = null;
            value1 = null;
        }
        this.key = key1;
        this.value = value1;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String[] getValueArray() {
        return value.split(",");
    }

    /**
     * Get a {@link HttpHeader} by the key.
     *
     * @param args the arguments to get from
     * @param key the key to use
     *
     * @return null if not found
     * */
    public static WebArgument getByKey(WebArgument[] args, String key) {
        for (WebArgument i : args) {
            if (i.getKey().equals(key)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Check if the key is in one of the {@link HttpHeader}s.
     *
     * @param webArguments the arguments to get from
     * @param key the key to use
     *
     * @return if the webArguments contains the key
     * */
    public boolean containsKey(WebArgument[] webArguments, String key) {
        return getByKey(webArguments, key) != null;
    }

    @Override
    public String toString() {
        return "WebArgument{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
