package net.redstonecraft.redstoneapi.webserver.internal;

import net.redstonecraft.redstoneapi.core.Pair;
import net.redstonecraft.redstoneapi.webserver.HttpMethod;
import net.redstonecraft.redstoneapi.webserver.RequestHandler;
import net.redstonecraft.redstoneapi.webserver.obj.DynamicHandlerBundle;
import net.redstonecraft.redstoneapi.webserver.obj.HandlerBundle;

import java.util.*;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class HandlerManager {

    private final Map<HttpMethod, Map<String, HandlerBundle>> handlers = new HashMap<>();
    private final Map<HttpMethod, Map<String[], DynamicHandlerBundle>> dynamicMap = new HashMap<>();

    public void setHandler(HttpMethod method, String path, HandlerBundle handlerBundle) {
        if (handlerBundle == null) {
            Map<String, HandlerBundle> handlerBundleMap = handlers.get(method);
            if (handlerBundleMap != null) {
                handlerBundleMap.remove(path);
                if (handlerBundleMap.isEmpty()) {
                    handlers.remove(method);
                }
            }
        } else if (handlerBundle instanceof DynamicHandlerBundle dynamicHandlerBundle) {
            if (!dynamicMap.containsKey(method)) {
                dynamicMap.put(method, new LinkedHashMap<>());
            }
            dynamicMap.get(method).put(Arrays.stream(dynamicHandlerBundle.getRoute()).map(i -> i.getSecond() ? null : i.getFirst()).toArray(String[]::new), dynamicHandlerBundle);
        } else {
            if (!handlers.containsKey(method)) {
                handlers.put(method, new HashMap<>());
            }
            handlers.get(method).put(path, handlerBundle);
        }
    }

    public HandlerBundle getHandler(HttpMethod method, String path) {
        Map<String, HandlerBundle> map = handlers.get(method);
        if (map != null) {
            HandlerBundle handlerBundle = map.get(path);
            if (handlerBundle != null) {
                return handlerBundle;
            }
            if (path.endsWith("/")) {
                handlerBundle = map.get(path.substring(0, path.length() - 1));
            } else {
                handlerBundle = map.get(path + "/");
            }
            if (handlerBundle != null) {
                return handlerBundle;
            }
            Map<String[], DynamicHandlerBundle> map1 = dynamicMap.get(method);
            if (map1 == null) {
                return null;
            }
            String[] parts = path.substring(1).split("/");
            String[] key = new String[parts.length];
            for (int i = 0; i < parts.length; i++) {
                key[i] = parts[i].startsWith("<") && parts[i].endsWith(">") ? null : parts[i];
            }
            HandlerBundle handlerBundle1 = null;
            loop: for (Map.Entry<String[], DynamicHandlerBundle> i : map1.entrySet()) {
                if (i.getKey().length == key.length) {
                    for (int j = 0; j < key.length; j++) {
                        if (i.getKey()[j] != null && !i.getKey()[j].equals(key[j])) {
                            continue loop;
                        }
                    }
                    handlerBundle1 = i.getValue();
                }
            }
            return handlerBundle1;
        }
        return null;
    }

    public void removeHandler(RequestHandler handler) {
        for (Map.Entry<HttpMethod, Map<String, HandlerBundle>> i : handlers.entrySet()) {
            for (Map.Entry<String, HandlerBundle> j : i.getValue().entrySet()) {
                if (j.getValue().getHandler().equals(handler)) {
                    handlers.get(i.getKey()).remove(j.getKey());
                }
            }
        }
    }

    public boolean hasPath(String path) {
        for (Map<String, HandlerBundle> i : handlers.values()) {
            if (i.containsKey(path)) {
                return true;
            }
        }
        return false;
    }

}
