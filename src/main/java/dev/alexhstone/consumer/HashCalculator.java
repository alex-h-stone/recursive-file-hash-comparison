package dev.alexhstone.consumer;

import dev.alexhstone.exception.InvalidFileException;
import dev.alexhstone.util.HashAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

@Slf4j
public class HashCalculator {

    private static final HashAlgorithm HASH_ALGORITHM = HashAlgorithm.SHA256;

    public String calculateHashFor(String string) {
        MessageDigest hashAlgorithm = HASH_ALGORITHM.getAlgorithm();

        byte[] hashAsBytes = hashAlgorithm.digest(string.getBytes());

        return convertToString(hashAsBytes);
    }

    public String calculateHashFor(File file) {
        if (file.isDirectory()) {
            String message = "Expected [%s] to be a file, but was actually a directory"
                    .formatted(file.getAbsolutePath());
            throw new InvalidFileException(message);
        }

        byte[] hashAsBytes = calculateHashOf(file);
        return convertToString(hashAsBytes);
    }

    private String convertToString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

    private byte[] calculateHashOf(File file) {
        log.debug("About to calculate the {} hash for: [{}]",
                HASH_ALGORITHM,
                file.getAbsolutePath());
        try {
            InputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount;

            MessageDigest messageDigest = HASH_ALGORITHM.getAlgorithm();
            while ((bytesCount = fis.read(byteArray)) != -1) {
                messageDigest.update(byteArray, 0, bytesCount);
            }

            fis.close();

            byte[] digested = messageDigest.digest();
            log.debug("Completed calculating {} hash for: [{}]",
                    HASH_ALGORITHM, file.getAbsolutePath());
            return digested;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getAlgorithmName() {
        return HASH_ALGORITHM.name();
    }
}