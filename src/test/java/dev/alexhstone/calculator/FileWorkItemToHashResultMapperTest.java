package dev.alexhstone.calculator;

import dev.alexhstone.consumer.WorkItemToHashResultMapper;
import dev.alexhstone.model.fileworkitem.FileWorkItem;
import dev.alexhstone.model.hashresult.HashResult;
import dev.alexhstone.producer.FileToWorkItemMapper;
import dev.alexhstone.test.util.FileSystemUtils;
import dev.alexhstone.util.Clock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.Instant;

import static dev.alexhstone.util.HamcrestUtilities.containsStrings;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class FileWorkItemToHashResultMapperTest {

    private static final Instant WORK_ITEM_CREATION_TIME =
            Instant.parse("2023-12-20T10:15:30Z");
    private static final Instant HASH_RESULT_CREATION_TIME =
            Instant.parse("2023-12-20T13:12:57Z");

    @TempDir
    private Path temporaryDirectory;

    private FileSystemUtils fileSystemUtils;
    private WorkItemToHashResultMapper mapper;
    private FileToWorkItemMapper fileToWorkItemMapper;
    private String temporaryDirectoryAbsolutePath;

    @BeforeEach
    void setUp() {
        fileSystemUtils = new FileSystemUtils(temporaryDirectory);
        fileToWorkItemMapper = new FileToWorkItemMapper(Clock.stubClockOf(WORK_ITEM_CREATION_TIME));
        temporaryDirectoryAbsolutePath = temporaryDirectory.toFile().getAbsolutePath();

        mapper = new WorkItemToHashResultMapper(Clock.stubClockOf(HASH_RESULT_CREATION_TIME));
    }

    @Test
    void shouldCreateFullyPopulatedFileHashResultForFileThatExists() {
        File existingFile = fileSystemUtils.createFileWithContent("existingFile.txt", "Some test file contents");

        FileWorkItem fileWorkItem = fileToWorkItemMapper.map(temporaryDirectory, existingFile);
        assertNotNull(fileWorkItem, "Failed precondition");

        HashResult actualHashResult = mapper.map(fileWorkItem);

        Assertions.assertAll(
                "Grouped Assertions of HashResult",
                () -> assertThat("existingFile.txt", equalTo(actualHashResult.getName())),
                () -> assertThat(actualHashResult.getAbsolutePath(), equalTo(existingFile.getAbsolutePath())),
                () -> assertThat(actualHashResult.getAbsolutePathToWorkingDirectory(), containsStrings(fileWorkItem.getAbsolutePathToWorkingDirectory())),
                () -> assertThat(actualHashResult.getRelativePath(), Matchers.isEmptyString()),
                () -> assertThat(actualHashResult.getRelativePathToFile(), equalTo("existingFile.txt")),
                () -> assertThat(actualHashResult.getSizeInBytes(), equalTo(BigInteger.valueOf(23))),
                () -> assertThat(actualHashResult.getSize(), equalTo("23 bytes")),
                () -> assertThat(actualHashResult.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME)),
                () -> assertThat(actualHashResult.getCreationTime(), equalTo(HASH_RESULT_CREATION_TIME)),
                () -> assertThat(actualHashResult.getHashingAlgorithmName(), equalTo("SHA256")),
                () -> assertThat(actualHashResult.getHashValue(),
                        equalTo("224ff5a028e147b555f07f3e833950acb250baa121c3cc742fc390f5fd5ff9ec")));
    }

    @Test
    void shouldCreateFullyPopulatedFileHashResultForFileInSubdirectory() {
        Path parentDirectory = fileSystemUtils.createDirectory("parentDirectory");
        Path childDirectory = new FileSystemUtils(parentDirectory).createDirectory("childDirectory");

        File existingFile = new FileSystemUtils(childDirectory).createFileWithContent("existingFile.txt", "Some test file contents");

        FileWorkItem fileWorkItem = fileToWorkItemMapper.map(temporaryDirectory, existingFile);
        assertNotNull(fileWorkItem, "Failed precondition");

        HashResult actualHashResult = mapper.map(fileWorkItem);

        Assertions.assertAll(
                "Grouped Assertions of HashResult",
                () -> assertThat("existingFile.txt", equalTo(actualHashResult.getName())),
                () -> assertThat(actualHashResult.getAbsolutePath(), equalTo(existingFile.getAbsolutePath())),
                () -> assertThat(actualHashResult.getRelativePath(), equalTo("parentDirectory\\childDirectory")),
                () -> assertThat(actualHashResult.getRelativePathToFile(), equalTo("parentDirectory\\childDirectory\\existingFile.txt")),
                () -> assertThat(actualHashResult.getSizeInBytes(), equalTo(BigInteger.valueOf(23))),
                () -> assertThat(actualHashResult.getSize(), equalTo("23 bytes")),
                () -> assertThat(actualHashResult.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME)),
                () -> assertThat(actualHashResult.getCreationTime(), equalTo(HASH_RESULT_CREATION_TIME)),
                () -> assertThat(actualHashResult.getHashingAlgorithmName(), equalTo("SHA256")),
                () -> assertThat(actualHashResult.getHashValue(),
                        equalTo("224ff5a028e147b555f07f3e833950acb250baa121c3cc742fc390f5fd5ff9ec")));
    }

    @Test
    void shouldCreateFullyPopulatedFileHashResultForEmptyDirectory() {
        Path emptyDirectory = fileSystemUtils.createDirectory("emptyDirectory");
        File emptyDirectoryAsFile = emptyDirectory.toFile();

        FileWorkItem fileWorkItem = fileToWorkItemMapper.map(temporaryDirectory, emptyDirectoryAsFile);
        assertNotNull(fileWorkItem, "Failed precondition");

        HashResult actualHashResult = mapper.map(fileWorkItem);

        Assertions.assertAll(
                "Grouped Assertions of HashResult",
                () -> assertThat(actualHashResult.getName(), equalTo("emptyDirectory")),
                () -> assertThat(actualHashResult.getAbsolutePath(), equalTo(emptyDirectoryAsFile.getAbsolutePath())),
                () -> assertThat(actualHashResult.getRelativePath(), equalTo("")),
                () -> assertThat(actualHashResult.getRelativePathToFile(), equalTo("emptyDirectory")),
                () -> assertThat(actualHashResult.getSizeInBytes(), equalTo(BigInteger.ZERO)),
                () -> assertThat(actualHashResult.getSize(), equalTo("0 bytes")),
                () -> assertThat(actualHashResult.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME)),
                () -> assertThat(actualHashResult.getCreationTime(), equalTo(HASH_RESULT_CREATION_TIME)),
                () -> assertThat(actualHashResult.getHashingAlgorithmName(), equalTo("SHA256")),
                () -> assertThat(actualHashResult.getHashValue(),
                        equalTo("[Cannot calculate hash for a directory]")));
    }

    @Test
    void shouldCreateFullyPopulatedFileHashResultForNonEmptyDirectory() {
        Path childDirectory = fileSystemUtils.createDirectory("childDirectory");
        new FileSystemUtils(childDirectory).createFileWithContent("someFile.dat", "File contents");
        File childDirectoryAsFile = childDirectory.toFile();

        assertThat("Failed precondition", childDirectoryAsFile.listFiles(), Matchers.arrayWithSize(1));

        FileWorkItem fileWorkItem = fileToWorkItemMapper.map(temporaryDirectory, childDirectoryAsFile);
        assertNotNull(fileWorkItem, "Failed precondition");

        HashResult actualHashResult = mapper.map(fileWorkItem);

        Assertions.assertAll(
                "Grouped Assertions of HashResult",
                () -> assertThat(actualHashResult.getName(), equalTo("childDirectory")),
                () -> assertThat(actualHashResult.getAbsolutePath(), equalTo(childDirectoryAsFile.getAbsolutePath())),
                () -> assertThat(actualHashResult.getRelativePath(), equalTo("")),
                () -> assertThat(actualHashResult.getRelativePathToFile(), equalTo("childDirectory")),
                () -> assertThat(actualHashResult.getSizeInBytes(), equalTo(BigInteger.valueOf(13))),
                () -> assertThat(actualHashResult.getSize(), equalTo("13 bytes")),
                () -> assertThat(actualHashResult.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME)),
                () -> assertThat(actualHashResult.getCreationTime(), equalTo(HASH_RESULT_CREATION_TIME)),
                () -> assertThat(actualHashResult.getHashingAlgorithmName(), equalTo("SHA256")),
                () -> assertThat(actualHashResult.getHashValue(),
                        equalTo("[Cannot calculate hash for a directory]")));
    }
}