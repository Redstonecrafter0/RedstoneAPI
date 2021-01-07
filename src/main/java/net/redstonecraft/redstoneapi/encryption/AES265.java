package net.redstonecraft.redstoneapi.encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;

public class AES265 {

    private final Cipher encryptCipher;
    private final Cipher decryptCipher;
    private final byte[] iv;

    public AES265(String password, byte[] salt) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factoryTmp = null;
        try {
            factoryTmp = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SecretKeyFactory factory = factoryTmp;
        SecretKey secretTmp = null;
        try {
            secretTmp = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SecretKey secret = secretTmp;
        Cipher encryptCipherTmp = null;
        try {
            encryptCipherTmp = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception e) {
            e.printStackTrace();
        }
        encryptCipher = encryptCipherTmp;
        byte[] ivTmp = null;
        try {
            encryptCipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = encryptCipher.getParameters();
            ivTmp = params.getParameterSpec(IvParameterSpec.class).getIV();
        } catch (Exception e) {
            e.printStackTrace();
        }
        iv = ivTmp;
        Cipher decryptCipherTmp = null;
        try {
            decryptCipherTmp = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception e) {
            e.printStackTrace();
        }
        decryptCipher = decryptCipherTmp;
        try {
            decryptCipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] encrypt(String message) {
        try {
            return encryptCipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(byte[] cipher) {
        try {
            return new String(decryptCipher.doFinal(cipher), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] generateSalt() {
        return new SecureRandom().generateSeed(8);
    }

    public static void setupPolicy() {
        Security.setProperty("crypto.policy", "unlimited");
    }

}
