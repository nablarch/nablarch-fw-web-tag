package nablarch.common.web.hiddenencryption;

import java.io.Serializable;

import nablarch.common.encryption.Encryptor;

/**
 * @author Kiyohito Itoh
 */
public class SampleEncryptor implements Encryptor<Serializable> {

    /**
     * {@inheritDoc}
     */
    public byte[] decrypt(Serializable context, byte[] src) throws IllegalArgumentException {
        return src;
    }

    /**
     * {@inheritDoc}
     */
    public byte[] encrypt(Serializable context, byte[] src) throws IllegalArgumentException {
        return src;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable generateContext() {
        return "test";
    }
}
