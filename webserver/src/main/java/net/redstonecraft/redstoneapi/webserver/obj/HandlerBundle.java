package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.webserver.RequestHandler;
import net.redstonecraft.redstoneapi.webserver.WebRequest;
import net.redstonecraft.redstoneapi.webserver.annotations.QueryParam;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public record HandlerBundle(RequestHandler handler, Method method) {

    public Object invoke(WebRequest request) throws InvocationTargetException, IllegalAccessException {
        List<Object> params = new ArrayList<>();
        params.add(request);
        Parameter[] parameters = method.getParameters();
        for (int i = 1; i < parameters.length; i++) {
            params.add(request.getArgs().get(parameters[i].getAnnotation(QueryParam.class).value()));
        }
        return method.invoke(handler, params.toArray(new Object[0]));
    }

}
