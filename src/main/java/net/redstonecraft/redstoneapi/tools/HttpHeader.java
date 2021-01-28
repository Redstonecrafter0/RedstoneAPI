package net.redstonecraft.redstoneapi.tools;

/**
 * HttpHeader class for providing http headers in {@link HttpRequest}
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class HttpHeader {

    public final String key;
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

    @Override
    public String toString() {
        return "HttpHeader{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
