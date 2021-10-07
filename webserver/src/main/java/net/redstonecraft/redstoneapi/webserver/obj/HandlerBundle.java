package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.webserver.RequestHandler;
import net.redstonecraft.redstoneapi.webserver.WebRequest;
import net.redstonecraft.redstoneapi.webserver.annotations.FormParam;
import net.redstonecraft.redstoneapi.webserver.annotations.QueryParam;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HandlerBundle {

    private final RequestHandler handler;
    private final Method method;

    public HandlerBundle(RequestHandler handler, Method method) {
        this.handler = handler;
        this.method = method;
    }

    public Object invoke(WebRequest request) throws InvocationTargetException, IllegalAccessException {
        List<Object> params = new ArrayList<>();
        params.add(request);
        Parameter[] parameters = method.getParameters();
        for (int i = 1; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(QueryParam.class)) {
                params.add(request.getArgs().get(parameters[i].getAnnotation(QueryParam.class).value()));
            } else {
                params.add(request.getFormData().get(parameters[i].getAnnotation(FormParam.class).value()));
            }
        }
        return method.invoke(handler, params.toArray(new Object[0]));
    }

    public RequestHandler getHandler() {
        return handler;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (HandlerBundle) obj;
        return Objects.equals(this.handler, that.handler) &&
                Objects.equals(this.method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handler, method);
    }

    @Override
    public String toString() {
        return "HandlerBundle[" +
                "handler=" + handler + ", " +
                "method=" + method + ']';
    }


}
