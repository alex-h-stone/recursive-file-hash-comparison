package dev.alexhstone.calculator;

import dev.alexhstone.exception.InvalidFileException;
import dev.alexhstone.util.HashAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

@Slf4j
class HashCalculator {

    private static final HashAlgorithm SHA256_HASH = HashAlgorithm.SHA256;

    String calculateHashFor(File file) {
        if (file.isDirectory()) {
            String message = "Expected [%s] to be a file, but was actually a directory"
                    .formatted(file.getAbsolutePath());
            throw new InvalidFileException(message);
        }

        byte[] hashAsBytes = calculateHashOf(file, SHA256_HASH);
        return convertToString(hashAsBytes);
    }

    private String convertToString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

    private byte[] calculateHashOf(File file, HashAlgorithm hashAlgorithm) {
        log.info("About to calculate {} hash for: {}",
                hashAlgorithm.getAlgorithmName(),
                file.getAbsolutePath());
        try {
            InputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount;

            MessageDigest messageDigest = hashAlgorithm.getAlgorithm();
            while ((bytesCount = fis.read(byteArray)) != -1) {
                messageDigest.update(byteArray, 0, bytesCount);
            }

            fis.close();

            byte[] digested = messageDigest.digest();
            log.info("Completed calculating hash for: " + file.getAbsolutePath());
            return digested;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getAlgorithmName() {
        return SHA256_HASH.name();
    }
}