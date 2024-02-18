package dev.alexhstone.producer;

import dev.alexhstone.model.workitem.FileWorkItem;
import dev.alexhstone.test.util.FileSystemUtils;
import dev.alexhstone.util.Clock;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
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
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileToFileWorkItemMapperTest {

    private static final Instant WORK_ITEM_CREATION_TIME =
            Instant.parse("2023-12-20T10:15:30Z");

    @TempDir
    private Path temporaryDirectory;

    private FileSystemUtils fileSystemUtils;

    private FileToWorkItemMapper fileToWorkItemMapper;

    @BeforeEach
    void setUp() {
        fileSystemUtils = new FileSystemUtils(temporaryDirectory);
        Clock stubClock = new Clock() {
            public Instant getInstantNow() {
                return WORK_ITEM_CREATION_TIME;
            }
        };
        fileToWorkItemMapper = new FileToWorkItemMapper(stubClock);
    }

    @Test
    void shouldMapAllFieldsForEmptyFile() {
        File fileWithContent = fileSystemUtils.createFileWithContent("sampleFile.dat", StringUtils.EMPTY);

        FileWorkItem actualFileWorkItem = applyMapper(temporaryDirectory, fileWithContent);

        assertAll(
                "Grouped Assertions for FileWorkItem",
                () -> assertNotNull(actualFileWorkItem),
                () -> assertThat(actualFileWorkItem.getId(), containsStrings("sampleFile.dat")),
                () -> assertThat(actualFileWorkItem.getName(), equalTo("sampleFile.dat")),
                () -> assertThat(actualFileWorkItem.getAbsolutePath(), containsStrings("sampleFile.dat")),
                () -> assertThat(actualFileWorkItem.getAbsolutePathToWorkingDirectory(), not(Matchers.isEmptyOrNullString())),
                () -> assertThat(actualFileWorkItem.getSizeInBytes(), equalTo(BigInteger.ZERO)),
                () -> assertThat(actualFileWorkItem.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME))
        );
    }

    @Test
    void shouldMapAllFieldsForNonEmptyFile() {
        File fileWithContent = fileSystemUtils.createFileWithContent("sampleFile.dat", "Some file contents");

        FileWorkItem actualFileWorkItem = applyMapper(temporaryDirectory, fileWithContent);

        assertAll(
                "Grouped Assertions for FileWorkItem",
                () -> assertNotNull(actualFileWorkItem),
                () -> assertThat(actualFileWorkItem.getId(), containsStrings("sampleFile.dat")),
                () -> assertThat(actualFileWorkItem.getName(), equalTo("sampleFile.dat")),
                () -> assertThat(actualFileWorkItem.getAbsolutePath(), containsStrings("sampleFile.dat")),
                () -> assertThat(actualFileWorkItem.getAbsolutePathToWorkingDirectory(), not(Matchers.isEmptyOrNullString())),
                () -> assertThat(actualFileWorkItem.getSizeInBytes(), equalTo(BigInteger.valueOf(18))),
                () -> assertThat(actualFileWorkItem.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME))
        );
    }

    @Test
    void shouldMapAllFieldsForNonEmptyFileInSubDirectory() {
        Path directoryOne = fileSystemUtils.createDirectory("directoryOne");
        File fileWithContent = new FileSystemUtils(directoryOne).createFileWithContent("sampleFile.dat", "New file contents");

        FileWorkItem actualFileWorkItem = applyMapper(directoryOne, fileWithContent);

        assertAll(
                "Grouped Assertions for FileWorkItem",
                () -> assertNotNull(actualFileWorkItem),
                () -> assertThat(actualFileWorkItem.getId(), containsStrings("directoryOne", "sampleFile.dat")),
                () -> assertThat(actualFileWorkItem.getName(), equalTo("sampleFile.dat")),
                () -> assertThat(actualFileWorkItem.getAbsolutePath(), containsStrings("directoryOne", "sampleFile.dat")),
                () -> assertThat(actualFileWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.containsString("directoryOne")),
                () -> assertThat(actualFileWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.not(Matchers.containsString("sampleFile.dat"))),
                () -> assertThat(actualFileWorkItem.getSizeInBytes(), equalTo(BigInteger.valueOf(17))),
                () -> assertThat(actualFileWorkItem.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME))
        );
    }

    @Test
    void shouldMapAllFieldsForAnEmptySubDirectory() {
        Path directoryOne = fileSystemUtils.createDirectory("directoryOne");
        Path directoryTwo = new FileSystemUtils(directoryOne).createDirectory("directoryTwo");

        FileWorkItem actualFileWorkItem = applyMapper(directoryOne, directoryTwo.toFile());
        assertThat("Failed precondition", directoryTwo.toFile().listFiles(), Matchers.arrayWithSize(0));

        assertAll(
                "Grouped Assertions for FileWorkItem",
                () -> assertNotNull(actualFileWorkItem),
                () -> assertThat(actualFileWorkItem.getId(), containsStrings("directoryOne", "directoryTwo")),
                () -> assertThat(actualFileWorkItem.getName(), equalTo("directoryTwo")),
                () -> assertThat(actualFileWorkItem.getAbsolutePath(), containsStrings("directoryOne", "directoryTwo")),
                () -> assertThat(actualFileWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.containsString("directoryOne")),
                () -> assertThat(actualFileWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.not(Matchers.containsString("directoryTwo"))),
                () -> assertThat(actualFileWorkItem.getSizeInBytes(), equalTo(BigInteger.ZERO)),
                () -> assertThat(actualFileWorkItem.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME))
        );
    }

    @Test
    void shouldMapAllFieldsForANonEmptySubDirectory() {
        Path directoryOne = fileSystemUtils.createDirectory("directoryOne");
        Path directoryTwo = new FileSystemUtils(directoryOne).createDirectory("directoryTwo");
        File fileWithContent = new FileSystemUtils(directoryTwo).createFileWithContent("sampleFile.dat", "New file contents");

        assertNotNull(fileWithContent, "Failed precondition");
        assertThat("Failed precondition", directoryTwo.toFile().listFiles(), Matchers.arrayWithSize(1));

        FileWorkItem actualFileWorkItem = applyMapper(directoryOne, directoryTwo.toFile());

        assertAll(
                "Grouped Assertions for FileWorkItem",
                () -> assertNotNull(actualFileWorkItem),
                () -> assertThat(actualFileWorkItem.getId(), containsStrings("directoryOne", "directoryTwo")),
                () -> assertThat(actualFileWorkItem.getName(), equalTo("directoryTwo")),
                () -> assertThat(actualFileWorkItem.getAbsolutePath(), containsStrings("directoryOne", "directoryTwo")),
                () -> assertThat(actualFileWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.containsString("directoryOne")),
                () -> assertThat(actualFileWorkItem.getAbsolutePathToWorkingDirectory(), Matchers.not(Matchers.containsString("directoryTwo"))),
                () -> assertThat(actualFileWorkItem.getSizeInBytes(), equalTo(BigInteger.valueOf(17))),
                () -> assertThat(actualFileWorkItem.getWorkItemCreationTime(), equalTo(WORK_ITEM_CREATION_TIME))
        );
    }

    private FileWorkItem applyMapper(Path workingDirectory, File file) {
        FileWorkItem fileWorkItem = fileToWorkItemMapper.map(workingDirectory, file);

        FileWorkItem fileWorkItemViaFunction = fileToWorkItemMapper
                .asFunction(workingDirectory)
                .apply(file);

        assertThat("Expected work items created via different means to be equal",
                fileWorkItem, equalTo(fileWorkItemViaFunction));

        return fileWorkItem;
    }

}