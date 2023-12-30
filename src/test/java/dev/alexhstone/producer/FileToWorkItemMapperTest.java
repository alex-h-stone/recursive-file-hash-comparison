package dev.alexhstone.producer;

import dev.alexhstone.model.queue.WorkItem;
import dev.alexhstone.test.util.FileSystemUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileToWorkItemMapperTest {

    private static final Instant WORK_ITEM_CREATION_TIME = Instant.parse("2023-12-20T10:15:30Z");

    @TempDir
    private Path temporaryDirectory;

    private FileSystemUtils fileSystemUtils;

    private FileToWorkItemMapper fileToWorkItemMapper;

    @BeforeEach
    void setUp() {
        fileSystemUtils = new FileSystemUtils(temporaryDirectory);
        fileToWorkItemMapper = new FileToWorkItemMapper() {
            Instant getInstantNow() {
                return WORK_ITEM_CREATION_TIME;
            }
        };
    }

    @Test
    void shouldMapAllFieldsForEmptyFile() {
        File fileWithContent = fileSystemUtils.createFileWithContent("sampleFile.dat", StringUtils.EMPTY);

        WorkItem actualWorkItem = fileToWorkItemMapper.map(temporaryDirectory, fileWithContent);

        assertAll(
                "Grouped Assertions for WorkItem",
                () -> assertNotNull(actualWorkItem),
                () -> assertThat(actualWorkItem.getId(), containsStrings("sampleFile.dat")),
                () -> assertThat(actualWorkItem.getName(), equalTo("sampleFile.dat")),
                () -> assertThat(actualWorkItem.getAbsolutePath(), containsStrings("sampleFile.dat")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), not(Matchers.isEmptyOrNullString())),
                () -> assertThat(actualWorkItem.getSizeInBytes(), equalTo(BigInteger.ZERO)),
                () -> assertThat(actualWorkItem.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME))
        );
    }

    @Test
    void shouldMapAllFieldsForNonEmptyFile() {
        File fileWithContent = fileSystemUtils.createFileWithContent("sampleFile.dat", "Some file contents");

        WorkItem actualWorkItem = fileToWorkItemMapper.map(temporaryDirectory, fileWithContent);

        assertAll(
                "Grouped Assertions for WorkItem",
                () -> assertNotNull(actualWorkItem),
                () -> assertThat(actualWorkItem.getId(), containsStrings("sampleFile.dat")),
                () -> assertThat(actualWorkItem.getName(), equalTo("sampleFile.dat")),
                () -> assertThat(actualWorkItem.getAbsolutePath(), containsStrings("sampleFile.dat")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), not(Matchers.isEmptyOrNullString())),
                () -> assertThat(actualWorkItem.getSizeInBytes(), equalTo(BigInteger.valueOf(18))),
                () -> assertThat(actualWorkItem.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME))
        );
    }

    @Test
    void shouldMapAllFieldsForNonEmptyFileInSubDirectory() {
        Path directoryOne = fileSystemUtils.createDirectory("directoryOne");
        File fileWithContent = new FileSystemUtils(directoryOne).createFileWithContent("sampleFile.dat", "New file contents");

        WorkItem actualWorkItem = fileToWorkItemMapper.map(directoryOne, fileWithContent);

        assertAll(
                "Grouped Assertions for WorkItem",
                () -> assertNotNull(actualWorkItem),
                () -> assertThat(actualWorkItem.getId(), containsStrings("directoryOne", "sampleFile.dat")),
                () -> assertThat(actualWorkItem.getName(), equalTo("sampleFile.dat")),
                () -> assertThat(actualWorkItem.getAbsolutePath(), containsStrings("directoryOne", "sampleFile.dat")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.containsString("directoryOne")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.not(Matchers.containsString("sampleFile.dat"))),
                () -> assertThat(actualWorkItem.getSizeInBytes(), equalTo(BigInteger.valueOf(17))),
                () -> assertThat(actualWorkItem.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME))
        );
    }

    @Test
    void shouldMapAllFieldsForAnEmptySubDirectory() {
        Path directoryOne = fileSystemUtils.createDirectory("directoryOne");
        Path directoryTwo = new FileSystemUtils(directoryOne).createDirectory("directoryTwo");

        WorkItem actualWorkItem = fileToWorkItemMapper.map(directoryOne, directoryTwo.toFile());
        assertThat("Failed precondition", directoryTwo.toFile().listFiles(), Matchers.arrayWithSize(0));

        assertAll(
                "Grouped Assertions for WorkItem",
                () -> assertNotNull(actualWorkItem),
                () -> assertThat(actualWorkItem.getId(), containsStrings("directoryOne", "directoryTwo")),
                () -> assertThat(actualWorkItem.getName(), equalTo("directoryTwo")),
                () -> assertThat(actualWorkItem.getAbsolutePath(), containsStrings("directoryOne", "directoryTwo")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.containsString("directoryOne")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.not(Matchers.containsString("directoryTwo"))),
                () -> assertThat(actualWorkItem.getSizeInBytes(), equalTo(BigInteger.ZERO)),
                () -> assertThat(actualWorkItem.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME))
        );
    }

    @Test
    void shouldMapAllFieldsForANonEmptySubDirectory() {
        Path directoryOne = fileSystemUtils.createDirectory("directoryOne");
        Path directoryTwo = new FileSystemUtils(directoryOne).createDirectory("directoryTwo");
        File fileWithContent = new FileSystemUtils(directoryTwo).createFileWithContent("sampleFile.dat", "New file contents");

        assertNotNull(fileWithContent, "Failed precondition");
        assertThat("Failed precondition", directoryTwo.toFile().listFiles(), Matchers.arrayWithSize(1));

        WorkItem actualWorkItem = fileToWorkItemMapper.map(directoryOne, directoryTwo.toFile());

        assertAll(
                "Grouped Assertions for WorkItem",
                () -> assertNotNull(actualWorkItem),
                () -> assertThat(actualWorkItem.getId(), containsStrings("directoryOne", "directoryTwo")),
                () -> assertThat(actualWorkItem.getName(), equalTo("directoryTwo")),
                () -> assertThat(actualWorkItem.getAbsolutePath(), containsStrings("directoryOne", "directoryTwo")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.containsString("directoryOne")),
                () -> assertThat(actualWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.not(Matchers.containsString("directoryTwo"))),
                () -> assertThat(actualWorkItem.getSizeInBytes(), equalTo(BigInteger.valueOf(17))),
                () -> assertThat(actualWorkItem.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME))
        );
    }

    private Matcher<String> containsStrings(String... substrings) {
        List<Matcher<? super String>> containsEachString = Arrays.stream(substrings)
                .map(CoreMatchers::containsString)
                .collect(Collectors.toList());

        return Matchers.allOf(containsEachString);
    }
}