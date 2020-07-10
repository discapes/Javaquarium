package com.javaquarium.backend.services;

import com.firework.Service;

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

@Service
public class CryptographyService {
    private final Charset charset = StandardCharsets.UTF_8;
    private final Random random = new SecureRandom();
    private Cipher cipher;
    private MessageDigest digest;

    public CryptographyService() {
        try {
            cipher = Cipher.getInstance("AES");
            digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception ignored) {}
        /* impossible exceptions */
    }

    /**
     * Encrypts a string with a key using AES encryption.
     *
     * @param msg the message to be encrypted.
     * @param key the key that the message will be encrypted with.
     * @return A Base64-encoded string of the encrypted message.
     */
    public String encrypt(String msg, String key) {
        digest.reset();
        byte[] msgBytes = msg.getBytes(charset);
        byte[] keyBytes = digest.digest(key.getBytes(charset));
        byte[] data;
        try {
            data = crypt(msgBytes, keyBytes, Cipher.ENCRYPT_MODE);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace(); /* this should never happen */
            return null;
        }
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Attempts to decrypt an AES-encrypted message with a key.
     *
     * @param msg the encrypted message.
     * @param key the key that the message was encrypted with.
     * @return The just-decrypted message as a string.
     * @throws BadPaddingException       the key is incorrect or the data is invalid.
     * @throws IllegalBlockSizeException the key is incorrect or the data is invalid.
     */
    public String decrypt(String msg, String key) throws BadPaddingException, IllegalBlockSizeException {
        digest.reset();
        byte[] msgBytes = Base64.getDecoder().decode(msg);
        byte[] keyBytes = digest.digest(key.getBytes(charset));
        byte[] data;
        try {
            data = crypt(msgBytes, keyBytes, Cipher.DECRYPT_MODE);
        } catch (InvalidKeyException e) {
            e.printStackTrace(); /* Should never happen */
            return null;
        }
        return new String(data, charset);
    }

    private byte[] crypt(byte[] msg, byte[] key, int opmode) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Key aesKey = new SecretKeySpec(key, "AES");
        cipher.init(opmode, aesKey);
        return cipher.doFinal(msg);
    }

    /**
     * Salts and hashes a password.
     *
     * @param pwd the password to be hashed.
     * @return A string containing the salt and the hash in base64 separated by a space.
     */
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

    /**
     * Checks if a password is correct in relation to a salted hash.
     *
     * @param saltAndHash a string containing the salt and the hash in base64 separated by a space.
     * @param pwd         the password it compares the hash to.
     * @return If the password is correct.
     */
    public boolean testPassword(String saltAndHash, String pwd) {
        digest.reset();
        String[] parts = saltAndHash.split("\\s+");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
        digest.update(salt);
        byte[] actualHash = digest.digest(pwd.getBytes(charset));
        return Arrays.equals(expectedHash, actualHash);
    }
}
