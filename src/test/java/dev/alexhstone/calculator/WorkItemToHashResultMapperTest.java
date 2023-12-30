package dev.alexhstone.calculator;

import dev.alexhstone.consumer.WorkItemToHashResultMapper;
import dev.alexhstone.model.datastore.HashResult;
import dev.alexhstone.model.queue.WorkItem;
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


class WorkItemToHashResultMapperTest {

    @TempDir
    private Path temporaryDirectory;

    private FileSystemUtils fileSystemUtils;
    private WorkItemToHashResultMapper hashGenerator;

    @BeforeEach
    void setUp() {
        fileSystemUtils = new FileSystemUtils(temporaryDirectory);
        hashGenerator = new WorkItemToHashResultMapper();
    }

    @Test
    void shouldCreateFullyPopulatedFileHashResultForFileThatExists() {
        File existingFile = fileSystemUtils.createFileWithContent("existingFile.txt", "Some test file contents");

        HashResult actualHashResult = hashGenerator.map(createWorkItem());

        Assertions.assertAll(
                "Grouped Assertions of HashResult",
                () -> Assertions.assertEquals("existingFile.txt", actualHashResult.getName()),
                () -> Assertions.assertEquals("", actualHashResult.getRelativePath()),
                () -> MatcherAssert.assertThat(actualHashResult.getAbsolutePath(), Matchers.containsString("existingFile.txt")),
                () -> Assertions.assertEquals(BigInteger.valueOf(23), actualHashResult.getSizeInBytes()),
                () -> Assertions.assertEquals("23 bytes", actualHashResult.getSize()),
                () -> Assertions.assertEquals("SHA256", actualHashResult.getHashingAlgorithmName()),
                () -> Assertions.assertEquals("224ff5a028e147b555f07f3e833950acb250baa121c3cc742fc390f5fd5ff9ec",
                        actualHashResult.getHashValue())
        );
    }

    @Test
    void shouldCreateFullyPopulatedFileHashResultForFileInSubdirectory() {
        Path parentDirectory = fileSystemUtils.createDirectory("parentDirectory");
        Path childDirectory = new FileSystemUtils(parentDirectory).createDirectory("childDirectory");

        File existingFile = new FileSystemUtils(childDirectory).createFileWithContent("existingFile.txt", "Some test file contents");

        HashResult actualHashResult = hashGenerator.map(createWorkItem());

        Assertions.assertAll(
                "Grouped Assertions of HashResult",
                () -> Assertions.assertEquals("existingFile.txt", actualHashResult.getName()),
                () -> Assertions.assertEquals("parentDirectory\\childDirectory", actualHashResult.getRelativePath()),
                () -> MatcherAssert.assertThat(actualHashResult.getAbsolutePath(), Matchers.containsString("existingFile.txt")),
                () -> Assertions.assertEquals(BigInteger.valueOf(23), actualHashResult.getSizeInBytes()),
                () -> Assertions.assertEquals("23 bytes", actualHashResult.getSize()),
                () -> Assertions.assertEquals("SHA256", actualHashResult.getHashingAlgorithmName()),
                () -> Assertions.assertEquals("224ff5a028e147b555f07f3e833950acb250baa121c3cc742fc390f5fd5ff9ec",
                        actualHashResult.getHashValue())
        );
    }

    private WorkItem createWorkItem() {
        return WorkItem.builder().build();
    }
}