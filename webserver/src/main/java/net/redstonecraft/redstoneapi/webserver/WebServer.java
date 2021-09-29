package net.redstonecraft.redstoneapi.webserver;

import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.loader.FileLocator;
import net.redstonecraft.redstoneapi.info.RedstoneAPI;
import net.redstonecraft.redstoneapi.core.*;
import net.redstonecraft.redstoneapi.webserver.annotations.Route;
import net.redstonecraft.redstoneapi.webserver.annotations.Routes;
import net.redstonecraft.redstoneapi.webserver.obj.*;
import net.redstonecraft.redstoneapi.webserver.websocket.WebsocketEvent;
import net.redstonecraft.redstoneapi.webserver.websocket.WebsocketEvents;
import net.redstonecraft.redstoneapi.webserver.websocket.events.WebsocketBinaryDataEvent;
import net.redstonecraft.redstoneapi.webserver.websocket.events.WebsocketConnectedEvent;
import net.redstonecraft.redstoneapi.webserver.websocket.events.WebsocketDisconnectedEvent;
import net.redstonecraft.redstoneapi.webserver.websocket.events.WebsocketMessageEvent;
import org.apache.commons.lang.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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
        public ErrorResponse handleError(HttpResponseCode code, String url, Map<String, String> args, HttpHeaders headers) {
            if (html.equals("")) {
                try {
                    html = new String(Objects.requireNonNull(getClass().getResourceAsStream("/webserver/error.html")).readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException ignored) {
                }
            }
            return new ErrorResponse(code, html.replace("{code}", String.valueOf(code.getCode()))
                    .replace("{desc}", code.getDescription())
                    .replace("{text}", code.getCode() < 500 ? "Ohh. There was an error. Try another page." : "Ohh. There was an error. Try again later."));
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

    private final Thread thread;
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
    public WebServer() throws IOException {
        this("localhost", 8080);
    }

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
        thread = new Thread(() -> {
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

    private static boolean isMatch(byte[] pattern, byte[] input, int pos) {
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[pos + i]) {
                return false;
            }
        }
        return true;
    }

    private static byte[][] byteArraySplit(byte[] pattern, byte[] input) {
        List<byte[]> l = new ArrayList<>();
        int blockStart = 0;
        for (int i = 0; i < input.length; i++) {
            if (isMatch(pattern, input, i)) {
                l.add(Arrays.copyOfRange(input, blockStart, i));
                blockStart = i + pattern.length;
                i = blockStart;
            }
        }
        l.add(Arrays.copyOfRange(input, blockStart, input.length));
        byte[][] arr = new byte[l.size()][];
        for (Enumerate.Item<byte[]> i : new Enumerate<>(l)) {
            arr[i.getCount()] = i.getValue();
        }
        return arr;
    }

    public Jinjava getJinjava() {
        return jinjava;
    }

    public void stop() {
        run = false;
        try {
            serverSocket.close();
            selector.close();
            thread.stop();
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
            connections.stream().filter(connection -> connection.keepAlive < t).forEach(connection -> {
                try {
                    connection.channel.close();
                } catch (IOException ignored) {
                }
                remove.add(connection);
            });
            connections.removeIf(remove::contains);
            List<WebSocketConnection> remove1 = new LinkedList<>();
            websocketManager.entrySet().stream().filter(entry -> entry.getValue().time < t).forEach(entry -> {
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
                    timeout.payload = lastKeepAliveId;
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
                                    if (id.equals(String.valueOf(websocketManager.getPing(webSocketConnection).payload))) {
                                        websocketManager.getPing(webSocketConnection).time = System.currentTimeMillis();
                                    }
                                } else if (action == 0x1) {
                                    String payload = new String(decoded, StandardCharsets.UTF_8);
                                    websocketManager.getPing(webSocketConnection).time = System.currentTimeMillis();
                                    websocketManager.executeMessageEvent(webSocketConnection, payload);
                                } else if (action == 0x9) {
                                    webSocketConnection.send(decoded);
                                } else if (action == 0x2) {
                                    websocketManager.getPing(webSocketConnection).time = System.currentTimeMillis();
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
                                continue;
                            }
                            try {
                                ByteBuffer buffer = ByteBuffer.allocate(8192);
                                int len;
                                if ((len = connection.channel.read(buffer)) == -1) {
                                    try {
                                        connection.channel.close();
                                    } catch (IOException ignored) {
                                    }
                                    connections.remove(connection);
                                    continue;
                                }
                                try {
                                    if (!(new String(buffer.array(), StandardCharsets.UTF_8).contains("\r\n\r\n"))) {
                                        errorHandlerManager.handle(HttpResponseCode.REQUEST_HEADER_FIELDS_TOO_LARGE, "UNKNOWN", new HashMap<>(), new HttpHeaders(new ArrayList<>()), e -> {
                                            try {
                                                sendResponse(connection.channel, "UNKNOWN", "UNKNOWN", e);
                                                connection.channel.close();
                                                connections.remove(connection);
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        });
                                        return;
                                    }
                                    Object parsed = WebRequest.parseRequest(connection, buffer, len, connections, this);
                                    if (parsed instanceof ErrorResponse errorResponse) {
                                        sendResponse(connection.channel, "UNKNOWN", "UNKNOWN", errorResponse);
                                        try {
                                            connection.channel.close();
                                        } catch (IOException ignored) {
                                        }
                                        connections.remove(connection);
                                    } else if (parsed instanceof WebRequest request) {
                                        try {
                                            try {
                                                if (request.getMethod().equals(HttpMethod.GET) && request.getProtocol().equals("HTTP/1.1") &&
                                                        Objects.requireNonNull(request.getHeaders().get("Connection")).equals("Upgrade") &&
                                                        Objects.requireNonNull(request.getHeaders().get("Upgrade")).equals("websocket") &&
                                                        request.getHeaders().get("Sec-WebSocket-Version") != null &&
                                                        request.getHeaders().get("Sec-WebSocket-Key") != null) {
                                                    throw new NotImplementedException();
                                                    // TODO: implement socket connection
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
                                                        sendResponse(connection.channel, "GET", request.getPath(), HttpResponseCode.OK, Files.readAllBytes(file.toPath()), new HttpHeader("Content-Type", MimeType.getByFilename(file.getCanonicalPath()).getMimetype()));
                                                    } catch (NoSuchFileException ignored) {
                                                        errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, request.getPath(), request.getArgs(), request.getHeaders(), response -> {
                                                            try {
                                                                sendResponse(connection.channel, "GET", request.getPath(), response);
                                                            } catch (IOException ioException) {
                                                                ioException.printStackTrace();
                                                            }
                                                        });
                                                    } catch (Throwable ignored) {
                                                        errorHandlerManager.handle(HttpResponseCode.INTERNAL_SERVER_ERROR, request.getPath(), request.getArgs(), request.getHeaders(), response -> {
                                                            try {
                                                                sendResponse(connection.channel, "GET", request.getPath(), response);
                                                            } catch (IOException ioException) {
                                                                ioException.printStackTrace();
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, request.getPath(), request.getArgs(), request.getHeaders(), response -> {
                                                        try {
                                                            sendResponse(connection.channel, "GET", request.getPath(), response);
                                                        } catch (IOException ioException) {
                                                            ioException.printStackTrace();
                                                        }
                                                    });
                                                }
                                                try {
                                                    connection.channel.close();
                                                } catch (IOException ignored) {
                                                }
                                                connections.remove(connection);
                                                return;
                                            } else if (request.getMethod().equals(HttpMethod.GET) && request.getPath().equals("/favicon.ico")) {
                                                File favicon = new File(new File(staticDir, "static"), "favicon.ico");
                                                if (favicon.exists()) {
                                                    WebResponse response = new WebResponse(Files.readAllBytes(favicon.toPath()), HttpResponseCode.OK, new HttpHeader("Content-Type", MimeType.getByFilename(favicon.getName()).getMimetype()));
                                                    sendResponse(connection.channel, "GET", request.getPath(), response);
                                                } else {
                                                    errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, request.getPath(), request.getArgs(), request.getHeaders(), response -> {
                                                        try {
                                                            sendResponse(connection.channel, "GET", request.getPath(), response);
                                                        } catch (IOException ioException) {
                                                            ioException.printStackTrace();
                                                        }
                                                    });
                                                }
                                                connections.remove(connection);
                                                return;
                                            }
                                            if (!httpMethod.equals(HttpMethod.OPTIONS)) {
                                                try {
                                                    HandlerBundle handlerBundle;
                                                    if (httpMethod.equals(HttpMethod.HEAD)) {
                                                        handlerBundle = handlerManager.getHandler(HttpMethod.GET.getRequestClass(), path);
                                                    } else {
                                                        handlerBundle = handlerManager.getHandler(httpMethod.getRequestClass(), path);
                                                    }
                                                    WebResponse webResponse;
                                                    if (handlerBundle != null) {
                                                        try {
                                                            handlerBundle.getMethod().setAccessible(true);
                                                            webResponse = (WebResponse) handlerBundle.getMethod().invoke(handlerBundle.getHandler(), webRequest);
                                                        } catch (Throwable e) {
                                                            errorHandlerManager.handle(HttpResponseCode.INTERNAL_SERVER_ERROR, webRequest.getPath(), webRequest.getWebArguments(), webRequest.getHeaders(), response -> {
                                                                try {
                                                                    sendResponse(connection.channel, httpMethod.name(), path, response);
                                                                    try {
                                                                        connection.channel.close();
                                                                    } catch (IOException ignored) {
                                                                    }
                                                                    connections.remove(connection);
                                                                } catch (IOException ioException) {
                                                                    ioException.printStackTrace();
                                                                }
                                                            });
                                                            return;
                                                        }
                                                    } else {
                                                        errorHandlerManager.handle(HttpResponseCode.NOT_FOUND, webRequest.getPath(), webRequest.getWebArguments(), webRequest.getHeaders(), response -> {
                                                            try {
                                                                sendResponse(connection.channel, httpMethod.name(), path, response);
                                                                try {
                                                                    connection.channel.close();
                                                                } catch (IOException ignored) {
                                                                }
                                                                connections.remove(connection);
                                                            } catch (IOException ioException) {
                                                                ioException.printStackTrace();
                                                            }
                                                        });
                                                        return;
                                                    }
                                                    sendResponse(connection.channel, httpMethod.name(), path, webResponse);
                                                    try {
                                                        connection.channel.close();
                                                    } catch (IOException ignored) {
                                                    }
                                                    connections.remove(connection);
                                                } catch (Throwable e) {
                                                    e.printStackTrace();
                                                    try {
                                                        connection.channel.close();
                                                    } catch (IOException ignored) {
                                                    }
                                                    connections.remove(connection);
                                                }
                                            } else {
                                                Set<String> list = new HashSet<>();
                                                list.add("OPTIONS");
                                                for (HttpMethod i : HttpMethod.values()) {

                                                    if (handlerManager.getHandler(i.getRequestClass(), path) != null) {
                                                        try {
                                                            list.add(i.name());
                                                        } catch (IllegalStateException ignored) {
                                                        }
                                                    }
                                                }
                                                if (list.contains("GET")) {
                                                    try {
                                                        list.add("HEAD");
                                                    } catch (IllegalStateException ignored) {
                                                    }
                                                }
                                                WebResponse webResponse = new WebResponse(new byte[0], HttpResponseCode.NO_CONTENT, new HttpHeader("Allow", String.join(", ", list)));
                                                sendResponse(connection.channel, method, path, webResponse);
                                                try {
                                                    connection.channel.close();
                                                } catch (IOException ignored) {
                                                }
                                                connections.remove(connection);
                                            }
                                        } catch (Throwable ignored) {
                                            try {
                                                connection.channel.close();
                                            } catch (IOException ignored1) {
                                            }
                                            connections.remove(connection);
                                        }
                                    }
                                } catch (IOException | NumberFormatException ignored) {
                                    try {
                                        connection.channel.close();
                                    } catch (IOException ignored1) {
                                    }
                                    connections.remove(connection);
                                }
                            } catch (IndexOutOfBoundsException | IOException ignored) {
                                try {
                                    connection.channel.close();
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

    private boolean getBitByByte(byte b, int pos) {
        return (b >> (8 - (pos + 1)) & 0x0001) == 1;
    }

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

    private void sendResponse(SocketChannel channel, String method, String path, WebResponse response) throws IOException {
        sendResponse(channel, method, path, response.getCode(), response.getContent(), response.getHeaders());
    }

    private String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    private void sendResponse(SocketChannel channel, String method, String path, HttpResponseCode code, byte[] content, HttpHeader... headers) throws IOException {
        Set<String> list = new HashSet<>();
        list.add("Content-Length: " + (code.equals(HttpResponseCode.NO_CONTENT) ? 0 : content.length));
        for (HttpHeader i : headers) {
            list.add(i.getKey() + ": " + i.getValue());
        }
        list.add("Server: RedstoneAPI/" + RedstoneAPI.getVersion().toString().replace("v", ""));
        list.add("Date: " + getServerTime());
        if (!list.contains("Connection: Upgrade")) {
            list.add("Connection: close");
        }
        byte[] resp = ("HTTP/1.1 " + code.getCode() + " " + code.getDescription() + "\r\n" + String.join("\r\n", list) + "\r\n\r\n").getBytes(StandardCharsets.UTF_8);
        byte[] respContent;
        if (method.equals("HEAD")) {
            respContent = resp;
        } else {
            respContent = new byte[resp.length + content.length];
            System.arraycopy(resp, 0, respContent, 0, resp.length);
            System.arraycopy(content, 0, respContent, resp.length, content.length);
        }
        if (code.equals(HttpResponseCode.NO_CONTENT)) {
            respContent = resp;
        }
        channel.write(ByteBuffer.wrap(respContent));
        if (logging) {
            logger.info("[" + channel.socket().getInetAddress().getHostAddress() + "] " + method + " " + path + " " + code.getCode() + " " + code.getDescription() + " " + respContent.length);
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
            if (i.channel.equals(channel)) {
                return i;
            }
        }
        return null;
    }

    public void addHandler(RequestHandler handler) {
        try {
            handler.setWebServer(this);
            for (Method i : handler.getClass().getMethods()) {
                if (i.isAnnotationPresent(Routes.class) && !Modifier.isStatic(i.getModifiers()) && i.getReturnType().equals(WebResponse.class)) {
                    for (Route j : i.getAnnotation(Routes.class).value()) {
                        if (j.value().startsWith("/")) {
                            Class<?>[] parameterTypes = i.getParameterTypes();
                            if (parameterTypes.length == 1) {
                                if (WebRequest.class.isAssignableFrom(parameterTypes[0])) {
                                    handlerManager.setHandler((Class<? extends WebRequest>) parameterTypes[0], j.value(), new HandlerBundle(handler, i));
                                }
                            }
                        }
                    }
                } else if (i.isAnnotationPresent(Route.class) && !Modifier.isStatic(i.getModifiers()) && i.getReturnType().equals(WebResponse.class)) {
                    Route j = i.getAnnotation(Route.class);
                    if (j.value().startsWith("/")) {
                        Class<?>[] parameterTypes = i.getParameterTypes();
                        if (parameterTypes.length == 1) {
                            if (WebRequest.class.isAssignableFrom(parameterTypes[0])) {
                                handlerManager.setHandler((Class<? extends WebRequest>) parameterTypes[0], j.value(), new HandlerBundle(handler, i));
                            }
                        }
                    }
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
        } catch (IllegalStateException ignored) {
        }
    }

    public void removeHandler(String method, String path) {
        try {
            Class<? extends WebRequest> requestMethod = HttpMethod.valueOf(method).getRequestClass();
            handlerManager.setHandler(requestMethod, path, null);
        } catch (IllegalArgumentException ignored) {
        }
    }

    public void removeHandlers(String path) {
        for (HttpMethod i : HttpMethod.values()) {
            removeHandler(i.name(), path);
        }
    }

    public void removeHandler(RequestHandler handler) {
        handlerManager.removeHandler(handler);
        websocketManager.removeHandler(handler);
    }

    public void setErrorHandler(HttpResponseCode code, ErrorHandler errorHandler) {
        errorHandlerManager.setHandler(code, errorHandler);
    }

    private static class HandlerManager {

        private final Map<Class<? extends WebRequest>, Map<String, HandlerBundle>> handlers = new HashMap<>();

        private void setHandler(Class<? extends WebRequest> requestMethod, String path, HandlerBundle handlerBundle) {
            if (!handlers.containsKey(requestMethod)) {
                handlers.put(requestMethod, new HashMap<>());
            }
            handlers.get(requestMethod).put(path, handlerBundle);
        }

        private HandlerBundle getHandler(Class<? extends WebRequest> requestMethod, String path) {
            if (path.contains("?")) {
                path = path.split("\\?")[0];
            }
            Map<String, HandlerBundle> map = handlers.get(requestMethod);
            if (map != null) {
                return map.get(path);
            }
            return null;
        }

        private void removeHandler(RequestHandler handler) {
            for (Map.Entry<Class<? extends WebRequest>, Map<String, HandlerBundle>> i : handlers.entrySet()) {
                for (Map.Entry<String, HandlerBundle> j : i.getValue().entrySet()) {
                    if (j.getValue().getHandler().equals(handler)) {
                        handlers.get(i.getKey()).remove(j.getKey());
                    }
                }
            }
        }

    }

    private static class WebsocketManager {

        private final Map<WebSocketConnection, WebSocketPing> webSocketConnections = new HashMap<>();
        private final Map<String, Map<Class<?>, List<WebSocketBundle>>> endpoints = new HashMap<>();

        private void addHandler(String path, Class<?> event, WebSocketBundle bundle) {
            if (!endpoints.containsKey(path)) {
                endpoints.put(path, new HashMap<>());
            }
            if (!endpoints.get(path).containsKey(event)) {
                endpoints.get(path).put(event, new ArrayList<>());
            }
            endpoints.get(path).get(event).add(bundle);
        }

        private void removeHandler(RequestHandler handler) {
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

        private void registerConnection(WebSocketConnection webSocketConnection, WebSocketPing ping) {
            webSocketConnections.put(webSocketConnection, ping);
        }

        private void unregisterConnection(WebSocketConnection webSocketConnection) {
            webSocketConnections.remove(webSocketConnection);
        }

        private WebSocketPing getPing(WebSocketConnection webSocketConnection) {
            return webSocketConnections.get(webSocketConnection);
        }

        private Set<Map.Entry<WebSocketConnection, WebSocketPing>> entrySet() {
            return webSocketConnections.entrySet();
        }

        private void forEach(BiConsumer<? super WebSocketConnection, ? super WebSocketPing> consumer) {
            webSocketConnections.forEach(consumer);
        }

        private boolean containsKey(WebSocketConnection webSocketConnection) {
            return webSocketConnections.containsKey(webSocketConnection);
        }

        private Set<WebSocketConnection> keySet() {
            return webSocketConnections.keySet();
        }

        private void broadcast(String path, String message) {
            webSocketConnections.entrySet().stream().filter(e -> e.getKey().getPath().equals(path)).forEach(e -> {
                try {
                    e.getKey().send(message);
                } catch (IOException ignored) {
                }
            });
        }

        private void broadcast(String path, byte[] payload) {
            webSocketConnections.entrySet().stream().filter(e -> e.getKey().getPath().equals(path)).forEach(e -> {
                try {
                    e.getKey().send(payload);
                } catch (IOException ignored) {
                }
            });
        }

        private void broadcast(String path, String room, String message) {
            webSocketConnections.entrySet().stream().filter(e -> e.getKey().getPath().equals(path) && e.getKey().getRoom().equals(room)).forEach(e -> {
                try {
                    e.getKey().send(message);
                } catch (IOException ignored) {
                }
            });
        }

        private void broadcast(String path, String room, byte[] payload) {
            webSocketConnections.entrySet().stream().filter(e -> e.getKey().getPath().equals(path) && e.getKey().getRoom().equals(room)).forEach(e -> {
                try {
                    e.getKey().send(payload);
                } catch (IOException ignored) {
                }
            });
        }

        private boolean pathExists(String path) {
            return endpoints.containsKey(path);
        }

        private void executeConnectEvent(WebSocketConnection connection) {
            try {
                endpoints.get(connection.getPath()).get(WebsocketConnectedEvent.class).forEach(bundle -> {
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

        private void executeDisconnectEvent(WebSocketConnection connection) {
            try {
                endpoints.get(connection.getPath()).get(WebsocketDisconnectedEvent.class).forEach(bundle -> {
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

        private void executeMessageEvent(WebSocketConnection connection, String message) {
            try {
                endpoints.get(connection.getPath()).get(WebsocketMessageEvent.class).forEach(bundle -> {
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

        private void executeBinaryEvent(WebSocketConnection connection, byte[] payload) {
            try {
                endpoints.get(connection.getPath()).get(WebsocketBinaryDataEvent.class).forEach(bundle -> {
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

    static class Connection {

        final SocketChannel channel;
        long keepAlive = System.currentTimeMillis();

        Connection(SocketChannel channel) {
            this.channel = channel;
        }

    }

    private static class WebSocketPing {

        private long time;
        private long payload;

        private WebSocketPing(long time, long payload) {
            this.time = time;
            this.payload = payload;
        }

    }

    public class ErrorHandlerManager {

        private final Map<HttpResponseCode, ErrorHandler> errorHandlers = new HashMap<>();
        private final ErrorHandler universalErrorHandler;

        private ErrorHandlerManager(ErrorHandler universalErrorHandler) {
            this.universalErrorHandler = universalErrorHandler;
        }

        private void setHandler(HttpResponseCode code, ErrorHandler handler) {
            if (HttpResponseCode.isError(code)) {
                errorHandlers.put(code, handler);
            }
        }

        private void removeHandler(HttpResponseCode code) {
            errorHandlers.remove(code);
        }

        private void handle(HttpResponseCode code, String url, Map<String, String> webArgs, HttpHeaders headers, Consumer<ErrorResponse> callback) {
            threadPool.submit(() -> {
                ErrorResponse errorResponse = getHandler(code).handleError(code, url, webArgs, headers);
                errorResponse.setErrorCode(code);
                callback.accept(errorResponse);
            });
        }

        private ErrorHandler getHandler(HttpResponseCode code) {
            ErrorHandler errorHandler = errorHandlers.get(code);
            return errorHandler != null ? errorHandler : universalErrorHandler;
        }

    }

}
