package net.redstonecraft.redstoneapi.webserver.obj;

public class RequestBundle {

    private final String path;
    private final String method;

    public RequestBundle(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }
}
