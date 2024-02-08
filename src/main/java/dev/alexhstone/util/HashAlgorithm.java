package dev.alexhstone.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum HashAlgorithm {
    SHA256("SHA-256");

    private final String algorithmName;

    HashAlgorithm(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public MessageDigest getAlgorithm() {
        return createMessageDigestAlgorithm(algorithmName);
    }

    private MessageDigest createMessageDigestAlgorithm(String algorithmName) {
        try {
            return MessageDigest.getInstance(algorithmName);
        } catch (NoSuchAlgorithmException e) {
            String message = "Unable to create instance of the algorithm [%s] because of: %s"
                    .formatted(algorithmName, e.getMessage());
            throw new RuntimeException(message, e);
        }
    }
}
