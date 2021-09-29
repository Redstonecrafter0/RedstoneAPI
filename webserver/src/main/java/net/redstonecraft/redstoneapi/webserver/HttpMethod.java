package net.redstonecraft.redstoneapi.webserver;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public enum HttpMethod {

    GET(false, true),
    POST(true, true),
    HEAD(false, false),
    PUT(true, false),
    DELETE(false, true),
    OPTIONS(false, true),
    PATCH(true, true);

    private final boolean hasBody;
    private final boolean hasBodyResponse;

    HttpMethod(boolean hasBody, boolean hasBodyResponse) {
        this.hasBody = hasBody;
        this.hasBodyResponse = hasBodyResponse;
    }

    public boolean hasBody() {
        return hasBody;
    }

    public boolean hasBodyResponse() {
        return hasBodyResponse;
    }

    public static boolean isMethodAvailable(String method) {
        try {
            HttpMethod.valueOf(method);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

}
