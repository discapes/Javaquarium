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
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Cryptographer {

    private final Charset charset = StandardCharsets.US_ASCII;
    MessageDigest digest;
    private Cipher cipher;

    public Cryptographer() {
        try {
            cipher = Cipher.getInstance("AES");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error initialising cipher.");
        }
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String base64Encrypt(String strMsg, String strKey) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        byte[] msg = strMsg.getBytes(charset);
        byte[] key = digest.digest(strKey.getBytes(charset));
        byte[] data;
        data = encrypt(msg, key);
        return Base64.getEncoder().encodeToString(data);
    }

    public String base64Decrypt(String encryptedMsg, String strKey) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMsg);
        byte[] key = digest.digest(strKey.getBytes(charset));
        byte[] data;
        data = decrypt(encryptedBytes, key);
        return new String(data, charset);
    }

    private byte[] encrypt(byte[] msg, byte[] key) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Key aesKey = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        return cipher.doFinal(msg);
    }

    private byte[] decrypt(byte[] encrypted, byte[] key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Key aesKey = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        return cipher.doFinal(encrypted);
    }

}
