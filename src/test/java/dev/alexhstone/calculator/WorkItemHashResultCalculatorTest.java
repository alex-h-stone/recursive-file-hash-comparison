package dev.alexhstone.calculator;

import dev.alexhstone.model.HashDetails;
import dev.alexhstone.model.datastore.WorkItemHashResult;
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


class WorkItemHashResultCalculatorTest {

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

        WorkItemHashResult actualWorkItemHashResult = hashGenerator.process(temporaryDirectory.toAbsolutePath(),
                existingFile);
        HashDetails actualHashDetails = actualWorkItemHashResult.getHashDetails();

        Assertions.assertAll(
                "Grouped Assertions of WorkItemHashResult",
                () -> Assertions.assertEquals("existingFile.txt", actualWorkItemHashResult.getName()),
                () -> Assertions.assertEquals("", actualWorkItemHashResult.getRelativePath()),
                () -> MatcherAssert.assertThat(actualWorkItemHashResult.getAbsolutePath(), Matchers.containsString("existingFile.txt")),
                () -> Assertions.assertEquals(BigInteger.valueOf(23), actualWorkItemHashResult.getSizeInBytes()),
                () -> Assertions.assertEquals("23 bytes", actualWorkItemHashResult.getSize()),
                () -> Assertions.assertEquals("SHA256", actualHashDetails.getHashingAlgorithmName()),
                () -> Assertions.assertEquals("224ff5a028e147b555f07f3e833950acb250baa121c3cc742fc390f5fd5ff9ec", actualHashDetails.getHashValue())
        );
    }

    @Test
    void shouldCreateFullyPopulatedFileHashResultForFileInSubdirectory() {
        Path parentDirectory = fileSystemUtils.createDirectory("parentDirectory");
        Path childDirectory = new FileSystemUtils(parentDirectory).createDirectory("childDirectory");

        File existingFile = new FileSystemUtils(childDirectory).createFileWithContent("existingFile.txt", "Some test file contents");

        WorkItemHashResult actualWorkItemHashResult = hashGenerator.process(temporaryDirectory.toAbsolutePath(),
                existingFile);
        HashDetails actualHashDetails = actualWorkItemHashResult.getHashDetails();

        Assertions.assertAll(
                "Grouped Assertions of WorkItemHashResult",
                () -> Assertions.assertEquals("existingFile.txt", actualWorkItemHashResult.getName()),
                () -> Assertions.assertEquals("parentDirectory\\childDirectory", actualWorkItemHashResult.getRelativePath()),
                () -> MatcherAssert.assertThat(actualWorkItemHashResult.getAbsolutePath(), Matchers.containsString("existingFile.txt")),
                () -> Assertions.assertEquals(BigInteger.valueOf(23), actualWorkItemHashResult.getSizeInBytes()),
                () -> Assertions.assertEquals("23 bytes", actualWorkItemHashResult.getSize()),
                () -> Assertions.assertEquals("SHA256", actualHashDetails.getHashingAlgorithmName()),
                () -> Assertions.assertEquals("224ff5a028e147b555f07f3e833950acb250baa121c3cc742fc390f5fd5ff9ec", actualHashDetails.getHashValue())
        );
    }
}