package dev.alexhstone.calculator;

import dev.alexhstone.consumer.HashCalculator;
import dev.alexhstone.test.util.FileSystemUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.text.IsEmptyString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashCalculatorTest {

    @TempDir
    private Path temporaryDirectory;

    private HashCalculator hashCalculator;

    private FileSystemUtils fileSystemUtils;

    @BeforeEach
    void setUp() {
        fileSystemUtils = new FileSystemUtils(temporaryDirectory);
        hashCalculator = new HashCalculator();
    }

    @Test
    void shouldGenerateNonEmptyHashForAFile() {
        File resolvedFile = createFileWithContent("temporaryFile.txt", "Hello, JUnit 5!");
        String hash = hashCalculator.calculateHashFor(resolvedFile);

        assertThat(hash, CoreMatchers.not(IsEmptyString.isEmptyString()));
    }

    @Test
    void shouldGenerateNonEmptyHashForAnEmptyFile() {
        File resolvedFile = createFileWithContent("temporaryFile.txt", "");
        String hash = hashCalculator.calculateHashFor(resolvedFile);

        assertThat(hash, CoreMatchers.not(IsEmptyString.isEmptyString()));
    }

    @Test
    void shouldGenerateTheSameHashForFilesWithTheSameContent() {
        String sameTestFileContents = "Some test file contents";
        File fileOne = createFileWithContent("someFile1.txt", sameTestFileContents);
        File fileTwo = createFileWithContent("someFile2.txt", sameTestFileContents);

        String fileOneHash = hashCalculator.calculateHashFor(fileOne);
        String fileTwoHash = hashCalculator.calculateHashFor(fileTwo);

        assertEquals(fileOneHash, fileTwoHash);
    }

    @Test
    void shouldGenerateTheSameHashForTheSameEverytimeItIsGenerated() {
        File file = createFileWithContent("someFile.txt", "Some test file contents");

        final String originalHash = hashCalculator.calculateHashFor(file);

        IntStream.range(1, 1_000).forEach(iteration -> {
            String newHash = hashCalculator.calculateHashFor(file);
            String message = "For iteration %d the new hash [%s] does not match the original hash [%s]"
                    .formatted(iteration, newHash, originalHash);
            assertEquals(originalHash, newHash, message);
        });
    }

    @Test
    void shouldNotCalculateHashIfFileIsADirectory() {
        File file = temporaryDirectory.toFile();
        assertTrue(file.isDirectory(), "Failed precondition");

        String actualHash = hashCalculator.calculateHashFor(file);

        assertEquals("[Cannot calculate hash for a directory]", actualHash);
    }

    @Test
    @Tag("long-running")
    void shouldNotEncounterThreadingIssuesWhenCalculatingManyHashesForDifferentFiles() throws InterruptedException {
        int numberOfThreads = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        int numberOfTestFiles = 2_000;
        List<File> testFiles = IntStream.range(1, numberOfTestFiles)
                .mapToObj(value -> {
                    String fileName = "testFile" + value + ".txt";
                    String fileContents = "Test file contents for file: " + fileName;
                    return createFileWithContent(fileName, fileContents);
                }).toList();

        Map<String, String> fileNameToHash = testFiles.stream()
                .collect(Collectors.toUnmodifiableMap(File::getName,
                        file -> hashCalculator.calculateHashFor(file)));

        for (File testFile : testFiles) {
            executorService.submit(() -> {
                String newHash = hashCalculator.calculateHashFor(testFile);
                String expectedHash = fileNameToHash.get(testFile.getName());

                assertEquals(expectedHash, newHash,
                        "Expected new hash generated in a multithreaded environment to match initial hash generated in single threaded environment");
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    void shouldReturnNonEmptyStringWhenCallingGetAlgorithmName() {
        String actualAlgorithmName = hashCalculator.getAlgorithmName();

        assertThat(actualAlgorithmName, Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    void shouldHashEmptyString() {
        String actualHash = hashCalculator.calculateHashFor(StringUtils.EMPTY);

        assertThat(actualHash, CoreMatchers.not(IsEmptyString.isEmptyString()));
    }

    @Test
    void shouldHashNonEmptyString() {
        String actualHash = hashCalculator.calculateHashFor("sample contents for hashing");

        assertThat(actualHash, CoreMatchers.not(IsEmptyString.isEmptyString()));
    }

    private File createFileWithContent(String fileName, String content) {
        return fileSystemUtils.createFileWithContent(fileName, content);
    }
}