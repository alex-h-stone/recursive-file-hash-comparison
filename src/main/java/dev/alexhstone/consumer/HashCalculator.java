package dev.alexhstone.consumer;

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
    private static final String CANNOT_CALCULATE_HASH_FOR_A_DIRECTORY = "[Cannot calculate hash for a directory]";

    public String calculateHashFor(String string) {
        MessageDigest hashAlgorithm = HASH_ALGORITHM.getAlgorithm();

        byte[] hashAsBytes = hashAlgorithm.digest(string.getBytes());

        return convertToString(hashAsBytes);
    }

    public String calculateHashFor(File file) {
        if (file.isDirectory()) {
            log.debug("Generating empty hash for [{}] as it is a directory and NOT a file",
                    file.getAbsolutePath());
            return CANNOT_CALCULATE_HASH_FOR_A_DIRECTORY;
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

        MessageDigest messageDigest = calculateMessageDigestFor(file);
        byte[] digested = messageDigest.digest();

        log.debug("Completed calculating {} hash for: [{}]",
                HASH_ALGORITHM, file.getAbsolutePath());

        return digested;
    }

    private MessageDigest calculateMessageDigestFor(File file) {
        try {
            InputStream fileInputStream = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount;

            MessageDigest messageDigest = HASH_ALGORITHM.getAlgorithm();
            while ((bytesCount = fileInputStream.read(byteArray)) != -1) {
                messageDigest.update(byteArray, 0, bytesCount);
            }
            fileInputStream.close();

            return messageDigest;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAlgorithmName() {
        return HASH_ALGORITHM.name();
    }
}