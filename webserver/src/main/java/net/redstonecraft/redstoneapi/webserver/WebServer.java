package net.redstonecraft.redstoneapi.webserver;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.loader.FileLocator;
import net.redstonecraft.redstoneapi.info.RedstoneAPI;
import net.redstonecraft.redstoneapi.core.*;
import net.redstonecraft.redstoneapi.webserver.annotations.*;
import net.redstonecraft.redstoneapi.webserver.internal.*;
import net.redstonecraft.redstoneapi.webserver.internal.exceptions.NoRouteParamException;
import net.redstonecraft.redstoneapi.webserver.obj.*;
import net.redstonecraft.redstoneapi.webserver.ws.Websocket;
import net.redstonecraft.redstoneapi.webserver.ws.Websockets;
import net.redstonecraft.redstoneapi.webserver.ws.events.WebsocketBinaryDataEvent;
import net.redstonecraft.redstoneapi.webserver.ws.events.WebsocketConnectedEvent;
import net.redstonecraft.redstoneapi.webserver.ws.events.WebsocketDisconnectedEvent;
import net.redstonecraft.redstoneapi.webserver.ws.events.WebsocketMessageEvent;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A complete WebServer with jinja rendering and websockets.
 * If the template folder is not specified ./templates will be used else the subfolder templates will be used.
 * Static content is delivered on /static whose folder is the subfolder called static of the specified baseDir. Access is restriceted to the static folder and its subfolders.
 *
 * @author Redstonecrafter0
 * @since 1.2
 */
public class WebServer {

    public static final ErrorHandler DEFAULT_UNIVERSAL_ERROR_HANDLER = new ErrorHandler() {

        private String html = "";

        @Override
        public WebResponse handleError(HttpResponseCode code, String url, Map<String, String> args, HttpHeaders headers) {
            if (html.equals("")) {
                try {
                    html = new String(Objects.requireNonNull(getClass().getResourceAsStream("/webserver/error.html")).readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException | NullPointerException ignored) {
                }
            }
            return WebResponse.create().setContent(html.replace("{code}", String.valueOf(code.getCode()))
                    .replace("{desc}", code.getDescription())
                    .replace("{text}", code.getCode() < 500 ? "Ohh. There was an error. Try another page." : "Ohh. There was an error. Try again later.")).setResponseCode(code).build();
        }
    };
    private static final Logger logger = Logger.getLogger(WebServer.class.getName());

    static {
        logger.setUseParentHandlers(false);
        ConsoleHandler sh = new ConsoleHandler();
        sh.setFormatter(new Formatter() {
            private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            @Override
            public String format(LogRecord record) {
                return "[" + simpleDateFormat.format(new Date()) + "] [" + record.getLevel().getName() + "] | " + record.getMessage() + "\n";
            }
        });
        logger.addHandler(sh);
    }

    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final JinjavaConfig jinjavaConfig = new JinjavaConfig();
    private final Jinjava jinjava = new Jinjava(jinjavaConfig);
    private final Selector selector;
    private final ServerSocketChannel serverSocket;
    private final List<Connection> connections = new CopyOnWriteArrayList<>();
    private final HandlerManager handlerManager = new HandlerManager();
    private final boolean logging;
    private final ErrorHandlerManager errorHandlerManager;
    private final WebsocketManager websocketManager = new WebsocketManager();
    private final File staticDir;
    private final File templateDir;
    private final int port;
    private final long websocketMaxLength;
    private long lastKeepAlive = System.currentTimeMillis();
    private long lastKeepAliveId = 0;
    private boolean run = true;

    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public WebServer() throws IOException {
        this("localhost", 8080);
    }

    @SuppressWarnings("unused")
    public WebServer(int port) throws IOException {
        this("", port);
    }

    public WebServer(String host, int port) throws IOException {
        this(host, port, true, DEFAULT_UNIVERSAL_ERROR_HANDLER, ".");
    }

    public WebServer(String host, int port, boolean logging, ErrorHandler defaultErrorHandler, String baseDir) throws IOException {
        this(new InetSocketAddress(host, port), logging, defaultErrorHandler, baseDir, 4194304);
    }

    public WebServer(SocketAddress address, boolean logging, ErrorHandler defaultErrorHandler, String baseDir, long websocketMaxLength) throws IOException {
        this.logging = logging;
        this.websocketMaxLength = websocketMaxLength;
        this.port = address instanceof InetSocketAddress inetSocketAddress ? inetSocketAddress.getPort() : -1;
        errorHandlerManager = new ErrorHandlerManager(defaultErrorHandler);
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(address);
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, serverSocket.validOps(), null);
        staticDir = new File(baseDir);
        if (!staticDir.exists() || !staticDir.isDirectory()) {
            staticDir.mkdirs();
        }
        templateDir = new File(staticDir, "templates");
        if (!templateDir.exists() || !templateDir.isDirectory()) {
            templateDir.mkdirs();
        }
        jinjava.setResourceLocator(new FileLocator(templateDir));
        if (this.logging) {
            logger.info(this.port == -1 ? "WebServer listening on Unix Socket" : "WebServer listening on port " + this.port);
        }
        Thread thread = new Thread(() -> {
            while (run) {
                try {
                    tick();
                } catch (ClosedSelectorException ignored) {
                } catch (OutOfMemoryError ignored) {
                    System.gc();
                } catch (Throwable e) {
                    if (logging) {
                        logger.severe(e.getClass().getName());
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public Jinjava getJinjava() {
        return jinjava;
    }

    @SuppressWarnings("unused")
    public void stop() {
        run = false;
        try {
            serverSocket.close();
            selector.close();
        } catch (Throwable ignored) {
        }
        if (logging) {
            logger.info(port == -1 ? "WebServer on Unix Socket stopped" : "WebServer on port " + port + " stopped");
        }
    }

    private void tick() throws IOException {
        long time = System.currentTimeMillis();
        if (time >= lastKeepAlive + 20000) {
            lastKeepAliveId++;
            if (lastKeepAliveId > Long.MAX_VALUE - 5) {
                lastKeepAliveId = 0;
            }
            lastKeepAlive = time;
            long t = time - 60000;
            for (Connection connection : connections) {
                if (connection.getKeepAlive() < t) {
                    try {
                        connection.getChannel().close();
                    } catch (IOException ignored) {
                    }
                    connections.remove(connection);
                }
            }
            for (Map.Entry<WebSocketConnection, WebSocketPing> entry : websocketManager.entrySet()) {
                if (entry.getValue().getTime() < t) {
                    try {
                        entry.getKey().getChannel().close();
                    } catch (IOException ignored) {
                    }
                    websocketManager.unregisterConnection(entry.getKey());
                }
            }
            websocketManager.forEach((connection, timeout) -> {
                try {
                    connection.send((byte) 0b10001001, String.valueOf(lastKeepAliveId).getBytes(StandardCharsets.UTF_8));
                    timeout.setPayload(lastKeepAliveId);
                } catch (IOException ignored) {
                    websocketManager.unregisterConnection(connection);
                }
            });
        }
        time += 20000;
        time -= lastKeepAlive;
        selector.select(time);
        for (SelectionKey key : selector.selectedKeys()) {
            try {
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    SocketChannel channel = serverSocket.accept();
                    if (channel != null) {
                        channel.configureBlocking(false);
                        connections.add(new Connection(channel));
                        channel.register(selector, channel.validOps(), null);
                    }
                } else if (key.isReadable()) {
                    if (websocketManager.containsKey(getWebsocketConnectionBySocketChannel((SocketChannel) key.channel()))) {
                        WebSocketConnection webSocketConnection = Objects.requireNonNull(getWebsocketConnectionBySocketChannel((SocketChannel) key.channel()));
                        try {
                            webSocketConnection.handle(websocketManager, websocketMaxLength, threadPool);
                        } catch (Throwable ignored) {
                            webSocketConnection.disconnect();
                        }
                    } else {
                        try {
                            Connection connection = getConnectionBySocketChannel((SocketChannel) key.channel());
                            if (connection == null) {
                                try {
                                    key.channel().close();
                                } catch (IOException ignored) {
                                }
                                continue;
                            }
                            try {
                                ByteBuffer buffer = ByteBuffer.allocate(8192);
                                int len;
                                if ((len = connection.getChannel().read(buffer)) == -1) {
                                    try {
                                        connection.getChannel().close();
                                    } catch (IOException ignored) {
                                    }
                                    connections.remove(connection);
                                    continue;
                                }
                                threadPool.submit(() -> {
                                    try {
                                        if (!(new String(buffer.array(), StandardCharsets.UTF_8).contains("\r\n\r\n"))) {
                                            sendResponseAndClose(connection, null, "UNKNOWN", errorHandlerManager.handle(HttpResponseCode.REQUEST_HEADER_FIELDS_TOO_LARGE, "UNKNOWN", new HashMap<>(), new HttpHeaders(new ArrayList<>())));
                                            return;
                                        }
                                        Object parsed = WebRequest.parseRequest(connection, buffer, len, connections, this);
                                        if (parsed instanceof WebResponse errorResponse) {
                                            sendResponseAndClose(connection, null, "UNKNOWN", errorResponse);
                                        } else if (parsed instanceof WebRequest request) {
                                            try {
                                                try {
                                                    if (request.getMethod().equals(HttpMethod.GET) && request.getProtocol().equals("HTTP/1.1") &&
                                                            Objects.requireNonNull(request.getHeaders().get("Connection")).equals("Upgrade") &&
                                                            Objects.requireNonNull(request.getHeaders().get("Upgrade")).equals("websocket") &&
                                                            request.getHeaders().get("Sec-WebSocket-Version") != null &&
                                                            request.getHeaders().get("Sec-WebSocket-Key") != null) {
                                                        if (websocketManager.pathExists(request.getPath())) {
                                                            String wsKey = Objects.requireNonNull(request.getHeaders().get("Sec-WebSocket-Key"));
                                                            String accept = Base64.getEncoder().encodeToString(Hashlib.sha1_raw(wsKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"));
                                                            sendResponse(connection.getChannel(), HttpMethod.GET, request.getPath(), HttpResponseCode.SWITCHING_PROTOCOLS, new ByteArrayInputStream(new byte[0]), new HttpHeader("Upgrade", "websocket"), new HttpHeader("Connection", "Upgrade"), new HttpHeader("Sec-WebSocket-Accept", accept));
                                                            WebSocketConnection webSocketConnection = new WebSocketConnection(connection.getChannel(), this, request);
                                                            websocketManager.registerConnection(webSocketConnection, new WebSocketPing(System.currentTimeMillis(), lastKeepAliveId));
                                                            try {
                                                                websocketManager.executeConnectEvent(webSocketConnection);
                                                            } catch (Throwable e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            sendResponse(connection.getChannel(), HttpMethod.GET, request.getPath(), HttpResponseCode.NOT_FOUND, new ByteArrayInputStream(new byte[0]));
                                                        }
                                                        connections.remove(connection);
                                                        return;
                                                    }
                                                } catch (NullPointerException ignored) {
                                                }
                                                if (request.getMethod().equals(HttpMethod.GET) && request.getPath().startsWith("/static/")) {
                                                    File file = new File(staticDir.getPath(), request.getPath());
                                                    if (file.getCanonicalPath().startsWith(staticDir.getCanonicalPath())) {
                                                        try {
                                                            sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), HttpResponseCode.OK, new FileInputStream(file), new HttpHeader("Content-Type", MimeType.getByFilename(file.getCanonicalPath()).getMimetype()));
                                                        } catch (NoSuchFileException ignored) {
                                                            sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, request.getPath(), request.getArgs(), request.getHeaders()));
                                                        } catch (Throwable ignored) {
                                                            sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), errorHandlerManager.handle(HttpResponseCode.INTERNAL_SERVER_ERROR, request.getPath(), request.getArgs(), request.getHeaders()));
                                                        }
                                                    } else {
                                                        sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, request.getPath(), request.getArgs(), request.getHeaders()));
                                                    }
                                                    return;
                                                } else if (request.getMethod().equals(HttpMethod.GET) && request.getPath().equals("/favicon.ico")) {
                                                    File favicon = new File(new File(staticDir, "static"), "favicon.ico");
                                                    if (favicon.exists()) {
                                                        sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), WebResponse.create().setContent(new FileInputStream(favicon)).addHeader(new HttpHeader("Content-Type", MimeType.getByFilename(favicon.getName()).getMimetype())).build());
                                                    } else {
                                                        sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, request.getPath(), request.getArgs(), request.getHeaders()));
                                                    }
                                                    return;
                                                }
                                                if (!request.getMethod().equals(HttpMethod.OPTIONS)) {
                                                    try {
                                                        HandlerBundle handlerBundle;
                                                        if (request.getMethod().equals(HttpMethod.HEAD)) {
                                                            handlerBundle = handlerManager.getHandler(HttpMethod.HEAD, request.getPath());
                                                            if (handlerBundle == null) {
                                                                handlerBundle = handlerManager.getHandler(HttpMethod.GET, request.getPath());
                                                            }
                                                        } else {
                                                            handlerBundle = handlerManager.getHandler(request.getMethod(), request.getPath());
                                                        }
                                                        Object response;
                                                        if (handlerBundle != null) {
                                                            try {
                                                                response = handlerBundle.invoke(request);
                                                            } catch (Throwable e) {
                                                                sendResponseAndClose(connection, request.getMethod(), request.getPath(), errorHandlerManager.handle(HttpResponseCode.INTERNAL_SERVER_ERROR, request.getPath(), request.getArgs(), request.getHeaders()));
                                                                return;
                                                            }
                                                        } else {
                                                            if (handlerManager.hasPath(request.getPath())) {
                                                                sendResponseAndClose(connection, request.getMethod(), request.getPath(), errorHandlerManager.handle(HttpResponseCode.METHOD_NOT_ALLOWED, request.getPath(), request.getArgs(), request.getHeaders()));
                                                            } else {
                                                                sendResponseAndClose(connection, request.getMethod(), request.getPath(), errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, request.getPath(), request.getArgs(), request.getHeaders()));
                                                            }
                                                            return;
                                                        }
                                                        sendResponseAndClose(connection, request.getMethod(), request.getPath(), convertToResponse(request, response));
                                                    } catch (Throwable ignored) {
                                                    }
                                                } else {
                                                    if (handlerManager.getHandler(HttpMethod.OPTIONS, request.getPath()) != null) {
                                                        sendResponseAndClose(connection, HttpMethod.OPTIONS, request.getPath(), convertToResponse(request, handlerManager.getHandler(HttpMethod.OPTIONS, request.getPath()).invoke(request)));
                                                    } else if (request.getPath().equals("*")) {
                                                        List<String> list = new ArrayList<>();
                                                        for (HttpMethod i : handlerManager.getUsedMethods()) {
                                                            list.add(i.name());
                                                        }
                                                        sendResponseAndClose(connection, HttpMethod.OPTIONS, request.getPath(), WebResponse.create().setResponseCode(HttpResponseCode.NO_CONTENT).addHeader(new HttpHeader("Allow", String.join(", ", list))).build());
                                                    } else {
                                                        Set<String> list = new HashSet<>();
                                                        list.add("OPTIONS");
                                                        for (HttpMethod i : HttpMethod.values()) {
                                                            if (handlerManager.getHandler(i, request.getPath()) != null) {
                                                                list.add(i.name());
                                                            }
                                                        }
                                                        if (list.contains("GET")) {
                                                            try {
                                                                list.add("HEAD");
                                                            } catch (IllegalStateException ignored) {
                                                            }
                                                        }
                                                        sendResponseAndClose(connection, HttpMethod.OPTIONS, request.getPath(), WebResponse.create().setResponseCode(HttpResponseCode.NO_CONTENT).addHeader(new HttpHeader("Allow", String.join(", ", list))).build());
                                                    }
                                                }
                                            } catch (Throwable ignored) {
                                                try {
                                                    connection.getChannel().close();
                                                } catch (IOException ignored1) {
                                                }
                                                connections.remove(connection);
                                            }
                                        }
                                    } catch (IOException | NumberFormatException ignored) {
                                        try {
                                            connection.getChannel().close();
                                        } catch (IOException ignored1) {
                                        }
                                        connections.remove(connection);
                                    }
                                });
                            } catch (IndexOutOfBoundsException | IOException ignored) {
                                try {
                                    connection.getChannel().close();
                                } catch (IOException ignored1) {
                                }
                                connections.remove(connection);
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        selector.selectedKeys().clear();
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
            default -> errorHandlerManager.handle(HttpResponseCode.INTERNAL_SERVER_ERROR, request.getPath(), request.getArgs(), request.getHeaders());
        };
    }

    void disconnectWebsocket(WebSocketConnection webSocketConnection) {
        websocketManager.executeDisconnectEvent(webSocketConnection);
        try {
            webSocketConnection.send((byte) 0x8, new byte[]{0x03, (byte) 0xe8});
        } catch (IOException ignored) {
        }
        try {
            webSocketConnection.getChannel().close();
        } catch (IOException ignored) {
        }
        websocketManager.unregisterConnection(webSocketConnection);
    }

    @SuppressWarnings("unused")
    public File getStaticDir() {
        return new File(staticDir, "static");
    }

    public File getTemplateDir() {
        return templateDir;
    }

    public void broadcastWebsocket(String path, String message) {
        websocketManager.broadcast(path, message);
    }

    public void broadcastWebsocket(String path, byte[] payload) {
        websocketManager.broadcast(path, payload);
    }

    public void broadcastWebsocket(String path, String room, String message) {
        websocketManager.broadcast(path, room, message);
    }

    public void broadcastWebsocket(String path, String room, byte[] payload) {
        websocketManager.broadcast(path, room, payload);
    }

    private void sendResponseAndClose(Connection conn, HttpMethod method, String path, WebResponse response) {
        try {
            sendResponse(conn.getChannel(), method, path, response);
        } catch (IOException ignored) {
        }
        try {
            conn.getChannel().close();
        } catch (IOException ignored) {
        }
        connections.remove(conn);
    }

    @SuppressWarnings("SameParameterValue")
    private void sendResponseAndClose(Connection conn, HttpMethod method, String path, HttpResponseCode code, InputStream content, HttpHeader... headers) {
        try {
            sendResponse(conn.getChannel(), method, path, code, content, headers);
        } catch (IOException ignored) {
        }
        try {
            conn.getChannel().close();
        } catch (IOException ignored) {
        }
        connections.remove(conn);
    }

    private void sendResponse(SocketChannel channel, HttpMethod method, String path, WebResponse response) throws IOException {
        sendResponse(channel, method, path, response.code(), response.content(), response.headers());
    }

    public static String toServerTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(" yyyy HH:mm:ss z", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //noinspection deprecation
        return new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}[date.getDay()] + ", " + String.format("%02d", date.getDate()) + " " + new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}[date.getMonth()] + dateFormat.format(date);
    }

    public static String getServerTime() {
        return toServerTime(new Date());
    }

    private void sendResponse(SocketChannel channel, HttpMethod method, String path, HttpResponseCode code, InputStream content, HttpHeader... headers) throws IOException {
        List<String> list = new ArrayList<>();
        list.add("Content-Length: " + (code.equals(HttpResponseCode.NO_CONTENT) ? 0 : content.available()));
        for (HttpHeader i : headers) {
            list.add(i.key() + ": " + i.value());
        }
        list.add("Server: RedstoneAPI/" + RedstoneAPI.getVersion().toString());
        list.add("Date: " + getServerTime());
        if (!list.contains("Connection: Upgrade")) {
            list.add("Connection: close");
        }
        byte[] resp = ("HTTP/1.1 " + code.getCode() + " " + code.getDescription() + "\r\n" + String.join("\r\n", list) + "\r\n\r\n").getBytes(StandardCharsets.UTF_8);
        channel.write(ByteBuffer.wrap(resp));
        int datalen = resp.length;
        if (!code.equals(HttpResponseCode.NO_CONTENT) || (method != null && method.hasBodyResponse())) {
            while (content.available() > 0) {
                byte[] chunk = new byte[Math.min(1024, content.available())];
                //noinspection ResultOfMethodCallIgnored
                content.read(chunk);
                channel.write(ByteBuffer.wrap(chunk));
                datalen += chunk.length;
            }
        }
        if (content != null) {
            content.close();
        }
        if (logging) {
            logger.info("[" + channel.socket().getInetAddress().getHostAddress() + "] " + (method != null ? method : "UNKNOWN") + " " + path + " " + code.getCode() + " " + code.getDescription() + " " + datalen);
        }
    }

    private WebSocketConnection getWebsocketConnectionBySocketChannel(SocketChannel channel) {
        for (WebSocketConnection i : websocketManager.keySet()) {
            if (i.getChannel().equals(channel)) {
                return i;
            }
        }
        return null;
    }

    private Connection getConnectionBySocketChannel(SocketChannel channel) {
        for (Connection i : connections) {
            if (i.getChannel().equals(channel)) {
                return i;
            }
        }
        return null;
    }

    public void addHandler(RequestHandler handler) {
        handler.setWebServer(this);
        for (Method i : handler.getClass().getMethods()) {
            if (i.isAnnotationPresent(Routes.class) && !Modifier.isStatic(i.getModifiers()) && i.getParameters().length >= 1) {
                for (Route j : i.getAnnotation(Routes.class).value()) {
                    internalRegisterHandler(handler, i, j);
                }
            } else if (i.isAnnotationPresent(Route.class) && !Modifier.isStatic(i.getModifiers()) && i.getParameters().length >= 1) {
                Route j = i.getAnnotation(Route.class);
                internalRegisterHandler(handler, i, j);
            } else if (i.isAnnotationPresent(Websockets.class) && !Modifier.isStatic(i.getModifiers())) {
                for (Websocket j : i.getAnnotation(Websockets.class).value()) {
                    if (i.getParameterTypes().length == 1 && j.value().startsWith("/")) {
                        if (Arrays.asList(WebsocketMessageEvent.class, WebsocketBinaryDataEvent.class, WebsocketConnectedEvent.class, WebsocketDisconnectedEvent.class).contains(i.getParameterTypes()[0])) {
                            websocketManager.addHandler(j.value(), i.getParameterTypes()[0], new WebSocketBundle(handler, i));
                        }
                    }
                }
            } else if (i.isAnnotationPresent(Websocket.class) && !Modifier.isStatic(i.getModifiers())) {
                if (i.getParameterTypes().length == 1 && i.getAnnotation(Websocket.class).value().startsWith("/")) {
                    if (Arrays.asList(WebsocketMessageEvent.class, WebsocketBinaryDataEvent.class, WebsocketConnectedEvent.class, WebsocketDisconnectedEvent.class).contains(i.getParameterTypes()[0])) {
                        websocketManager.addHandler(i.getAnnotation(Websocket.class).value(), i.getParameterTypes()[0], new WebSocketBundle(handler, i));
                    }
                }
            }
        }
    }

    private void internalRegisterHandler(RequestHandler handler, Method i, Route j) {
        if (j.value().startsWith("/")) {
            Parameter[] parameterTypes = i.getParameters();
            if (!Arrays.stream(parameterTypes).skip(1).allMatch(k -> k.getType().equals(String.class) && (k.isAnnotationPresent(QueryParam.class) || k.isAnnotationPresent(FormParam.class) || k.isAnnotationPresent(RouteParam.class)))) {
                return;
            }
            if (WebRequest.class.equals(parameterTypes[0].getType())) {
                for (HttpMethod k : HttpMethod.getFromMethod(i)) {
                    try {
                        handlerManager.setHandler(k, j.value(), new DynamicHandlerBundle(handler, i, j.value()));
                    } catch (NoRouteParamException ignored) {
                        handlerManager.setHandler(k, j.value(), new HandlerBundle(handler, i));
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        }
    }

    public void removeHandler(HttpMethod method, String path) {
        try {
            handlerManager.setHandler(method, path, null);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @SuppressWarnings("unused")
    public void removeHandlers(String path) {
        for (HttpMethod i : HttpMethod.values()) {
            removeHandler(i, path);
        }
    }

    @SuppressWarnings("unused")
    public void removeHandler(RequestHandler handler) {
        handlerManager.removeHandler(handler);
        websocketManager.removeHandler(handler);
    }

    @SuppressWarnings("unused")
    public void setErrorHandler(HttpResponseCode code, ErrorHandler errorHandler) {
        errorHandlerManager.setHandler(code, errorHandler);
    }

    @SuppressWarnings("unused")
    public void removeErrorHandler(HttpResponseCode code) {
        errorHandlerManager.removeHandler(code);
    }

}
