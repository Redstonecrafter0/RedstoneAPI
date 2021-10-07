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
import net.redstonecraft.redstoneapi.webserver.websocket.WebsocketEvent;
import net.redstonecraft.redstoneapi.webserver.websocket.WebsocketEvents;
import net.redstonecraft.redstoneapi.webserver.websocket.events.WebsocketBinaryDataEvent;
import net.redstonecraft.redstoneapi.webserver.websocket.events.WebsocketConnectedEvent;
import net.redstonecraft.redstoneapi.webserver.websocket.events.WebsocketDisconnectedEvent;
import net.redstonecraft.redstoneapi.webserver.websocket.events.WebsocketMessageEvent;
import org.apache.commons.lang.NotImplementedException;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.*;
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
            return new WebResponse(html.replace("{code}", String.valueOf(code.getCode()))
                    .replace("{desc}", code.getDescription())
                    .replace("{text}", code.getCode() < 500 ? "Ohh. There was an error. Try another page." : "Ohh. There was an error. Try again later."), code);
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
    private final List<Connection> connections = new LinkedList<>();
    private final HandlerManager handlerManager = new HandlerManager();
    private final boolean logging;
    private final ErrorHandlerManager errorHandlerManager;
    private final WebsocketManager websocketManager = new WebsocketManager();
    private final File staticDir;
    private final File templateDir;
    private final int port;
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
        this.logging = logging;
        this.port = port;
        errorHandlerManager = new ErrorHandlerManager(defaultErrorHandler);
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(host, port));
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
            logger.info("WebServer listening on port " + this.port);
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
            logger.info("WebServer on port " + port + " stopped");
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
            List<Connection> remove = new LinkedList<>();
            connections.stream().filter(connection -> connection.getKeepAlive() < t).forEach(connection -> {
                try {
                    connection.getChannel().close();
                } catch (IOException ignored) {
                }
                remove.add(connection);
            });
            connections.removeIf(remove::contains);
            List<WebSocketConnection> remove1 = new LinkedList<>();
            websocketManager.entrySet().stream().filter(entry -> entry.getValue().getTime() < t).forEach(entry -> {
                try {
                    entry.getKey().getChannel().close();
                } catch (IOException ignored) {
                }
                remove1.add(entry.getKey());
            });
            remove1.forEach(websocketManager::unregisterConnection);
            websocketManager.forEach((connection, timeout) -> {
                try {
                    connection.send((byte) 0b10001001, String.valueOf(lastKeepAliveId).getBytes(StandardCharsets.UTF_8));
                    timeout.setPayload(lastKeepAliveId);
                } catch (IOException ignored) {
                    connection.disconnect();
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
                        ByteBuffer buffer = ByteBuffer.allocate(65544);
                        try {
                            webSocketConnection.getChannel().read(buffer);
                        } catch (IOException ignored) {
                            webSocketConnection.disconnect();
                            continue;
                        }
                        threadPool.submit(() -> {
                            byte[] arr = buffer.array();
                            try {
                                byte actionRaw = arr[0];
                                boolean fin = getBitByByte(actionRaw, 0);
                                actionRaw <<= 4;
                                actionRaw >>= 4;
                                int action = actionRaw & 0xf;
                                byte rawLength = arr[1];
                                boolean mask = getBitByByte(rawLength, 0);
                                rawLength <<= 1;
                                rawLength >>= 1;
                                int orgLength = Byte.toUnsignedInt(rawLength);
                                int length;
                                int pos;
                                if (!fin || !mask) {
                                    webSocketConnection.disconnect();
                                    return;
                                }
                                if (orgLength <= 125) {
                                    length = orgLength;
                                    pos = 2;
                                } else if (orgLength == 126) {
                                    length = getIntByBytes(new byte[]{arr[2], arr[3]});
                                    pos = 4;
                                } else {
                                    webSocketConnection.disconnect();
                                    return;
                                }
                                byte[] maskKey = new byte[4];
                                System.arraycopy(arr, pos, maskKey, 0, maskKey.length);
                                byte[] encoded = new byte[length];
                                for (int i = 0; i < length; i++) {
                                    encoded[i] = arr[i + pos + 4];
                                }
                                byte[] decoded = new byte[length];
                                for (int i = 0; i < encoded.length; i++) {
                                    decoded[i] = (byte) (encoded[i] ^ maskKey[i % 4]);
                                }
                                if (action == 0xA) {
                                    String id = new String(decoded, StandardCharsets.UTF_8);
                                    if (id.equals(String.valueOf(websocketManager.getPing(webSocketConnection).getPayload()))) {
                                        websocketManager.getPing(webSocketConnection).setTime(System.currentTimeMillis());
                                    }
                                } else if (action == 0x1) {
                                    String payload = new String(decoded, StandardCharsets.UTF_8);
                                    websocketManager.getPing(webSocketConnection).setTime(System.currentTimeMillis());
                                    websocketManager.executeMessageEvent(webSocketConnection, payload);
                                } else if (action == 0x9) {
                                    webSocketConnection.send(decoded);
                                } else if (action == 0x2) {
                                    websocketManager.getPing(webSocketConnection).setTime(System.currentTimeMillis());
                                    websocketManager.executeBinaryEvent(webSocketConnection, decoded);
                                } else {
                                    webSocketConnection.disconnect();
                                }
                            } catch (Exception ignored) {
                                webSocketConnection.disconnect();
                            }
                        });
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
                                                        throw new NotImplementedException();
                                                        // TODO: reimplement socket connection
    //                                                    if (websocketManager.pathExists(webRequest.getPath())) {
    //                                                        String wsKey = Objects.requireNonNull(HttpHeader.getByKey(headers, "Sec-WebSocket-Key")).getValue();
    //                                                        String accept = Base64.getEncoder().encodeToString(Hashlib.sha1_raw(wsKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"));
    //                                                        sendResponse(connection.channel, "GET", path, HttpResponseCode.SWITCHING_PROTOCOLS, new byte[0], new HttpHeader("Upgrade", "websocket"), new HttpHeader("Connection", "Upgrade"), new HttpHeader("Sec-WebSocket-Accept", accept));
    //                                                        WebSocketConnection webSocketConnection = new WebSocketConnection(connection.channel, this, path);
    //                                                        websocketManager.registerConnection(webSocketConnection, new WebSocketPing(System.currentTimeMillis(), lastKeepAliveId));
    //                                                        websocketManager.executeConnectEvent(webSocketConnection);
    //                                                    } else {
    //                                                        sendResponse(connection.channel, "GET", webRequest.getPath(), HttpResponseCode.NOT_FOUND, new byte[0]);
    //                                                    }
    //                                                    connections.remove(connection);
    //                                                    return;
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
                                                        sendResponseAndClose(connection, HttpMethod.GET, request.getPath(), new WebResponse(Files.readAllBytes(favicon.toPath()), HttpResponseCode.OK, new HttpHeader("Content-Type", MimeType.getByFilename(favicon.getName()).getMimetype())));
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
                                                        sendResponseAndClose(connection, request.getMethod(), request.getPath(), switch (response) {
                                                            case WebResponse r -> r;
                                                            case String s -> new WebResponse(s);
                                                            case byte[] d -> new WebResponse(d);
                                                            case ByteBuffer b -> new WebResponse(b.array());
                                                            case InputStream s -> new WebResponse(s);
                                                            case null -> new WebResponse("null");
                                                            case Throwable throwable -> new WebResponse(StringUtils.stringFromError(throwable));
                                                            default -> errorHandlerManager.handle(HttpResponseCode.INTERNAL_SERVER_ERROR, request.getPath(), request.getArgs(), request.getHeaders());
                                                        });
                                                    } catch (Throwable ignored) {
                                                    }
                                                } else {
                                                    if (handlerManager.getHandler(HttpMethod.OPTIONS, request.getPath()) != null) {
                                                        //noinspection ConstantConditions
                                                        sendResponseAndClose(connection, HttpMethod.OPTIONS, request.getPath(), (WebResponse) handlerManager.getHandler(HttpMethod.OPTIONS, request.getPath()).invoke(request));
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
                                                        sendResponseAndClose(connection, HttpMethod.OPTIONS, request.getPath(), new WebResponse(new byte[0], HttpResponseCode.NO_CONTENT, new HttpHeader("Allow", String.join(", ", list))));
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

    private int getIntByBytes(byte[] arr) {
        int c = 0;
        for (byte i : arr) {
            c |= Byte.toUnsignedInt(i);
            c <<= 8;
        }
        return c;
    }

    @SuppressWarnings("SameParameterValue")
    private boolean getBitByByte(byte b, int pos) {
        return (b >> (8 - (pos + 1)) & 0x0001) == 1;
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
            sendResponse(conn.getChannel(), method, path, code, content, Arrays.asList(headers));
        } catch (IOException ignored) {
        }
        try {
            conn.getChannel().close();
        } catch (IOException ignored) {
        }
        connections.remove(conn);
    }

    private void sendResponse(SocketChannel channel, HttpMethod method, String path, WebResponse response) throws IOException {
        sendResponse(channel, method, path, response.getCode(), response.getContent(), response.getHeaders());
    }

    public static String getServerTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(" yyyy HH:mm:ss z", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //noinspection deprecation
        return new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}[date.getDay()] + ", " + String.format("%02d", date.getDate()) + " " + new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}[date.getMonth()] + dateFormat.format(date);
    }

    private void sendResponse(SocketChannel channel, HttpMethod method, String path, HttpResponseCode code, InputStream content, Collection<HttpHeader> headers) throws IOException {
        List<String> list = new ArrayList<>();
        list.add("Content-Length: " + (code.equals(HttpResponseCode.NO_CONTENT) ? 0 : content.available()));
        for (HttpHeader i : headers) {
            list.add(i.getKey() + ": " + i.getValue());
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
            } else if (i.isAnnotationPresent(WebsocketEvents.class) && !Modifier.isStatic(i.getModifiers())) {
                for (WebsocketEvent j : i.getAnnotation(WebsocketEvents.class).value()) {
                    if (i.getParameterTypes().length == 1 && j.path().startsWith("/")) {
                        if (Arrays.asList(WebsocketMessageEvent.class, WebsocketBinaryDataEvent.class, WebsocketConnectedEvent.class, WebsocketDisconnectedEvent.class).contains(i.getParameterTypes()[0])) {
                            websocketManager.addHandler(j.path(), i.getParameterTypes()[0], new WebSocketBundle(handler, i));
                        }
                    }
                }
            } else if (i.isAnnotationPresent(WebsocketEvent.class) && !Modifier.isStatic(i.getModifiers())) {
                if (i.getParameterTypes().length == 1 && i.getAnnotation(WebsocketEvent.class).path().startsWith("/")) {
                    if (Arrays.asList(WebsocketMessageEvent.class, WebsocketBinaryDataEvent.class, WebsocketConnectedEvent.class, WebsocketDisconnectedEvent.class).contains(i.getParameterTypes()[0])) {
                        websocketManager.addHandler(i.getAnnotation(WebsocketEvent.class).path(), i.getParameterTypes()[0], new WebSocketBundle(handler, i));
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
