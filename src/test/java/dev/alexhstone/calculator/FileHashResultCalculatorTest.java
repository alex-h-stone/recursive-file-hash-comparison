package dev.alexhstone.calculator;

import dev.alexhstone.model.FileHashResult;
import dev.alexhstone.model.HashDetails;
import dev.alexhstone.test.util.FileSystemUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;


class FileHashResultCalculatorTest {

    @TempDir
    private Path temporaryDirectory;

    private FileSystemUtils fileSystemUtils;
    private FileHashResultCalculator hashGenerator;

    @BeforeEach
    void setUp() {
        fileSystemUtils = new FileSystemUtils(temporaryDirectory);
        hashGenerator = new FileHashResultCalculator(new HashDetailsCalculator());
    }

    @Test
    void shouldCreateFullyPopulatedFileHashResultForFileThatExists() {
        File existingFile = fileSystemUtils.createFileWithContent("existingFile.txt", "Some test file contents");

        FileHashResult actualFileHashResult = hashGenerator.process(temporaryDirectory.toAbsolutePath(),
                existingFile);
        HashDetails actualHashDetails = actualFileHashResult.getHashDetails();

        Assertions.assertAll(
                "Grouped Assertions of FileHashResult",
                () -> Assertions.assertEquals("existingFile.txt", actualFileHashResult.getFileName()),
                () -> Assertions.assertEquals("", actualFileHashResult.getRelativePathToFile()),
                () -> MatcherAssert.assertThat(actualFileHashResult.getAbsolutePathToFile(), Matchers.containsString("existingFile.txt")),
                () -> Assertions.assertEquals(BigInteger.valueOf(23), actualFileHashResult.getFileSizeInBytes()),
                () -> Assertions.assertEquals("23 bytes", actualFileHashResult.getFileSize()),
                () -> Assertions.assertEquals("SHA256", actualHashDetails.getHashingAlgorithmName()),
                () -> Assertions.assertEquals("224ff5a028e147b555f07f3e833950acb250baa121c3cc742fc390f5fd5ff9ec", actualHashDetails.getHashValue())
        );
    }

    @Test
    void shouldCreateFullyPopulatedFileHashResultForFileInSubdirectory() {
        Path parentDirectory = fileSystemUtils.createDirectory("parentDirectory");
        Path childDirectory = new FileSystemUtils(parentDirectory).createDirectory("childDirectory");

        File existingFile = new FileSystemUtils(childDirectory).createFileWithContent("existingFile.txt", "Some test file contents");

        FileHashResult actualFileHashResult = hashGenerator.process(temporaryDirectory.toAbsolutePath(),
                existingFile);
        HashDetails actualHashDetails = actualFileHashResult.getHashDetails();

        Assertions.assertAll(
                "Grouped Assertions of FileHashResult",
                () -> Assertions.assertEquals("existingFile.txt", actualFileHashResult.getFileName()),
                () -> Assertions.assertEquals("parentDirectory\\childDirectory", actualFileHashResult.getRelativePathToFile()),
                () -> MatcherAssert.assertThat(actualFileHashResult.getAbsolutePathToFile(), Matchers.containsString("existingFile.txt")),
                () -> Assertions.assertEquals(BigInteger.valueOf(23), actualFileHashResult.getFileSizeInBytes()),
                () -> Assertions.assertEquals("23 bytes", actualFileHashResult.getFileSize()),
                () -> Assertions.assertEquals("SHA256", actualHashDetails.getHashingAlgorithmName()),
                () -> Assertions.assertEquals("224ff5a028e147b555f07f3e833950acb250baa121c3cc742fc390f5fd5ff9ec", actualHashDetails.getHashValue())
        );
    }
}