package dev.alexhstone.util;

import dev.alexhstone.exception.InvalidPathException;
import dev.alexhstone.test.util.FileSystemUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FolderValidatorTest {

    @TempDir
    private Path temporaryDirectory;

    private FolderValidator folderValidator;

    @BeforeEach
    void setUp() {
        folderValidator = new FolderValidator();
    }

    @Test
    void folderPathExistsAndIsValid_Success() {
        File folderAsFile = temporaryDirectory.toFile();

        assertTrue(folderAsFile.exists(), "Failed precondition");
        assertTrue(folderAsFile.canWrite(), "Failed precondition");

        Path validPath = folderValidator.validateExistsAndWritable(temporaryDirectory);
        assertNotNull(validPath);
        assertEquals(temporaryDirectory, validPath);
    }

    @Test
    void folderPathDoesNotExist_Failure() {
        Path invalidPathDoesNotExist = Path.of("invalidPath_doesNotExist");

        assertFalse(invalidPathDoesNotExist.toFile().exists(), "Failed precondition");

        InvalidPathException expectedException = assertThrows(InvalidPathException.class,
                () -> folderValidator.validateExistsAndWritable(invalidPathDoesNotExist));

        assertThat(expectedException.getMessage(), Matchers.containsString("The path does not exist"));
    }

    @Test
    void folderPathIsActuallyAFile_Failure() {
        File newFileWithContents = new FileSystemUtils(temporaryDirectory).createFileWithContent("testFile.txt", "File contents");

        assertTrue(newFileWithContents.exists(), "Failed precondition");
        assertTrue(newFileWithContents.isFile(), "Failed precondition");
        assertFalse(newFileWithContents.isDirectory(), "Failed precondition");

        Path newFileWithContentsPath = newFileWithContents.toPath();

        InvalidPathException expectedException = assertThrows(InvalidPathException.class,
                () -> folderValidator.validateExistsAndWritable(newFileWithContentsPath));

        assertThat(expectedException.getMessage(), Matchers.containsString("The path is not a directory"));
    }

    @Test
    void folderWithAbsolutePathAsStringExistsAndIsValid_Success() {
        File temporaryDirectoryAsFile = temporaryDirectory.toFile();

        assertTrue(temporaryDirectoryAsFile.exists(), "Failed precondition");
        assertTrue(temporaryDirectoryAsFile.canWrite(), "Failed precondition");

        String absolutePathToDirectoryAsString = temporaryDirectoryAsFile.getAbsolutePath();

        Path validPath = folderValidator.validateExistsAndWritable(absolutePathToDirectoryAsString);
        assertNotNull(validPath);
        assertEquals(temporaryDirectory, validPath);
    }

    @Test
    void folderWithAbsolutePathAsStringDoesNotExist_Failure() {
        String invalidPathToFolderString = "Invalid path to folder";
        Path invalidPathToFolder = Paths.get(invalidPathToFolderString);

        assertFalse(invalidPathToFolder.toFile().exists(), "Failed precondition");

        InvalidPathException expectedException = assertThrows(InvalidPathException.class,
                () -> folderValidator.validateExistsAndWritable(invalidPathToFolderString));

        assertThat(expectedException.getMessage(), Matchers.containsString("The path does not exist"));
    }
}