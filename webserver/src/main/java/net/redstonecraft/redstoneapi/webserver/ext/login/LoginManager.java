package net.redstonecraft.redstoneapi.webserver.ext.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import net.redstonecraft.redstoneapi.core.tools.Hashlib;
import net.redstonecraft.redstoneapi.core.tuple.Pair;
import net.redstonecraft.redstoneapi.webserver.WebRequest;
import net.redstonecraft.redstoneapi.webserver.obj.Cookie;
import net.redstonecraft.redstoneapi.webserver.obj.WebResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class LoginManager<T extends User> {

    private final Algorithm algorithm;
    private final UserProvider<T> userProvider;
    private final String domain;
    private final boolean httpsOnly;
    private final JWTVerifier verifier;

    public LoginManager(String secretKey, UserProvider<T> userProvider, String domain, boolean httpsOnly) {
        this(createRSA(secretKey), userProvider, domain, httpsOnly);
    }

    /**
     * Generate a private key using <b>openssl genrsa -out private_key.pem 2048</b>.<br>
     * Convert the private key to PKCS#8 format <b>openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt</b>.<br>
     * Create the public key from the private key using <b>openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der</b>.
     *
     * @param publicKey The public key file in DER format
     * @param privateKey The private key file in PKCS#8 format
     * @throws IOException if an I/O exception occurs
     * @throws InvalidKeySpecException if one of the keys is invalid
     */
    public LoginManager(File publicKey, File privateKey, UserProvider<T> userProvider, String domain, boolean httpsOnly) throws IOException, InvalidKeySpecException {
        this(Algorithm.RSA512(readPublicKey(publicKey), readPrivateKey(privateKey)), userProvider, domain, httpsOnly);
    }

    public LoginManager(Algorithm algorithm, UserProvider<T> userProvider, String domain, boolean httpsOnly) {
        this.algorithm = algorithm;
        this.userProvider = userProvider;
        this.domain = "".equals(domain) ? null : domain;
        this.httpsOnly = httpsOnly;
        verifier = JWT.require(algorithm).withIssuer("redstoneapi").build();
    }

    public void loginUser(String username, String password, Date expiresAt, WebResponse.Builder response) {
        User user = userProvider.login(username, password);
        updateUserRefreshToken(user, expiresAt, response);
    }

    public void updateUserRefreshToken(User user, Date expiresAt, WebResponse.Builder response) {
        if (user != null) {
            response.addCookie(new Cookie("Jsessionid", JWT.create().withExpiresAt(expiresAt).withIssuer("redstoneapi").withClaim("uid", user.getId()).sign(algorithm)), expiresAt, null, domain, null, httpsOnly, true, Cookie.SameSite.LAX);
        }
    }

    public T getUserWithRefreshToken(WebRequest request) {
        String sessionId = request.getHeaders().getCookies().get("Jsessionid");
        if (sessionId == null) {
            return null;
        }
        try {
            DecodedJWT jwt = verifier.verify(sessionId);
            Date date = jwt.getExpiresAt();
            if (date == null) {
                return null;
            }
            if (new Date().after(date)) {
                return null;
            }
            String uid = jwt.getClaim("uid").asString();
            if (uid == null) {
                return null;
            }
            return userProvider.getUserFromUserId(uid);
        } catch (JWTVerificationException ignored) {
            return null;
        }
    }

    public String getAccessToken(User user, Map<String, String> userdata, Date expiresAt) {
        return JWT.create().withIssuer("redstoneapi").withClaim("uid", user.getId()).withClaim("userdata", userdata).withExpiresAt(expiresAt).sign(algorithm);
    }

    public Pair<T, Map<String, String>> decodeAccessToken(String accessToken) {
        try {
            DecodedJWT jwt = verifier.verify(accessToken);
            Date date = jwt.getExpiresAt();
            if (date == null) {
                return null;
            }
            if (new Date().after(date)) {
                return null;
            }
            String uid = jwt.getClaim("uid").asString();
            if (uid == null) {
                return null;
            }
            Map<String, String> map = new HashMap<>();
            Map<String, Object> body = jwt.getClaim("userdata").asMap();
            if (body != null) {
                for (Map.Entry<String, Object> i : body.entrySet()) {
                    if (i.getValue() instanceof String s) {
                        map.put(i.getKey(), s);
                    }
                }
            }
            return new Pair<>(userProvider.getUserFromUserId(uid), map);
        } catch (JWTVerificationException ignored) {
            return null;
        }
    }

    private static RSAPublicKey readPublicKey(File file) throws IOException, InvalidKeySpecException {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(Files.readAllBytes(file.toPath())));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static RSAPrivateKey readPrivateKey(File file) throws IOException, InvalidKeySpecException {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePublic(new PKCS8EncodedKeySpec(Files.readAllBytes(file.toPath())));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Algorithm createRSA(String secretKey) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, new SecureRandom(Hashlib.sha256_raw(secretKey)));
            KeyPair keyPair = generator.genKeyPair();
            return Algorithm.RSA512((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

}
