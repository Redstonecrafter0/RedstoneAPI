package net.redstonecraft.redstoneapi.core;

import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Shortcuts for hashing
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class Hashlib {

    public static String sha256(String str) {
        return sha256(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha256(byte[] arr) {
        return hash("SHA-256", arr);
    }

    public static String md5(String str) {
        return md5(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String md5(byte[] arr) {
        return hash("MD5", arr);
    }

    public static String sha1(String str) {
        return sha1(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha1(byte[] arr) {
        return hash("SHA-1", arr);
    }

    public static String sha384(String str) {
        return sha384(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha384(byte[] arr) {
        return hash("SHA-384", arr);
    }

    public static String sha512(String str) {
        return sha512(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha512(byte[] arr) {
        return hash("SHA-512", arr);
    }

    public static String hmacSha512(String str, String key) {
        return hmacSha512(str.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
    }

    public static String hmacSha512(byte[] arr, byte[] key) {
        return hmac("HmacSHA512", arr, key);
    }

    private static String hash(String algorithm, byte[] arr) {
        return bytesToHex(hash_raw(algorithm, arr));
    }

    private static String hmac(String algorithm, byte[] arr, byte[] key) {
        return bytesToHex(hmac_raw(algorithm, arr, key));
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte i : hash) {
            String hex = Integer.toHexString(0xff & i);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] sha256_raw(String str) {
        return sha256_raw(str.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] sha256_raw(byte[] arr) {
        return hash_raw("SHA-256", arr);
    }

    public static byte[] md5_raw(String str) {
        return md5_raw(str.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] md5_raw(byte[] arr) {
        return hash_raw("MD5", arr);
    }

    public static byte[] sha1_raw(String str) {
        return sha1_raw(str.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] sha1_raw(byte[] arr) {
        return hash_raw("SHA-1", arr);
    }

    public static byte[] sha384_raw(String str) {
        return sha384_raw(str.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] sha384_raw(byte[] arr) {
        return hash_raw("SHA-384", arr);
    }

    public static byte[] sha512_raw(String str) {
        return sha512_raw(str.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] sha512_raw(byte[] arr) {
        return hash_raw("SHA-512", arr);
    }

    public static byte[] hmacSha512_raw(String str, String key) {
        return hmacSha512_raw(str.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] hmacSha512_raw(byte[] arr, byte[] key) {
        return hmac_raw("HmacSHA512", arr, key);
    }

    private static byte[] hash_raw(String algorithm, byte[] arr) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.reset();
            md.update(arr);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static byte[] hmac_raw(String algorithm, byte[] arr, byte[] key) {
        try {
            Mac hmac = Mac.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
            hmac.init(keySpec);
            return hmac.doFinal(arr);
        } catch (NoSuchAlgorithmException | InvalidKeyException ignored) {
        }
        return null;
    }

    public static String bcrypt_hash(String str) {
        return BCrypt.hashpw(str, "");
    }

    public static String bcrypt_hash(String str, String salt) {
        return BCrypt.hashpw(str, salt);
    }

    public static boolean bcrypt_check(String str, String hash) {
        return BCrypt.checkpw(str, hash);
    }

}
