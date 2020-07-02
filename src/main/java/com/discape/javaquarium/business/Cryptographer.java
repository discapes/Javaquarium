package com.discape.javaquarium.business;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class Cryptographer {

    private final Charset charset = StandardCharsets.UTF_8;
    private final Random random = new SecureRandom();
    private Cipher cipher;
    private MessageDigest digest;

    public Cryptographer() {
        try {
            cipher = Cipher.getInstance("AES");
            digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception ignored) {}
    }

    public String encrypt(String msg, String key) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        digest.reset();
        byte[] msgBytes = msg.getBytes(charset);
        byte[] keyBytes = digest.digest(key.getBytes(charset));
        byte[] data = crypt(msgBytes, keyBytes, Cipher.ENCRYPT_MODE);
        return Base64.getEncoder().encodeToString(data);
    }

    public String decrypt(String msg, String key) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        digest.reset();
        byte[] msgBytes = Base64.getDecoder().decode(msg);
        byte[] keyBytes = digest.digest(key.getBytes(charset));
        byte[] data = crypt(msgBytes, keyBytes, Cipher.DECRYPT_MODE);
        return new String(data, charset);
    }

    private byte[] crypt(byte[] msg, byte[] key, int opmode) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Key aesKey = new SecretKeySpec(key, "AES");
        cipher.init(opmode, aesKey);
        return cipher.doFinal(msg);
    }

    public String saltHashPassword(String pwd) {
        digest.reset();
        byte[] salt = new byte[20];
        random.nextBytes(salt);
        digest.update(salt);
        byte[] hash = digest.digest(pwd.getBytes(charset));
        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hashB64 = Base64.getEncoder().encodeToString(hash);
        return saltB64 + " " + hashB64;
    }

    public boolean testPassword(String saltHash, String pwd) {
        digest.reset();
        String[] parts = saltHash.split("\\s+");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
        digest.update(salt);
        byte[] actualHash = digest.digest(pwd.getBytes(charset));
        return Arrays.equals(expectedHash, actualHash);
    }
}