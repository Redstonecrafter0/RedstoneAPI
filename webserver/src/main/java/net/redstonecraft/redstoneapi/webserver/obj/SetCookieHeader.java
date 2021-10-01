package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.webserver.WebServer;

import java.util.Date;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class SetCookieHeader extends HttpHeader {

    public SetCookieHeader(Cookie cookie) {
        this(cookie, null, null, null, null, false, false, null);
    }

    public SetCookieHeader(Cookie cookie, Date expires, Long maxAge, String domain, String path, boolean secure, boolean httpOnly, SameSite sameSite) {
        super("Set-Cookie", serialize(cookie, expires, maxAge, domain, path, secure, httpOnly, sameSite));
    }

    private static String serialize(Cookie cookie, Date expires, Long maxAge, String domain, String path, boolean secure, boolean httpOnly, SameSite sameSite) {
        StringBuilder sb = new StringBuilder(cookie.key() + "=" + cookie.value());
        if (expires != null) {
            sb.append("; Expires=").append(WebServer.getServerTime());
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

    public enum SameSite {

        STRICT("Strict"),
        LAX("Lax"),
        NONE("None");

        private final String string;

        SameSite(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

    }

}
