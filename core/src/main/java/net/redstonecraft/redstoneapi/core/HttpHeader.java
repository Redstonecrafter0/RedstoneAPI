package net.redstonecraft.redstoneapi.core;

import java.util.List;

/**
 * HttpHeader class for providing http headers in {@link HttpRequest}
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public record HttpHeader(String key, String value) implements Comparable<HttpHeader> {

    /**
     * Get a {@link HttpHeader} by the key.
     *
     * @param headers the headers to get from
     * @param key the key from which to get the value from
     *
     * @return null if not found
     * */
    public static HttpHeader getByKey(HttpHeader[] headers, String key) {
        for (HttpHeader i : headers) {
            if (i.key().equalsIgnoreCase(key)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Get a {@link HttpHeader} by the key.
     *
     * @param headers the headers to get from
     * @param key the key from which to get the value from
     *
     * @return null if not found
     * */
    public static HttpHeader getByKey(List<HttpHeader> headers, String key) {
        for (HttpHeader i : headers) {
            if (i.key().equalsIgnoreCase(key)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Check if the key is in one of the {@link HttpHeader}s.
     *
     * @param headers the headers to check on
     * @param key the key from which to check the existance from
     *
     * @return if the key exists
     * */
    public static boolean containsKey(HttpHeader[] headers, String key) {
        return getByKey(headers, key) != null;
    }

    @Override
    public int compareTo(HttpHeader o) {
        return key.compareTo(o.key);
    }

}
