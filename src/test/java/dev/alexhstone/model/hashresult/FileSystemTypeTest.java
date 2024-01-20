package dev.alexhstone.model.hashresult;

import dev.alexhstone.test.util.FileSystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileSystemTypeTest {

    @TempDir
    private Path temporaryDirectory;

    private FileSystemUtils fileSystemUtils;

    @BeforeEach
    void setUp() {
        fileSystemUtils = new FileSystemUtils(temporaryDirectory);
    }

    @Test
    void shouldReturnFILEForAFileFileObject() {
        File file = fileSystemUtils.createFileWithContent("tempFilename.dat", "File contents");

        FileSystemType actualFileSystemType = FileSystemType.valueOfFile(file);

        assertEquals(FileSystemType.FILE, actualFileSystemType);
    }

    @Test
    void shouldReturnDIRECTORYorADirectoryFileObject() {
        Path directory = fileSystemUtils.createDirectory("tempDirectory");

        FileSystemType actualFileSystemType = FileSystemType.valueOfFile(directory.toFile());

        assertEquals(FileSystemType.DIRECTORY, actualFileSystemType);
    }

    @Test
    void shouldThrowIllegalArForNullFileObject() {
        IllegalArgumentException actualException = assertThrows(IllegalArgumentException.class,
                () -> FileSystemType.valueOfFile(null));

        assertEquals("Unable to determine FileSystemType for [null]", actualException.getMessage());
    }

    @Test
    void shouldThrowIllegalStateExceptionForUnknownFileEntity() {
        File unknownFileEntity = new File("unknownFileEntity");

        IllegalStateException actualException = assertThrows(IllegalStateException.class,
                () -> FileSystemType.valueOfFile(unknownFileEntity));

        assertEquals("Unable to determine FileSystemType for [unknownFileEntity]", actualException.getMessage());
    }
}