package com.seafile.seandroid2.crypt;

import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

/**
 * Provides crypt logic.<br/>
 * <p><b>Note:</b> {@link Session} instance must have correct {@link Session#SEED} value in order to successfully encrypt/decrypt data.<p/>
 * {@link CryptException} thrown when wrong SEED used
 *
 * @author novic_dev
 */
public class CryptHelper {

    private static final String TAG = "CryptService";
    private static final String PRNG_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String CRYPT_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int AES_KEY_LENGTH = 256;
    private static final int ITERATIONS = 1;
    
    /**
     * For testing only!!!
     */
    private static final String SALT = "0123456789abcdef";
    private static final String SEED = "potato-tomato";

    /**
     * Encrypt byte array<br/>
     * <b>Note:</b> {@link Session} instance must have correct {@link Session#SEED} value in order to successfully encrypt/decrypt data.
     *
     * @param bytes bytes to encode
     * @return encoded bytes
     */
    public byte[] encrypt(byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKeySpec(), getParamSpec(cipher));
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            throw new CryptException(e);
        }
    }

    /**
     * Decrypt byte array<br/>
     * <b>Note:</b> {@link Session} instance must have correct {@link Session#SEED} value in order to successfully encrypt/decrypt data.
     *
     * @param bytes bytes to decode
     * @return decoded bytes
     */
    public byte[] decrypt(byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKeySpec(), getParamSpec(cipher));
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            throw new CryptException(e);
        }
    }

    public String encryptString(String entity) {
        byte[] encryptedArray = encrypt(entity.getBytes());
        return Base64.encodeToString(encryptedArray, Base64.DEFAULT);
    }

    public String decryptString(String encodedString) {
        byte[] encryptedArray = Base64.decode(encodedString, Base64.DEFAULT);
        return new String(decrypt(encryptedArray));
    }

    private SecretKeySpec getKeySpec() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String seed = (String) this.SEED;

        if (seed == null || seed.isEmpty()) {
            throw new IllegalStateException("Seed is empty");
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance(PRNG_ALGORITHM);
        SecretKey secretKey = factory.generateSecret(new PBEKeySpec(seed.toCharArray(), SALT.getBytes(), ITERATIONS, AES_KEY_LENGTH));
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    private AlgorithmParameterSpec getParamSpec(Cipher cipher) throws NoSuchAlgorithmException {
        byte[] iv = new byte[] {-12, 23, 124, 7, 111, -114, -69, -37, -54, 111, -32, -47, -34, -40, -23, -4};
//        DBOApplication.getInstance().getLocalStorage().getBytes(LocalStorage.IV);
//        if (iv == null) {
//            iv = new byte[cipher.getBlockSize()];
//            SecureRandom.getInstance("SHA1PRNG").nextBytes(iv);
//            DBOApplication.getInstance().getLocalStorage().saveBytes(LocalStorage.IV, iv);
//        }

        return new IvParameterSpec(iv);
    }

    public static class CryptException extends RuntimeException {

        public CryptException(Throwable throwable) {
            super(throwable);
        }
    }
}
