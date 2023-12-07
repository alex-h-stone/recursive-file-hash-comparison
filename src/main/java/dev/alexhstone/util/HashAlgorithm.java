package dev.alexhstone.util;


import lombok.Getter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Getter
public enum HashAlgorithm {
    SHA256("SHA-256");

    private final String algorithmName;
    private final MessageDigest algorithm;

    HashAlgorithm(String algorithmName) {
        this.algorithmName = algorithmName;
        this.algorithm = createMessageDigestAlgorithm(algorithmName);
    }

    private static MessageDigest createMessageDigestAlgorithm(String algorithmName) {
        try {
            return MessageDigest.getInstance(algorithmName);
        } catch (NoSuchAlgorithmException e) {
            String message = "Unable to create instance of the algorithm [%s] because of: %s"
                    .formatted(algorithmName, e.getMessage());
            throw new RuntimeException(message, e);
        }
    }
}
