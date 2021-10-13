package net.redstonecraft.redstoneapi.core;

import java.util.List;
import java.util.Optional;

/**
 * HttpHeader record for representation of Http Headers
 * @param key the header key
 * @param value the header value
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public record HttpHeader(String key, String value) implements Comparable<HttpHeader> {

    /**
     * Get a {@link HttpHeader} by the key from an array.
     *
     * @param headers the headers to get from
     * @param key the key from which to get the value from
     *
     * @return the header or null if not found
     * */
    public static Optional<HttpHeader> getByKey(HttpHeader[] headers, String key) {
        for (HttpHeader i : headers) {
            if (i.key().equalsIgnoreCase(key)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    /**
     * Get a {@link HttpHeader} by the key from a list.
     *
     * @param headers the headers to get from
     * @param key the key from which to get the value from
     *
     * @return the header or null if not found
     * */
    public static Optional<HttpHeader> getByKey(List<HttpHeader> headers, String key) {
        for (HttpHeader i : headers) {
            if (i.key().equalsIgnoreCase(key)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    /**
     * Check if the key is in one of the {@link HttpHeader}s.
     *
     * @param headers the headers to check on
     * @param key the key from which to check the existence from
     *
     * @return if the key exists
     * */
    public static boolean containsKey(HttpHeader[] headers, String key) {
        return getByKey(headers, key).isPresent();
    }

    /**
     * Compares the keys of the headers
     *
     * @param o the header to compare with
     * @return result of comparison
     */
    @Override
    public int compareTo(HttpHeader o) {
        return key.compareTo(o.key);
    }

}
