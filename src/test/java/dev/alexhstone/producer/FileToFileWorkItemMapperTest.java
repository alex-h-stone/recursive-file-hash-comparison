package dev.alexhstone.producer;

import dev.alexhstone.model.queue.FileWorkItem;
import dev.alexhstone.test.util.FileSystemUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileToFileWorkItemMapperTest {

    @TempDir
    private Path temporaryDirectory;

    private FileSystemUtils fileSystemUtils;

    private FileToFileWorkItemMapper fileToFileWorkItemMapper;

    @BeforeEach
    void setUp() {
        fileSystemUtils = new FileSystemUtils(temporaryDirectory);
        fileToFileWorkItemMapper = new FileToFileWorkItemMapper();
    }

    @Test
    void shouldMapAllFieldsForEmptyFile() {
        File fileWithContent = fileSystemUtils.createFileWithContent("sampleFile.dat", StringUtils.EMPTY);

        FileWorkItem actualFileWorkItem = fileToFileWorkItemMapper.map(temporaryDirectory, fileWithContent);

        assertAll(
                "Grouped Assertions of FileHashResult",
                () -> assertNotNull(actualFileWorkItem),
                () -> assertThat( actualFileWorkItem.getAbsolutePath(),CoreMatchers.containsString("sampleFile.dat")),
                () -> assertThat( actualFileWorkItem.getAbsolutePathToWorkingDirectory(), not(Matchers.isEmptyOrNullString())),
                () -> assertEquals(BigInteger.ZERO, actualFileWorkItem.getSizeInBytes())
        );
    }

    @Test
    void shouldMapAllFieldsForNonEmptyFile() {
        File fileWithContent = fileSystemUtils.createFileWithContent("sampleFile.dat", "Some file contents");

        FileWorkItem actualFileWorkItem = fileToFileWorkItemMapper.map(temporaryDirectory, fileWithContent);

        assertAll(
                "Grouped Assertions of FileHashResult",
                () -> assertNotNull(actualFileWorkItem),
                () -> assertThat( actualFileWorkItem.getAbsolutePath(),CoreMatchers.containsString("sampleFile.dat")),
                () -> assertThat( actualFileWorkItem.getAbsolutePathToWorkingDirectory(), not(Matchers.isEmptyOrNullString())),
                () -> assertEquals(BigInteger.valueOf(18), actualFileWorkItem.getSizeInBytes())
        );
    }

    @Test
    void shouldMapAllFieldsNonEmptyFileInDirectoryStructure() {
        Path directoryOne = fileSystemUtils.createDirectory("directoryOne");
        File fileWithContent = new FileSystemUtils(directoryOne).createFileWithContent("sampleFile.dat", "New file contents");

        FileWorkItem actualFileWorkItem = fileToFileWorkItemMapper.map(directoryOne, fileWithContent);

        assertAll(
                "Grouped Assertions of FileHashResult",
                () -> assertNotNull(actualFileWorkItem),
                () -> assertThat( actualFileWorkItem.getAbsolutePath(),CoreMatchers.containsString("sampleFile.dat")),
                () -> assertThat( actualFileWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.containsString("directoryOne")),
                () -> assertEquals(BigInteger.valueOf(17), actualFileWorkItem.getSizeInBytes())
        );
    }
}