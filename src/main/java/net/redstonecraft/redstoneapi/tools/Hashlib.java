package net.redstonecraft.redstoneapi.tools;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Shortcuts for hashing
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class Hashlib {

    public static String sha265(String str) {
        return sha265(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha265(byte[] arr) {
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

    private static String hash(String algorithm, byte[] arr) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.reset();
            md.update(arr);
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
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

    public static byte[] sha265_raw(String str) {
        return sha265_raw(str.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] sha265_raw(byte[] arr) {
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
