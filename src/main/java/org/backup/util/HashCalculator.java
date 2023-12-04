package org.backup.util;

import lombok.extern.slf4j.Slf4j;
import org.backup.exception.InvalidFileException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

@Slf4j
public class HashCalculator {

    private static final HashAlgorithm HASH_ALGORITHM = HashAlgorithm.SHA256;

    public String calculateHashFor(File file) {
        if (file.isDirectory()) {
            String message = "Expected [%s] to be a file, but was actually a directory"
                    .formatted(file.getAbsolutePath());
            throw new InvalidFileException(message);
        }

        MessageDigest sha256Algorithm = HASH_ALGORITHM.getMessageDigestAlgorithm();
        byte[] hashAsBytes = calculateHashOf(file, sha256Algorithm);
        return convertToString(hashAsBytes);
    }

    private static String convertToString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

    private byte[] calculateHashOf(File file, MessageDigest sha256Algorithm) {
        InputStream fis = null;
        log.info("About to calculate hash for: " + file.getAbsolutePath());
        try {
            fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;

            while ((bytesCount = fis.read(byteArray)) != -1) {
                sha256Algorithm.update(byteArray, 0, bytesCount);
            }

            fis.close();

            byte[] digested = sha256Algorithm.digest();
            log.info("Completed calculating hash for: " + file.getAbsolutePath());
            return digested;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getAlgorithmName() {
        return HASH_ALGORITHM.name();
    }
}