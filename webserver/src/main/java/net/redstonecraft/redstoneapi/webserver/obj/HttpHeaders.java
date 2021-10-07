package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.core.HttpHeader;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public record HttpHeaders(List<HttpHeader> headers) {

    public String get(String key) {
        HttpHeader header = HttpHeader.getByKey(headers, key);
        return header == null ? null : header.getValue();
    }

    public long getContentLength() {
        try {
            return Integer.parseInt(get("Content-Length"));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    public Map<String, String> getCookies() {
        String header = get("Cookie");
        if (header == null) {
            return new HashMap<>();
        }
        Map<String, String> cookies = new HashMap<>();
        for (String i : header.split(";")) {
            String[] parsed = i.split("=");
            if (parsed.length == 2) {
                cookies.put(parsed[0].trim(), parsed[1].trim());
            }
        }
        return cookies;
    }

    public byte[] serialize() {
        StringBuilder sb = new StringBuilder();
        for (HttpHeader i : headers) {
            //noinspection StringConcatenationInsideStringBufferAppend
            sb.append(i.getKey() + ": " + i.getValue() + "\r\n");
        }
        return sb.append("\r\n").toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "headers=" + headers +
                '}';
    }

}
