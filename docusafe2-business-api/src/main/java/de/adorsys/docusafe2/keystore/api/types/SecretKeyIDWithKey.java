package de.adorsys.docusafe2.keystore.api.types;

import org.adorsys.cryptoutils.utils.HexUtil;

import javax.crypto.SecretKey;

public class SecretKeyIDWithKey {
    private KeyID keyID;
    private SecretKey secretKey;

    public SecretKeyIDWithKey(KeyID keyID, SecretKey secretKey) {
        this.keyID = keyID;
        this.secretKey = secretKey;
    }

    public KeyID getKeyID() {
        return keyID;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    @Override
    public String toString() {
        return "SecretKeyIDWithKey{" +
                "keyID=" + keyID +
                ", secretKey.algorithm = " + secretKey.getAlgorithm() +
                ", secretKey.format = " + secretKey.getFormat() +
                ", secretKey.encoded = " + HexUtil.convertBytesToHexString(secretKey.getEncoded()) +
                '}';
    }
}
