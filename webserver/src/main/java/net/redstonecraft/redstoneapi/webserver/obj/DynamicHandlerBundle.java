package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.core.Pair;
import net.redstonecraft.redstoneapi.webserver.RequestHandler;
import net.redstonecraft.redstoneapi.webserver.WebRequest;
import net.redstonecraft.redstoneapi.webserver.annotations.FormParam;
import net.redstonecraft.redstoneapi.webserver.annotations.QueryParam;
import net.redstonecraft.redstoneapi.webserver.annotations.RouteParam;
import net.redstonecraft.redstoneapi.webserver.internal.exceptions.NoRouteParamException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class DynamicHandlerBundle extends HandlerBundle {

    private final Pair<String, Boolean>[] route;

    public DynamicHandlerBundle(RequestHandler handler, Method method, String path) throws IndexOutOfBoundsException, NoRouteParamException {
        super(handler, method);
        String[] parts = path.substring(1).split("/");
        //noinspection unchecked
        route = new Pair[parts.length];
        for (int i = 0; i < parts.length; i++) {
            route[i] = parts[i].startsWith("<") && parts[i].endsWith(">") ? new Pair<>(parts[i].substring(1, parts[i].length() - 1), true) : new Pair<>(parts[i], false);
        }
        if (Arrays.stream(route).noneMatch(Pair::getSecond)) {
            throw new NoRouteParamException();
        }
    }

    @Override
    public Object invoke(WebRequest request) throws InvocationTargetException, IllegalAccessException {
        String[] parts = request.getPath().substring(1).split("/");
        Map<String, String> routeParams = new HashMap<>();
        for (int i = 0; i < parts.length; i++) {
            if (route[i].getSecond()) {
                routeParams.put(route[i].getFirst(), parts[i]);
            }
        }
        List<Object> params = new ArrayList<>();
        params.add(request);
        Parameter[] parameters = getMethod().getParameters();
        for (int i = 1; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(QueryParam.class)) {
                params.add(request.getArgs().get(parameters[i].getAnnotation(QueryParam.class).value()));
            } else if (parameters[i].isAnnotationPresent(FormParam.class)) {
                params.add(request.getFormData().get(parameters[i].getAnnotation(FormParam.class).value()));
            } else {
                params.add(routeParams.get(parameters[i].getAnnotation(RouteParam.class).value()));
            }
        }
        return getMethod().invoke(getHandler(), params.toArray(new Object[0]));
    }

    public Pair<String, Boolean>[] getRoute() {
        return route;
    }

}
