package net.redstonecraft.redstoneapi.webserver.ext.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import net.redstonecraft.redstoneapi.core.Hashlib;
import net.redstonecraft.redstoneapi.core.Pair;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class LoginManager {

    private final Algorithm algorithm;
    private final UserProvider userProvider;

    public LoginManager(String secretKey, UserProvider userProvider) {
        this(createRSA(secretKey), userProvider);
    }

    private static Algorithm createRSA(String secretKey) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, new SecureRandom(Hashlib.sha265_raw(secretKey)));
            KeyPair keyPair = generator.genKeyPair();
            return Algorithm.RSA512((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public LoginManager(Algorithm algorithm, UserProvider userProvider) {
        this.algorithm = algorithm;
        this.userProvider = userProvider;
    }

    public String loginUser(User user, Date expiresAt) {
        return JWT.create().withClaim("uid", user.id()).withExpiresAt(expiresAt).sign(algorithm);
    }

}
