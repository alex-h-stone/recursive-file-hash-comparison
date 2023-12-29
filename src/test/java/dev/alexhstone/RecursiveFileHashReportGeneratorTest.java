package dev.alexhstone;

import dev.alexhstone.calculator.RecursiveFileHashCalculator;
import dev.alexhstone.exception.InvalidFileHashPathException;
import dev.alexhstone.model.datastore.WorkItemHashResult;
import dev.alexhstone.test.util.FileSystemUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecursiveFileHashReportGeneratorTest {

    @TempDir
    private Path temporaryDirectory;

    private RecursiveFileHashCalculator reportGenerator;

    private FileSystemUtils fileSystemUtils;

    @BeforeEach
    void setUp() {
        fileSystemUtils = new FileSystemUtils(temporaryDirectory);
        reportGenerator = new RecursiveFileHashCalculator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown_path", "invalid_path",})
    void shouldThrowExceptionIfGivenInvalidPath(String invalidPath) {
        InvalidFileHashPathException invalidFileHashPathException = assertThrows(InvalidFileHashPathException.class,
                () -> process(invalidPath));

        String exceptionMessage = invalidFileHashPathException.getMessage();
        assertThat(exceptionMessage, CoreMatchers.containsString("The supplied path [" + invalidPath + "] does not exist"));
        assertThat(exceptionMessage, CoreMatchers.containsString("expected something of the form C:\\directory"));
    }

    @Test
    void shouldThrowExceptionIfGivenInvalidPath() {
        InvalidFileHashPathException invalidFileHashPathException = assertThrows(InvalidFileHashPathException.class,
                () -> process("G:\\non-exist"));

        assertEquals("The supplied path [G:\\non-exist] does not exist, expected something of the form C:\\directory. The supplied path was resolved to the absolute path [G:\\non-exist]",
                invalidFileHashPathException.getMessage());
    }

    @Test
    void shouldGenerateAnEmptyListForAnEmptyFolder() {
        Set<WorkItemHashResult> workItemHashResults = process(temporaryDirectory.toFile().getAbsolutePath());

        assertThat(workItemHashResults, Matchers.empty());
    }

    @Test
    void shouldGenerateASingleResultForSingleFileInAFolder() {
        createNonEmptyFile("emptyFile.txt");

        Set<WorkItemHashResult> workItemHashResults = process(temporaryDirectory.toFile().getAbsolutePath());

        assertThat(workItemHashResults, Matchers.hasSize(1));
    }

    @Test
    void shouldGenerateAReportWithTwoFilesForAFolderWithTwoFiles() {
        createNonEmptyFile("emptyFile1.txt");
        createNonEmptyFile("emptyFile2.txt");

        Set<WorkItemHashResult> workItemHashResults = process(temporaryDirectory.toFile().getAbsolutePath());

        assertThat(workItemHashResults, Matchers.hasSize(2));
    }

    private Set<WorkItemHashResult> process(String path) {
        Set<WorkItemHashResult> hashResults = Collections.synchronizedSet(new HashSet<>());

        reportGenerator.process(Paths.get(path), hashResults::add);

        return hashResults;
    }

    private void createNonEmptyFile(String file) {
        fileSystemUtils.createFileWithContent(file, "Not empty");
    }
}