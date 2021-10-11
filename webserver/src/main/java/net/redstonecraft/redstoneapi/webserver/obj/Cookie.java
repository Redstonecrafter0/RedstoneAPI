package net.redstonecraft.redstoneapi.webserver.obj;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public record Cookie(String key, String value) {

    @Override
    public String key() {
        return URLEncoder.encode(key, StandardCharsets.UTF_8);
    }

    @Override
    public String value() {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
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
