package dev.alexhstone.test.util;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSystemUtilsTest {

    @TempDir
    private Path temporaryDirectory;
    private FileSystemUtils fileSystemUtils;

    @BeforeEach
    void setUp() {
        fileSystemUtils = new FileSystemUtils(temporaryDirectory);
    }

    @Test
    void createFileWithContentSuccess() throws IOException {
        String fileContents = "Sample content";
        File createdFile = fileSystemUtils.createFileWithContent("TestFile.txt", fileContents);

        assertTrue(createdFile.exists());
        assertTrue(createdFile.isFile());
        assertTrue(createdFile.canRead());
        String readString = Files.readString(createdFile.toPath());
        assertEquals(fileContents, readString);
    }

    @Test
    void createFileWithContentFailureAsAlreadyExists() throws IOException {
        String sameFileName = "TestFile.txt";
        File createdFile = fileSystemUtils.createFileWithContent(sameFileName, "Sample content");

        IllegalArgumentException expectedException = assertThrows(IllegalArgumentException.class,
                () -> fileSystemUtils.createFileWithContent(sameFileName, "Different sample content"));

        assertThat(expectedException.getMessage(), Matchers.containsString("Cannot create new file with path"));
        assertThat(expectedException.getMessage(), Matchers.containsString("as a file with that name already exists"));
        String readString = Files.readString(createdFile.toPath());
        assertEquals("Sample content", readString);
    }

    @Test
    void createEmptyDirectorySuccess() {
        Path sampleDirectory = fileSystemUtils.createDirectory("sampleDirectory");

        assertTrue(sampleDirectory.toFile().exists());
        assertTrue(sampleDirectory.toFile().canRead());
        assertTrue(sampleDirectory.toFile().isDirectory());
    }

    @Test
    void createEmptyDirectoryFailureAsAlreadyExists() {
        Path sampleDirectory = fileSystemUtils.createDirectory("sampleDirectory");

        IllegalArgumentException expectedException = assertThrows(IllegalArgumentException.class, () -> fileSystemUtils.createDirectory("sampleDirectory"));

        assertThat(expectedException.getMessage(), Matchers.containsString("Unable to create the new directory [sampleDirectory] in the working directory"));
    }
}