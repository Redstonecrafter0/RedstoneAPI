package net.redstonecraft.redstoneapi.webserver;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.loader.FileLocator;
import net.redstonecraft.redstoneapi.core.http.HttpHeader;
import net.redstonecraft.redstoneapi.core.http.HttpResponseCode;
import net.redstonecraft.redstoneapi.info.RedstoneAPI;
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
    final List<Connection> connections = new CopyOnWriteArrayList<>();
    final HandlerManager handlerManager = new HandlerManager();
    private final boolean logging;
    final ErrorHandlerManager errorHandlerManager;
    final WebsocketManager websocketManager = new WebsocketManager();
    private final File staticDir;
    private final File templateDir;
    private final int port;
    private final long websocketMaxLength;
    private long lastKeepAlive = System.currentTimeMillis();
    long lastKeepAliveId = 0;
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

    public static String toServerTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(" yyyy HH:mm:ss z", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ", " + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)) + " " + new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}[calendar.get(Calendar.MONTH)] + dateFormat.format(date);
    }

    public static String getServerTime() {
        return toServerTime(new Date());
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
                    connection.close();
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
                    WebSocketConnection webSocketConnection = getWebsocketConnectionBySocketChannel((SocketChannel) key.channel());
                    if (webSocketConnection != null) {
                        if (!webSocketConnection.isHandled()) {
                            try {
                                webSocketConnection.markHandled();
                                webSocketConnection.handle(websocketManager, websocketMaxLength, threadPool);
                            } catch (Throwable ignored) {
                                webSocketConnection.disconnect();
                            }
                        }
                    } else {
                        Connection connection = getConnectionBySocketChannel((SocketChannel) key.channel());
                        if (connection == null) {
                            key.channel().close();
                            continue;
                        }
                        if (!connection.isProcessed()) {
                            connection.markProcessed();
                            threadPool.submit(new RequestProcessor(connection, this));
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        selector.selectedKeys().clear();
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

    void sendResponseAndClose(Connection conn, WebRequest request, WebResponse response) {
        try {
            sendResponse(conn.getChannel(), request, response);
        } catch (IOException ignored) {
        }
        try {
            conn.getChannel().close();
        } catch (IOException ignored) {
        }
        connections.remove(conn);
    }

    @SuppressWarnings("SameParameterValue")
    void sendResponseAndClose(Connection conn, WebRequest request, HttpResponseCode code, InputStream content, HttpHeader... headers) {
        try {
            sendResponse(conn.getChannel(), request, code, content, headers);
        } catch (IOException ignored) {
        }
        try {
            conn.getChannel().close();
        } catch (IOException ignored) {
        }
        connections.remove(conn);
    }

    void sendResponse(SocketChannel channel, WebRequest request, WebResponse response) throws IOException {
        sendResponse(channel, request, response.code(), response.content(), response.headers());
    }

    void sendResponse(SocketChannel channel, WebRequest request, HttpResponseCode code, InputStream content, HttpHeader... headers) throws IOException {
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
        if (!code.equals(HttpResponseCode.NO_CONTENT) || (request != null && request.getMethod().hasBodyResponse())) {
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
            logger.info("[" + channel.socket().getInetAddress().getHostAddress() + "] " + (request != null ? request.getMethod() : "UNKNOWN") + " " + (request != null ? request.getPath() : "null") + " " + code.getCode() + " " + code.getDescription() + " " + datalen);
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
        Parameter[] parameterTypes = i.getParameters();
        if (!Arrays.stream(parameterTypes).skip(1).allMatch(k -> k.getType().equals(String.class) && (k.isAnnotationPresent(QueryParam.class) || k.isAnnotationPresent(FormParam.class) || k.isAnnotationPresent(RouteParam.class) || k.isAnnotationPresent(HeaderParam.class)))) {
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
