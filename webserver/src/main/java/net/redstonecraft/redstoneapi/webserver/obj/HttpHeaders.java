package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.core.HttpHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public record HttpHeaders(List<HttpHeader> headers) {

    public String get(String key) {
        return HttpHeader.getByKey(headers, key).orElse(new HttpHeader(null, null)).value();
    }

    public Optional<String> getOptional(String key) {
        String value = get(key);
        return value == null ? Optional.empty() : Optional.of(value);
    }

    public String getOrDefault(String key, String defaultValue) {
        return HttpHeader.getByKey(headers, key).orElse(new HttpHeader("", defaultValue)).value();
    }

    public int getContentLength() {
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

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "headers=" + headers +
                '}';
    }

}
