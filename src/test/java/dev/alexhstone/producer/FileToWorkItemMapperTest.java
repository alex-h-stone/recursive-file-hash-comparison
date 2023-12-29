package dev.alexhstone.producer;

import dev.alexhstone.model.queue.WorkItem;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileToWorkItemMapperTest {

    @TempDir
    private Path temporaryDirectory;

    private FileSystemUtils fileSystemUtils;

    private FileToWorkItemMapper fileToWorkItemMapper;

    @BeforeEach
    void setUp() {
        fileSystemUtils = new FileSystemUtils(temporaryDirectory);
        fileToWorkItemMapper = new FileToWorkItemMapper();
    }

    @Test
    void shouldMapAllFieldsForEmptyFile() {
        File fileWithContent = fileSystemUtils.createFileWithContent("sampleFile.dat", StringUtils.EMPTY);

        WorkItem actualWorkItem = fileToWorkItemMapper.map(temporaryDirectory, fileWithContent);

        assertAll(
                "Grouped Assertions for WorkItem",
                () -> assertNotNull(actualWorkItem),
                () -> assertThat(actualWorkItem.getAbsolutePath(), CoreMatchers.containsString("sampleFile.dat")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), not(Matchers.isEmptyOrNullString())),
                () -> assertEquals(BigInteger.ZERO, actualWorkItem.getSizeInBytes())
        );
    }

    @Test
    void shouldMapAllFieldsForNonEmptyFile() {
        File fileWithContent = fileSystemUtils.createFileWithContent("sampleFile.dat", "Some file contents");

        WorkItem actualWorkItem = fileToWorkItemMapper.map(temporaryDirectory, fileWithContent);

        assertAll(
                "Grouped Assertions for WorkItem",
                () -> assertNotNull(actualWorkItem),
                () -> assertThat(actualWorkItem.getAbsolutePath(), CoreMatchers.containsString("sampleFile.dat")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), not(Matchers.isEmptyOrNullString())),
                () -> assertEquals(BigInteger.valueOf(18), actualWorkItem.getSizeInBytes())
        );
    }

    @Test
    void shouldMapAllFieldsNonEmptyFileInDirectoryStructure() {
        Path directoryOne = fileSystemUtils.createDirectory("directoryOne");
        File fileWithContent = new FileSystemUtils(directoryOne).createFileWithContent("sampleFile.dat", "New file contents");

        WorkItem actualWorkItem = fileToWorkItemMapper.map(directoryOne, fileWithContent);

        assertAll(
                "Grouped Assertions for WorkItem",
                () -> assertNotNull(actualWorkItem),
                () -> assertThat(actualWorkItem.getId(), CoreMatchers.containsString("directoryOne")),
                () -> assertThat(actualWorkItem.getName(), CoreMatchers.equalTo("sampleFile.dat")),

                () -> assertThat(actualWorkItem.getAbsolutePath(), CoreMatchers.containsString("sampleFile.dat")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.containsString("directoryOne")),
                () -> assertEquals(BigInteger.valueOf(17), actualWorkItem.getSizeInBytes())
        );
    }

    @Test
    void shouldMapAllFieldsForAnEmptyDirectory() {
        Path directoryOne = fileSystemUtils.createDirectory("directoryOne");
        Path directoryTwo = new FileSystemUtils(directoryOne).createDirectory("directoryTwo");

        WorkItem actualWorkItem = fileToWorkItemMapper.map(directoryOne, directoryTwo.toFile());

        assertAll(
                "Grouped Assertions for WorkItem",
                () -> assertNotNull(actualWorkItem),
                () -> assertThat(actualWorkItem.getId(), CoreMatchers.containsString("directoryOne")),
                () -> assertThat(actualWorkItem.getId(), CoreMatchers.containsString("directoryTwo")),
                () -> assertThat(actualWorkItem.getName(), CoreMatchers.equalTo("directoryTwo")),
                () -> assertThat(actualWorkItem.getAbsolutePath(), CoreMatchers.containsString("directoryOne")),
                () -> assertThat(actualWorkItem.getAbsolutePath(), CoreMatchers.containsString("directoryTwo")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.containsString("directoryOne")),
                () -> assertEquals(BigInteger.ZERO, actualWorkItem.getSizeInBytes()),
                () -> assertThat(actualWorkItem.getWorkItemCreationTime(), Matchers.greaterThanOrEqualTo(Instant.now().minus(1, ChronoUnit.DAYS)))
        );
    }

    @Test
    void shouldMapAllFieldsForANonEmptyDirectory() {
        Path directoryOne = fileSystemUtils.createDirectory("directoryOne");
        Path directoryTwo = new FileSystemUtils(directoryOne).createDirectory("directoryTwo");
        File fileWithContent = new FileSystemUtils(directoryTwo).createFileWithContent("sampleFile.dat", "New file contents");
        assertNotNull(fileWithContent, "Failed precondition");

        WorkItem actualWorkItem = fileToWorkItemMapper.map(directoryOne, directoryTwo.toFile());

        assertAll(
                "Grouped Assertions for WorkItem",
                () -> assertNotNull(actualWorkItem),
                () -> assertThat(actualWorkItem.getId(), CoreMatchers.containsString("directoryOne")),
                () -> assertThat(actualWorkItem.getId(), CoreMatchers.containsString("directoryTwo")),
                () -> assertThat(actualWorkItem.getName(), CoreMatchers.equalTo("directoryTwo")),
                () -> assertThat(actualWorkItem.getAbsolutePath(), CoreMatchers.containsString("directoryOne")),
                () -> assertThat(actualWorkItem.getAbsolutePath(), CoreMatchers.containsString("directoryTwo")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.containsString("directoryOne")),
                () -> assertEquals(BigInteger.valueOf(17), actualWorkItem.getSizeInBytes()),
                () -> assertThat(actualWorkItem.getWorkItemCreationTime(), Matchers.greaterThanOrEqualTo(Instant.now().minus(1, ChronoUnit.DAYS)))
        );
    }
}