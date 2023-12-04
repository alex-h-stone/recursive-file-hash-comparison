package dev.alexhstone;

import dev.alexhstone.model.FolderHierarchy;
import dev.alexhstone.test.util.FileCreator;
import dev.alexhstone.exception.InvalidFileHashPathException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecursiveFileHashReportGeneratorTest {

    @TempDir
    private Path temporaryDirectory;

    private RecursiveFileHashCalculator reportGenerator;

    private FileCreator fileCreator;

    @BeforeEach
    void setUp() {
        fileCreator = new FileCreator(temporaryDirectory);
        reportGenerator = new RecursiveFileHashCalculator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown_path", "invalid_path",})
    void shouldThrowExceptionIfGivenInvalidPath(String invalidPath) {
        InvalidFileHashPathException invalidFileHashPathException = assertThrows(InvalidFileHashPathException.class,
                () -> reportGenerator.process(invalidPath));

        String exceptionMessage = invalidFileHashPathException.getMessage();
        assertThat(exceptionMessage, CoreMatchers.containsString("The supplied path [" + invalidPath + "] does not exist"));
        assertThat(exceptionMessage, CoreMatchers.containsString("expected something of the form C:\\directory"));
    }

    @Test
    void shouldThrowExceptionIfGivenInvalidPath() {
        InvalidFileHashPathException invalidFileHashPathException = assertThrows(InvalidFileHashPathException.class,
                () -> reportGenerator.process("G:\\non-exist"));

        assertEquals("The supplied path [G:\\non-exist] does not exist, expected something of the form C:\\directory. The supplied path was resolved to the absolute path [G:\\non-exist]",
                invalidFileHashPathException.getMessage());
    }

    @Test
    void shouldGenerateAnEmptyListForAnEmptyFolder() {
        FolderHierarchy results = reportGenerator.process(temporaryDirectory.toFile().getAbsolutePath());

        assertThat(results.getFileHashResults(), Matchers.empty());
        assertThat(results.getRootFolderAbsolutePath(), Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    void shouldGenerateASingleResultForSingleFileInAFolder() {
        createNonEmptyFile("emptyFile.txt");

        FolderHierarchy results = reportGenerator.process(temporaryDirectory.toFile().getAbsolutePath());

        assertThat(results.getFileHashResults(), Matchers.hasSize(1));
        assertThat(results.getRootFolderAbsolutePath(), Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    void shouldGenerateAReportWithTwoFilesForAFolderWithTwoFiles() {
        createNonEmptyFile("emptyFile1.txt");
        createNonEmptyFile("emptyFile2.txt");

        FolderHierarchy results = reportGenerator.process(temporaryDirectory.toFile().getAbsolutePath());

        assertThat(results.getFileHashResults(), Matchers.hasSize(2));
        assertThat(results.getRootFolderAbsolutePath(), Matchers.not(Matchers.isEmptyOrNullString()));
    }

    private void createNonEmptyFile(String file) {
        fileCreator.createFileWithContent(file, "Not empty");
    }
}