package org.backup.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public enum HashAlgorithm {
    SHA256("SHA-256");

    private final String digestAlgorithmName;

    HashAlgorithm(String digestAlgorithmName) {
        this.digestAlgorithmName = digestAlgorithmName;
    }

    public MessageDigest getMessageDigestAlgorithm() {
        try {
            // TODO consider initialise at startup, 1 instance?
            // verify thread safety?
            return MessageDigest.getInstance(digestAlgorithmName);
        } catch (NoSuchAlgorithmException e) {
            String message = "Unable to create instance of the algorithm [%s] because of: %s"
                    .formatted(digestAlgorithmName, e.getMessage());
            throw new RuntimeException(message, e);
        }
    }
}
