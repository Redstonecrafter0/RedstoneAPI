package net.redstonecraft.redstoneapi.webserver.ext.forms;

import net.redstonecraft.redstoneapi.core.tools.Hashlib;
import net.redstonecraft.redstoneapi.core.tuple.Pair;
import net.redstonecraft.redstoneapi.webserver.WebRequest;
import net.redstonecraft.redstoneapi.webserver.obj.Cookie;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class FormValidator {

    private final String secretKey;
    private final SecureRandom secureRandom;
    private final boolean secure;

    private final Map<String, Long> invalid = new HashMap<>();

    private long lastClean = System.currentTimeMillis();

    public FormValidator(String secretKey, boolean https) {
        this.secretKey = secretKey;
        secure = https;
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private Pair<String, String> generateCsrfToken() {
        String token = Long.toHexString(secureRandom.nextLong()) + "." + Long.toHexString(System.currentTimeMillis());
        String hash = Hashlib.hmacSha512(token, secretKey);
        return new Pair<>(token, hash);
    }

    public boolean isValid(WebRequest request, String csrfToken, long timeout) {
        try {
            clean();
            String hash = request.getHeaders().getCookies().get("csrf");
            if (hash == null || csrfToken == null || !csrfToken.contains(".")) {
                return false;
            }
            synchronized (invalid) {
                if (invalid.containsKey(hash)) {
                    return false;
                }
                String[] splitted = csrfToken.split("\\.");
                if (splitted.length != 2) {
                    return false;
                }
                long timestamp;
                try {
                    timestamp = Long.valueOf(splitted[1], 16);
                    if (timestamp + timeout < System.currentTimeMillis()) {
                        return false;
                    }
                } catch (NumberFormatException ignored) {
                    return false;
                }
                boolean valid = hash.equals(Hashlib.hmacSha512(csrfToken, secretKey));
                if (valid) {
                    invalid.put(hash, timestamp + timeout);
                }
                return valid;
            }
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    public String generate(WebResponse.Builder response) {
        clean();
        Pair<String, String> tokenPair = generateCsrfToken();
        response.addCookie(new Cookie("csrf", tokenPair.second()), null, null, null, null, secure, true, Cookie.SameSite.LAX);
        return tokenPair.first();
    }

    private void clean() {
        if (lastClean + 10000 < System.currentTimeMillis()) {
            synchronized (invalid) {
                Set<String> keys = invalid.entrySet().stream().filter(i -> i.getValue() < System.currentTimeMillis()).map(Map.Entry::getKey).collect(Collectors.toSet());
                for (String i : keys) {
                    invalid.remove(i);
                }
            }
            lastClean = System.currentTimeMillis();
        }
    }

}
