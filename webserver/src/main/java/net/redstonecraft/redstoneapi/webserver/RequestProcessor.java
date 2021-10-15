package net.redstonecraft.redstoneapi.webserver;

import net.redstonecraft.redstoneapi.core.*;
import net.redstonecraft.redstoneapi.webserver.internal.*;
import net.redstonecraft.redstoneapi.webserver.obj.HandlerBundle;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
record RequestProcessor(Connection connection, WebServer webServer) implements Runnable {

    @Override
    public void run() {
        try {
            Object parsed = WebRequest.parseRequest(connection.getInputStream(), webServer);
            switch (parsed) {
                case WebResponse errorResponse -> webServer.sendResponseAndClose(connection, null, "UNKNOWN", errorResponse);
                case WebRequest request -> {
                    try {
                        if (request.getMethod().equals(HttpMethod.GET) &&
                                request.getHeaders().getOrDefault("Connection", "").equalsIgnoreCase("Upgrade") &&
                                request.getHeaders().getOrDefault("Upgrade", "").equalsIgnoreCase("websocket") &&
                                request.getHeaders().get("Sec-WebSocket-Version") != null &&
                                request.getHeaders().get("Sec-WebSocket-Key") != null) {
                            if (webServer.websocketManager.pathExists(request.getPath())) {
                                String wsKey = Objects.requireNonNull(request.getHeaders().get("Sec-WebSocket-Key"));
                                String accept = Base64.getEncoder().encodeToString(Hashlib.sha1_raw(wsKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"));
                                webServer.sendResponse(connection.getChannel(), HttpMethod.GET, request.getPath(), HttpResponseCode.SWITCHING_PROTOCOLS, new ByteArrayInputStream(new byte[0]), new HttpHeader("Upgrade", "websocket"), new HttpHeader("Connection", "Upgrade"), new HttpHeader("Sec-WebSocket-Accept", accept));
                                WebSocketConnection webSocketConnection = new WebSocketConnection(connection.getChannel(), connection.getInputStream(), webServer, request);
                                webServer.websocketManager.registerConnection(webSocketConnection, new WebSocketPing(System.currentTimeMillis(), webServer.lastKeepAliveId));
                                try {
                                    webServer.websocketManager.executeConnectEvent(webSocketConnection);
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            } else {
                                webServer.sendResponse(connection.getChannel(), HttpMethod.GET, request.getPath(), HttpResponseCode.NOT_FOUND, new ByteArrayInputStream(new byte[0]));
                            }
                            webServer.connections.remove(connection);
                            return;
                        }
                    } catch (NullPointerException ignored) {
                    }
                    if (request.getMethod().equals(HttpMethod.GET) && request.getPath().startsWith("/static/")) {
                        File file = new File(webServer.getStaticDir().getParent(), request.getPath());
                        if (file.getCanonicalPath().startsWith(webServer.getStaticDir().getCanonicalPath())) {
                            try {
                                webServer.sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), HttpResponseCode.OK, new FileInputStream(file), new HttpHeader("Content-Type", MimeType.getByFilename(file.getCanonicalPath()).getMimetype()));
                            } catch (FileNotFoundException ignored) {
                                webServer.sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), webServer.errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, request.getPath(), request.getArgs(), request.getHeaders()));
                            } catch (Throwable ignored) {
                                webServer.sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), webServer.errorHandlerManager.handle(HttpResponseCode.INTERNAL_SERVER_ERROR, request.getPath(), request.getArgs(), request.getHeaders()));
                            }
                        } else {
                            webServer.sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), webServer.errorHandlerManager.handle(HttpResponseCode.BAD_REQUEST, request.getPath(), request.getArgs(), request.getHeaders()));
                        }
                        return;
                    } else if (request.getMethod().equals(HttpMethod.GET) && request.getPath().equals("/favicon.ico")) {
                        File favicon = new File(webServer.getStaticDir().getParent(), "favicon.ico");
                        if (favicon.exists()) {
                            webServer.sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), WebResponse.create().setContent(new FileInputStream(favicon)).addHeader(new HttpHeader("Content-Type", MimeType.getByFilename(favicon.getName()).getMimetype())).build());
                        } else {
                            webServer.sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), webServer.errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, request.getPath(), request.getArgs(), request.getHeaders()));
                        }
                        return;
                    } else if (request.getMethod().equals(HttpMethod.GET) && request.getPath().equals("/robots.txt")) {
                        File robots = new File(webServer.getStaticDir().getParent(), "robots.txt");
                        if (robots.exists()) {
                            webServer.sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), WebResponse.create().setContent(new FileInputStream(robots)).addHeader(new HttpHeader("Content-Type", MimeType.getByFilename(robots.getName()).getMimetype())).build());
                        } else {
                            webServer.sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), webServer.errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, request.getPath(), request.getArgs(), request.getHeaders()));
                        }
                        return;
                    }
                    if (!request.getMethod().equals(HttpMethod.OPTIONS)) {
                        HandlerBundle handlerBundle;
                        if (request.getMethod().equals(HttpMethod.HEAD)) {
                            handlerBundle = webServer.handlerManager.getHandler(HttpMethod.HEAD, request.getPath());
                            if (handlerBundle == null) {
                                handlerBundle = webServer.handlerManager.getHandler(HttpMethod.GET, request.getPath());
                            }
                        } else {
                            handlerBundle = webServer.handlerManager.getHandler(request.getMethod(), request.getPath());
                        }
                        Object response;
                        if (handlerBundle != null) {
                            try {
                                response = handlerBundle.invoke(request);
                            } catch (Throwable e) {
                                webServer.sendResponseAndClose(connection, request.getMethod(), request.getPath(), webServer.errorHandlerManager.handle(HttpResponseCode.INTERNAL_SERVER_ERROR, request.getPath(), request.getArgs(), request.getHeaders()));
                                return;
                            }
                        } else {
                            if (webServer.handlerManager.hasPath(request.getPath())) {
                                webServer.sendResponseAndClose(connection, request.getMethod(), request.getPath(), webServer.errorHandlerManager.handle(HttpResponseCode.METHOD_NOT_ALLOWED, request.getPath(), request.getArgs(), request.getHeaders()));
                            } else {
                                webServer.sendResponseAndClose(connection, request.getMethod(), request.getPath(), webServer.errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, request.getPath(), request.getArgs(), request.getHeaders()));
                            }
                            return;
                        }
                        webServer.sendResponseAndClose(connection, request.getMethod(), request.getPath(), convertToResponse(request, response));
                    } else {
                        if (webServer.handlerManager.getHandler(HttpMethod.OPTIONS, request.getPath()) != null) {
                            webServer.sendResponseAndClose(connection, HttpMethod.OPTIONS, request.getPath(), convertToResponse(request, webServer.handlerManager.getHandler(HttpMethod.OPTIONS, request.getPath()).invoke(request)));
                        } else if (request.getPath().equals("*")) {
                            List<String> list = new ArrayList<>();
                            for (HttpMethod i : webServer.handlerManager.getUsedMethods()) {
                                list.add(i.name());
                            }
                            webServer.sendResponseAndClose(connection, HttpMethod.OPTIONS, request.getPath(), WebResponse.create().setResponseCode(HttpResponseCode.NO_CONTENT).addHeader(new HttpHeader("Allow", String.join(", ", list))).build());
                        } else {
                            Set<String> list = new HashSet<>();
                            list.add("OPTIONS");
                            for (HttpMethod i : HttpMethod.values()) {
                                if (webServer.handlerManager.getHandler(i, request.getPath()) != null) {
                                    list.add(i.name());
                                }
                            }
                            if (list.contains("GET")) {
                                try {
                                    list.add("HEAD");
                                } catch (IllegalStateException ignored) {
                                }
                            }
                            webServer.sendResponseAndClose(connection, HttpMethod.OPTIONS, request.getPath(), WebResponse.create().setResponseCode(HttpResponseCode.NO_CONTENT).addHeader(new HttpHeader("Allow", String.join(", ", list))).build());
                        }
                    }
                }
                default -> {
                    connection.close();
                    webServer.connections.remove(connection);
                }
            }
        } catch (Throwable ignored) {
            connection.close();
            webServer.connections.remove(connection);
        }
    }

    private WebResponse convertToResponse(WebRequest request, Object response) {
        return switch (response) {
            case WebResponse r -> r;
            case WebResponse.Builder b -> b.build();
            case String s -> WebResponse.create().setContent(s).build();
            case byte[] d -> WebResponse.create().setContent(d).build();
            case ByteBuffer b -> WebResponse.create().setContent(b).build();
            case InputStream s -> WebResponse.create().setContent(s).build();
            case null -> WebResponse.create().setContent("null").build();
            case Throwable throwable -> WebResponse.create().setContent(StringUtils.stringFromError(throwable)).build();
            default -> webServer.errorHandlerManager.handle(HttpResponseCode.INTERNAL_SERVER_ERROR, request.getPath(), request.getArgs(), request.getHeaders());
        };
    }

}
