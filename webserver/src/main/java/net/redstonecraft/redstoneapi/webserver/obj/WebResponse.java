package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.core.HttpResponseCode;
import net.redstonecraft.redstoneapi.webserver.WebServer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * The response sent to the client when an HTTP request came in to the {@link net.redstonecraft.redstoneapi.webserver.WebServer}
 *
 * @author Redstonecrafter0
 * @since 1.2
 */
public record WebResponse(InputStream content, HttpResponseCode code, HttpHeader... headers) {

    @Override
    public HttpResponseCode code() {
        return code == null ? HttpResponseCode.INTERNAL_SERVER_ERROR : code;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private InputStream content = new ByteArrayInputStream(new byte[0]);
        private final Map<String, HttpHeader> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        private HttpResponseCode code = HttpResponseCode.OK;

        private Builder() {
        }

        public Builder setContent(String content) {
            return setContent(content.getBytes(StandardCharsets.UTF_8));
        }

        public Builder setContent(ByteBuffer buffer) {
            return setContent(buffer.array());
        }

        public Builder setContent(byte[] data) {
            return setContent(new ByteArrayInputStream(data));
        }

        public Builder setContent(InputStream is) {
            this.content = is;
            return this;
        }

        public Builder addHeader(HttpHeader header) {
            if (!header.key().equalsIgnoreCase("Set-Cookie")) {
                headers.remove(header.key());
            }
            headers.put(header.key(), header);
            return this;
        }

        public Builder addCookie(Cookie cookie) {
            return addCookie(cookie, null, null, null, null, false, false, null);
        }

        public Builder addCookie(Cookie cookie, Date expires, Long maxAge, String domain, String path, boolean secure, boolean httpOnly, Cookie.SameSite sameSite) {
            return addHeader(new HttpHeader("Set-Cookie", serializeCookie(cookie, expires, maxAge, domain, path, secure, httpOnly, sameSite)));
        }

        public Builder setResponseCode(HttpResponseCode code) {
            this.code = code;
            return this;
        }

        public WebResponse build() {
            return new WebResponse(content, code, headers.values().toArray(new HttpHeader[0]));
        }

        private static String serializeCookie(Cookie cookie, Date expires, Long maxAge, String domain, String path, boolean secure, boolean httpOnly, Cookie.SameSite sameSite) {
            StringBuilder sb = new StringBuilder(cookie.key() + "=" + cookie.value());
            if (expires != null) {
                sb.append("; Expires=").append(WebServer.toServerTime(expires));
            }
            if (maxAge != null) {
                sb.append("; Max-Age=").append(maxAge);
            }
            if (domain != null) {
                sb.append("; Domain=").append(domain);
            }
            if (path != null) {
                sb.append("; Path=").append(path);
            }
            if (sameSite != null) {
                sb.append("; SameSite=").append(sameSite);
            }
            if (secure) {
                sb.append("; Secure");
            }
            if (httpOnly) {
                sb.append("; HttpOnly");
            }
            return sb.toString();
        }

    }

}
