package net.redstonecraft.redstoneapi.webserver;

import net.redstonecraft.redstoneapi.core.HttpResponseCode;
import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.data.json.parser.JSONParser;
import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.data.json.parser.ParseException;
import net.redstonecraft.redstoneapi.webserver.obj.HttpHeaders;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebRequest {

    private final String path;
    private final HttpHeaders headers;
    private final InputStream inputStream;
    private final WebServer webServer;
    private final HttpMethod method;
    private final Map<String, String> args = new HashMap<>();
    private final String protocol;

    public WebRequest(HttpMethod method, String path, String protocol, HttpHeaders headers, InputStream inputStream, WebServer webServer) {
        this.method = method;
        this.protocol = protocol;
        this.webServer = webServer;
        if (path.contains("?")) {
            String[] splitted = path.split("\\?", 2);
            this.path = splitted[0];
            for (String i : splitted[1].split("&")) {
                String[] arg = i.split("=");
                args.put(URLDecoder.decode(arg[0], StandardCharsets.UTF_8), URLDecoder.decode(arg[1], StandardCharsets.UTF_8));
            }
        } else {
            this.path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        }
        this.headers = headers;
        this.inputStream = inputStream;
    }

    public String getPath() {
        return path;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public byte[] getContent() {
        try {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    public String getContentAsString() {
        return new String(getContent(), StandardCharsets.UTF_8);
    }

    public JSONArray getContentAsJsonArray() {
        return JSONParser.parseArray(getContentAsString());
    }

    public JSONObject getContentAsJsonObject() throws ParseException {
        return JSONParser.parseObject(getContentAsString());
    }

    public String getProtocol() {
        return protocol;
    }

    public WebServer getWebServer() {
        return webServer;
    }

    @Override
    public String toString() {
        return "WebRequest{" +
                "path='" + path + '\'' +
                ", headers=" + headers +
                '}';
    }

    static Object parseRequest(WebServer.Connection connection, ByteBuffer buffer, int len, List<WebServer.Connection> connections, WebServer webServer) throws IOException {
        InputStream is = new ByteArrayInputStream(Arrays.copyOfRange(buffer.array(), 0, len));
        StringBuilder sb = new StringBuilder();
        int t;
        while ((t = is.read()) != 32 && t != -1) { // 32 = ' '
            sb.append(new String(new byte[]{(byte) t}, StandardCharsets.UTF_8));
        }
        if (t == -1) {
            return new WebResponse("", HttpResponseCode.BAD_REQUEST);
        }
        String method = sb.toString();
        sb = new StringBuilder();
        while ((t = is.read()) != 32 && t != -1) { // 32 = ' '
            sb.append(new String(new byte[]{(byte) t}, StandardCharsets.UTF_8));
        }
        if (t == -1) {
            return new WebResponse("", HttpResponseCode.BAD_REQUEST);
        }
        String path = sb.toString();
        sb = new StringBuilder();
        int prev1 = 0;
        while ((t = is.read()) != 10 && prev1 != 13 && t != -1) { // 13 = '\r' // 10 = '\n'
            sb.append(new String(new byte[]{(byte) t}, StandardCharsets.UTF_8));
            prev1 = t;
        }
        if (t == -1) {
            return new WebResponse("", HttpResponseCode.BAD_REQUEST);
        }
        String protocol = sb.toString().trim();
        sb = new StringBuilder();
        List<Byte> buf = new ArrayList<>(4);
        buf.add((byte) 0);
        buf.add((byte) 0);
        buf.add((byte) 0);
        buf.add((byte) 0);
        t = is.read();
        boolean eoh = false;
        while (t != -1) {
            sb.append(new String(new byte[]{(byte) t}, StandardCharsets.UTF_8));
            buf.add((byte) t);
            buf.remove(0);
            if (new String(toPrimitiveBytes(buf), StandardCharsets.UTF_8).equals("\r\n\r\n")) {
                eoh = true;
                break;
            } else {
                t = is.read();
            }
        }
        if (!eoh) {
            return new WebResponse("", HttpResponseCode.BAD_REQUEST);
        }
        List<HttpHeader> tmpHeaders = new LinkedList<>();
        for (String i : sb.toString().split("\r\n")) {
            String[] a = i.split(": ", 2);
            tmpHeaders.add(new HttpHeader(a[0], a[1]));
        }
        HttpHeaders headers = new HttpHeaders(tmpHeaders);
        if (!HttpMethod.isMethodAvailable(method)) {
            return new WebResponse("", HttpResponseCode.METHOD_NOT_ALLOWED);
        }
        if (!path.startsWith("/") && !(path.startsWith("*") && method.equals(HttpMethod.OPTIONS.name()))) {
            return new WebResponse("", HttpResponseCode.BAD_REQUEST);
        }
        if (path.length() > 2048) {
            return new WebResponse("", HttpResponseCode.URI_TOO_LONG);
        }
        if (!protocol.equals("HTTP/1.1") && !protocol.equals("HTTP/1.0")) {
            return new WebResponse("", HttpResponseCode.HTTP_VERSION_NOT_SUPPORTED);
        }
        if (HttpMethod.valueOf(method).hasBody() && (headers.get("Content-Length") == null || headers.getContentLength() != is.available())) {
            return new WebResponse("", HttpResponseCode.BAD_REQUEST);
        }
        return new WebRequest(HttpMethod.valueOf(method), path, protocol, headers, is, webServer);
    }

    private static byte[] toPrimitiveBytes(List<Byte> list) {
        byte[] arr = new byte[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

}