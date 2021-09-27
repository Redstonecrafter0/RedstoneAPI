package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.webserver.handler.RequestHandler;

import java.lang.reflect.Method;

public class HandlerBundle {

    private final RequestHandler handler;
    private final Method method;

    public HandlerBundle(RequestHandler handler, Method method) {
        this.handler = handler;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public RequestHandler getHandler() {
        return handler;
    }
}
