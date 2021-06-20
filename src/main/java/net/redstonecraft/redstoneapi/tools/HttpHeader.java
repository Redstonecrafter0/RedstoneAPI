package net.redstonecraft.redstoneapi.tools;

/**
 * HttpHeader class for providing http headers in {@link HttpRequest}
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class HttpHeader implements Comparable<HttpHeader> {

    /**
     * @deprecated
     * @see HttpHeader#getKey()
     * */
    public final String key;
    /**
     * @deprecated
     * @see HttpHeader#getValue()
     * */
    public final String value;

    /**
     * Create a http header for {@link HttpRequest}
     *
     * @param key header key
     * @param value header value
     * */
    public HttpHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

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
            if (i.getKey().equalsIgnoreCase(key)) {
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
    public String toString() {
        return "HttpHeader{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public int compareTo(HttpHeader o) {
        return key.compareTo(o.key);
    }

}
