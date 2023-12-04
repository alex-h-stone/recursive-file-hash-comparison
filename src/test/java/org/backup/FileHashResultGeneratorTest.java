package org.backup;

import org.backup.model.FileHashResult;
import org.backup.model.HashDetails;
import org.backup.test.util.FileCreator;
import org.backup.util.HashCalculator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileHashResultGeneratorTest {

    @TempDir
    private Path temporaryDirectory;

    private FileHashResultGenerator resultGenerator;
    private FileCreator fileCreator;

    @BeforeEach
    void setUp() {
        fileCreator = new FileCreator(temporaryDirectory);

        HashCalculator hashCalculator = new HashCalculator();
        resultGenerator = new FileHashResultGenerator(hashCalculator);
    }

    @Test
    void shouldCreateFullyPopulatedFileHashResultForFileThatExists() {
        File existingFile = createFileWithContent("existingFile.txt", "Some test file contents");

        FileHashResult actualFileHashResult = resultGenerator.process(existingFile);
        HashDetails actualHashDetails = actualFileHashResult.getHashDetails();

        Assertions.assertAll(
                "Grouped Assertions of FileHashResult",
                () -> assertEquals("existingFile.txt", actualFileHashResult.getFileName()),
                () -> assertThat(actualFileHashResult.getAbsolutePath(), Matchers.containsString("existingFile.txt")),
                () -> assertEquals(BigInteger.valueOf(23), actualFileHashResult.getFileSizeInBytes()),
                () -> assertEquals("23 bytes", actualFileHashResult.getFileSize()),
                () -> assertEquals("SHA256", actualHashDetails.getHashingAlgorithmName()),
                () -> assertEquals("224ff5a028e147b555f07f3e833950acb250baa121c3cc742fc390f5fd5ff9ec", actualHashDetails.getHashValue())
        );
    }

    private File createFileWithContent(String fileName, String content) {
        return fileCreator.createFileWithContent(fileName, content);
    }
}