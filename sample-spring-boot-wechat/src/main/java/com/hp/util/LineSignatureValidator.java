package com.hp.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import lombok.NonNull;
import lombok.ToString;

/*
 * This class validates value of the `X-LINE-Signature` header.
 */
@ToString
public class LineSignatureValidator {

    private static final String HASH_ALGORITHM = "HmacSHA256";
    private final SecretKeySpec secretKeySpec;
    private String channelToken;

    public void setChannelToken(String channelToken) {
        this.channelToken = channelToken;
    }

    /**
     * Create new instance with channel secret.
     */
    public LineSignatureValidator(byte[] channelSecret,String channelToken) {
        this.secretKeySpec = new SecretKeySpec(channelSecret, HASH_ALGORITHM);
        this.channelToken = channelToken;
    }

    /**
     * Validate signature.
     *
     * @param content Body of the http request in byte array.
     * @param headerSignature Signature value from `X-LINE-Signature` HTTP header
     * @return True if headerSignature matches signature of the content. False otherwise.
     */
    public boolean validateSignature(@NonNull byte[] content, @NonNull String headerSignature) {
        final byte[] signature = generateSignature(content);
        final byte[] decodeHeaderSignature = Base64.getDecoder().decode(headerSignature);
        return MessageDigest.isEqual(decodeHeaderSignature, signature);
    }

    /**
     * Validate authorization.
     * @param authorization authorization value from 'Authorization' HTTP header
     * @return True
     */
    public boolean validateAuthorization(@NonNull String authorization){
        return ("Bearer " + this.channelToken).equals(authorization);
    }

    /**
     * Generate signature value.
     *
     * @param content Body of the http request.
     * @return generated signature value.
     */
    public byte[] generateSignature(@NonNull byte[] content) {
        try {
            Mac mac = Mac.getInstance(HASH_ALGORITHM);
            mac.init(secretKeySpec);
            return mac.doFinal(content);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            // "HmacSHA256" is always supported in Java 8 platform.
            //   (see https://docs.oracle.com/javase/8/docs/api/javax/crypto/Mac.html)
            // All valid-SecretKeySpec-instance are not InvalidKey.
            //   (because the key for HmacSHA256 can be of any length. see RFC2104)
            throw new IllegalStateException(e);
        }
    }

    public String generateSignatureStr(@NonNull byte[] content){
        final byte[] signature = generateSignature(content);
        return Base64.getEncoder().encodeToString(signature);
    }
}
