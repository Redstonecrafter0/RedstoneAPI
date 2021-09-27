package net.redstonecraft.redstoneapi.webserver.obj.requests;

import net.redstonecraft.redstoneapi.data.json.JSONArray;
import net.redstonecraft.redstoneapi.data.json.JSONObject;
import net.redstonecraft.redstoneapi.data.json.parser.JSONParser;
import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.data.json.parser.ParseException;
import net.redstonecraft.redstoneapi.webserver.WebServer;
import net.redstonecraft.redstoneapi.webserver.obj.WebArgument;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class WebRequest {

    private final String path;
    private final HttpHeader[] headers;
    private final byte[] content;
    private final WebArgument[] webArguments;
    private final WebServer webServer;

    public WebRequest(String path, HttpHeader[] headers, byte[] content, WebServer webServer) {
        String path1;
        this.webServer = webServer;
        if (path.contains("?")) {
            String[] tmp = path.split("\\?", 2);
            try {
                path1 = URLDecoder.decode(tmp[0], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                path1 = tmp[0];
            }
            String[] webArgsRaw = tmp[1].split("&");
            WebArgument[] webArgumentsTmp = new WebArgument[webArgsRaw.length];
            WebArgument[] webArgumentsTmp1 = new WebArgument[0];
            try {
                for (int i = 0; i < webArgumentsTmp.length; i++) {
                    String[] split = webArgsRaw[i].split("=", 2);
                    webArgumentsTmp[i] = new WebArgument(split[0], split[1]);
                }
                webArgumentsTmp1 = webArgumentsTmp;
            } catch (Exception ignored) {
            }
            webArguments = webArgumentsTmp1;
        } else {
            try {
                path1 = URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException ignored1) {
                path1 = path;
            }
            webArguments = new WebArgument[0];
        }
        this.path = path1;
        this.headers = headers;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public HttpHeader[] getHeaders() {
        return headers;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentAsString() {
        return new String(content, StandardCharsets.UTF_8);
    }

    public JSONArray getContentAsJsonArray() {
        return JSONParser.parseArray(getContentAsString());
    }

    public JSONObject getContentAsJsonObject() throws ParseException {
        return JSONParser.parseObject(getContentAsString());
    }

    public WebArgument[] getWebArguments() {
        return webArguments;
    }

    public WebServer getWebServer() {
        return webServer;
    }

    @Override
    public String toString() {
        return "WebRequest{" +
                "path='" + path + '\'' +
                ", headers=" + Arrays.toString(headers) +
                ", contentSize=" + content.length +
                ", webArguments=" + Arrays.toString(webArguments) +
                '}';
    }
}
