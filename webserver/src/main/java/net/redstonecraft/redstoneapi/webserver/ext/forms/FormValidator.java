package net.redstonecraft.redstoneapi.webserver.ext.forms;

import net.redstonecraft.redstoneapi.core.Hashlib;
import net.redstonecraft.redstoneapi.core.Pair;
import net.redstonecraft.redstoneapi.webserver.WebRequest;
import net.redstonecraft.redstoneapi.webserver.obj.Cookie;
import net.redstonecraft.redstoneapi.webserver.obj.SetCookieHeader;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class FormValidator {

    private final String secretKey;
    private final SecureRandom secureRandom;

    public FormValidator(String secretKey) {
        this.secretKey = secretKey;
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public Pair<String, String> generateCsrfToken() {
        String token = Long.toHexString(secureRandom.nextLong());
        String hash = Hashlib.hmacSha512(token, secretKey);
        return new Pair<>(token, hash);
    }

    public boolean validate(WebRequest request, String csrfToken) {
        String hash = request.getHeaders().getCookies().get("csrf");
        if (hash == null) {
            return false;
        }
        return hash.equals(Hashlib.hmacSha512(csrfToken, secretKey));
    }

    public String apply(WebResponse response) {
        Pair<String, String> tokenPair = generateCsrfToken();
        response.addHeader(new SetCookieHeader(new Cookie("csrf", tokenPair.getSecond()), null, null, null, null, false, true, SetCookieHeader.SameSite.NONE));
        return tokenPair.getFirst();
    }

}
