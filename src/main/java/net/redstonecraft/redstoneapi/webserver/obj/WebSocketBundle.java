package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.webserver.handler.RequestHandler;

import java.lang.reflect.Method;

public class WebSocketBundle {

    private final RequestHandler handler;
    private final Method method;

    public WebSocketBundle(RequestHandler handler, Method method) {
        this.handler = handler;
        this.method = method;
    }

    public RequestHandler getHandler() {
        return handler;
    }

    public Method getMethod() {
        return method;
    }
}
