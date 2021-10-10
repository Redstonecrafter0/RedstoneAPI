package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.webserver.RequestHandler;

import java.lang.reflect.Method;

public record WebSocketBundle(RequestHandler handler, Method method) {
}
