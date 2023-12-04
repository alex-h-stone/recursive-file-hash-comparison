package org.backup.util;

import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class HashAlgorithmTest {

    @Test
    void allHashAlgorithmsShouldBeNonNull() {
        Arrays.stream(HashAlgorithm.values()).forEach(hashAlgorithm -> {
            MessageDigest algorithm = hashAlgorithm.getMessageDigestAlgorithm();
            String message = "Expected  non-null hashAlgorithm for: [%s]"
                    .formatted(hashAlgorithm);
            assertNotNull(algorithm, message);
        });
    }
}