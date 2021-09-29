package net.redstonecraft.redstoneapi.webserver;

import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.data.json.parser.JSONParser;
import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.data.json.parser.ParseException;
import net.redstonecraft.redstoneapi.webserver.obj.HttpHeaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebRequest {

    private String path;
    private final HttpHeaders headers;
    private final InputStream content;
    private final WebServer webServer;
    private final HttpMethod method;
    final String protocol;

    public WebRequest(HttpMethod method, String path, String protocol, List<HttpHeader> headers, InputStream content, WebServer webServer) {
        this.method = method;
        this.protocol = protocol;
        this.webServer = webServer;
        if (path.contains("?")) {
            this.path = "";
        } else {
            this.path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        }
        this.headers = new HttpHeaders(headers);
        this.content = content;
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
        return content;
    }

    public byte[] getContent() {
        try {
            return content.readAllBytes();
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

    static WebRequest parseRequest(WebServer.Connection connection, ByteBuffer buffer, int len, List<WebServer.Connection> connections, WebServer webServer) throws IOException {
        InputStream is = new ByteArrayInputStream(Arrays.copyOfRange(buffer.array(), 0, len));
        StringBuilder sb = new StringBuilder();
        int t;
        while ((t = is.read()) != 32 && t != -1) { // 32 = ' '
            sb.append(new String(new byte[]{(byte) t}, StandardCharsets.UTF_8));
        }
        if (t == -1) {
            return null;
        }
        String method = sb.toString();
        sb = new StringBuilder();
        while ((t = is.read()) != 32 && t != -1) { // 32 = ' '
            sb.append(new String(new byte[]{(byte) t}, StandardCharsets.UTF_8));
        }
        if (t == -1) {
            return null;
        }
        String path = sb.toString();
        sb = new StringBuilder();
        int prev1 = 0;
        while ((t = is.read()) != 10 && prev1 != 13 && t != -1) { // 13 = '\r' // 10 = '\n'
            sb.append(new String(new byte[]{(byte) t}, StandardCharsets.UTF_8));
            prev1 = t;
        }
        if (t == -1) {
            return null;
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
            return null;
        }
        List<HttpHeader> headers = new LinkedList<>();
        for (String i : sb.toString().split("\r\n")) {
            String[] a = i.split(": ", 2);
            headers.add(new HttpHeader(a[0], a[1]));
        }
        if (!HttpMethod.isMethodAvailable(method)) {
            return null;
        }
        return new WebRequest(HttpMethod.valueOf(method), path, protocol, headers, is, webServer);
    }

    boolean validate() {
    }

    private static byte[] toPrimitiveBytes(List<Byte> list) {
        byte[] arr = new byte[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

}
