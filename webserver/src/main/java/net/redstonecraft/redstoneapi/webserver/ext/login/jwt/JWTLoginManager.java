package net.redstonecraft.redstoneapi.webserver.ext.login.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import net.redstonecraft.redstoneapi.core.Hashlib;

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
import java.util.Map;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class JWTLoginManager {

    private final Algorithm algorithm;

    public JWTLoginManager(String secretKey) {
        this(createRSA(secretKey));
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
    public JWTLoginManager(File publicKey, File privateKey) throws IOException, InvalidKeySpecException {
        this(Algorithm.RSA512(readPublicKey(publicKey), readPrivateKey(privateKey)));
    }

    public JWTLoginManager(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String loginUser(long userId, Map<String, String> userdata, Date expiresAt) {
        return JWT.create().withClaim("uid", userId).withClaim("userdata", userdata).withExpiresAt(expiresAt).sign(algorithm);
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
