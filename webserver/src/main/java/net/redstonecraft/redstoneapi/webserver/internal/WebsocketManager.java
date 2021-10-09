package net.redstonecraft.redstoneapi.webserver.internal;

import net.redstonecraft.redstoneapi.webserver.RequestHandler;
import net.redstonecraft.redstoneapi.webserver.WebSocketConnection;
import net.redstonecraft.redstoneapi.webserver.obj.WebSocketBundle;
import net.redstonecraft.redstoneapi.webserver.ws.events.WebsocketBinaryDataEvent;
import net.redstonecraft.redstoneapi.webserver.ws.events.WebsocketConnectedEvent;
import net.redstonecraft.redstoneapi.webserver.ws.events.WebsocketDisconnectedEvent;
import net.redstonecraft.redstoneapi.webserver.ws.events.WebsocketMessageEvent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class WebsocketManager {

    private final Map<WebSocketConnection, WebSocketPing> webSocketConnections = new ConcurrentHashMap<>();
    private final Map<String, Map<Class<?>, List<WebSocketBundle>>> endpoints = new HashMap<>();

    public void addHandler(String path, Class<?> event, WebSocketBundle bundle) {
        if (!endpoints.containsKey(path)) {
            endpoints.put(path, new HashMap<>());
        }
        if (!endpoints.get(path).containsKey(event)) {
            endpoints.get(path).put(event, new ArrayList<>());
        }
        endpoints.get(path).get(event).add(bundle);
    }

    public void removeHandler(RequestHandler handler) {
        endpoints.forEach((path, eventHandler) -> {
            eventHandler.forEach((eventType, list) -> {
                List<WebSocketBundle> list1 = new LinkedList<>();
                list.forEach(e -> {
                    if (e.getHandler().equals(handler)) {
                        list1.add(e);
                    }
                });
                list.removeIf(list1::contains);
                if (list.size() == 0) {
                    eventHandler.remove(eventType);
                }
            });
            if (eventHandler.size() == 0) {
                endpoints.remove(path);
            }
        });
    }

    public void registerConnection(WebSocketConnection webSocketConnection, WebSocketPing ping) {
        webSocketConnections.put(webSocketConnection, ping);
    }

    public void unregisterConnection(WebSocketConnection webSocketConnection) {
        webSocketConnections.remove(webSocketConnection);
    }

    public WebSocketPing getPing(WebSocketConnection webSocketConnection) {
        return webSocketConnections.get(webSocketConnection);
    }

    public Set<Map.Entry<WebSocketConnection, WebSocketPing>> entrySet() {
        return webSocketConnections.entrySet();
    }

    public void forEach(BiConsumer<? super WebSocketConnection, ? super WebSocketPing> consumer) {
        webSocketConnections.forEach(consumer);
    }

    public boolean containsKey(WebSocketConnection webSocketConnection) {
        if (webSocketConnection == null) {
            return false;
        }
        return webSocketConnections.containsKey(webSocketConnection);
    }

    public Set<WebSocketConnection> keySet() {
        return webSocketConnections.keySet();
    }

    public void broadcast(String path, String message) {
        webSocketConnections.entrySet().stream().filter(e -> e.getKey().getRequest().getPath().equals(path)).forEach(e -> {
            try {
                e.getKey().send(message);
            } catch (IOException ignored) {
            }
        });
    }

    public void broadcast(String path, byte[] payload) {
        webSocketConnections.entrySet().stream().filter(e -> e.getKey().getRequest().getPath().equals(path)).forEach(e -> {
            try {
                e.getKey().send(payload);
            } catch (IOException ignored) {
            }
        });
    }

    public void broadcast(String path, String room, String message) {
        webSocketConnections.entrySet().stream().filter(e -> e.getKey().getRequest().getPath().equals(path) && e.getKey().getRoom().equals(room)).forEach(e -> {
            try {
                e.getKey().send(message);
            } catch (IOException ignored) {
            }
        });
    }

    public void broadcast(String path, String room, byte[] payload) {
        webSocketConnections.entrySet().stream().filter(e -> e.getKey().getRequest().getPath().equals(path) && e.getKey().getRoom().equals(room)).forEach(e -> {
            try {
                e.getKey().send(payload);
            } catch (IOException ignored) {
            }
        });
    }

    public boolean pathExists(String path) {
        return endpoints.containsKey(path);
    }

    public void executeConnectEvent(WebSocketConnection connection) {
        try {
            endpoints.get(connection.getRequest().getPath()).get(WebsocketConnectedEvent.class).forEach(bundle -> {
                bundle.getMethod().setAccessible(true);
                try {
                    bundle.getMethod().invoke(bundle.getHandler(), new WebsocketConnectedEvent(connection));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        } catch (NullPointerException ignored) {
        }
    }

    public void executeDisconnectEvent(WebSocketConnection connection) {
        try {
            endpoints.get(connection.getRequest().getPath()).get(WebsocketDisconnectedEvent.class).forEach(bundle -> {
                bundle.getMethod().setAccessible(true);
                try {
                    bundle.getMethod().invoke(bundle.getHandler(), new WebsocketDisconnectedEvent(connection));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        } catch (NullPointerException ignored) {
        }
    }

    public void executeMessageEvent(WebSocketConnection connection, String message) {
        try {
            endpoints.get(connection.getRequest().getPath()).get(WebsocketMessageEvent.class).forEach(bundle -> {
                bundle.getMethod().setAccessible(true);
                try {
                    bundle.getMethod().invoke(bundle.getHandler(), new WebsocketMessageEvent(connection, message));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        } catch (NullPointerException ignored) {
        }
    }

    public void executeBinaryEvent(WebSocketConnection connection, byte[] payload) {
        try {
            endpoints.get(connection.getRequest().getPath()).get(WebsocketBinaryDataEvent.class).forEach(bundle -> {
                bundle.getMethod().setAccessible(true);
                try {
                    bundle.getMethod().invoke(bundle.getHandler(), new WebsocketBinaryDataEvent(connection, payload));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        } catch (NullPointerException ignored) {
        }
    }

}
