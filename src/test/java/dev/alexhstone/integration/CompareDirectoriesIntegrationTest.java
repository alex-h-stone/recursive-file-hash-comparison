package dev.alexhstone.integration;

import dev.alexhstone.CompareDirectories;
import dev.alexhstone.model.DiffResults;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("long-running")
@Tag("integration")
public class CompareDirectoriesIntegrationTest {

    @TempDir
    private Path reportDirectory;

    @Test
    void scenarioOne_ShouldBeNoDifferencesAsAllFilesAreIdentical() {
        String scenarioOne = "C:\\Users\\Alex\\github_projects\\recursive-file-hash-comparison\\src\\test\\resources\\integration\\scenarioOne\\";

        CompareDirectories compareDirectories = new CompareDirectories(
                scenarioOne + "left",
                scenarioOne + "right",
                reportDirectory.toFile().getAbsolutePath());

        DiffResults diffResults = compareDirectories.execute();

        assertThat(diffResults.getLeftFilesNotPresentInRight(),
                Matchers.hasSize(0));
        assertThat(diffResults.getRightFilesNotPresentInLeft(),
                Matchers.hasSize(0));

        File[] reportFiles = reportDirectory.toFile().listFiles();

        assertThat(reportFiles, Matchers.arrayWithSize(3));

        Arrays.stream(reportFiles).forEach(file -> {
            BigInteger fileSizeInBytes = FileUtils.sizeOfAsBigInteger(file);
            assertTrue(fileSizeInBytes.compareTo(BigInteger.ONE) >= 0);
        });
    }

    @Test
    void scenarioTwo_ShouldBeADifferencesAspopulatedFile1_copyAreNotIdenticalContents() {
        String scenarioOne = "C:\\Users\\Alex\\github_projects\\recursive-file-hash-comparison\\src\\test\\resources\\integration\\scenarioTwo\\";

        CompareDirectories compareDirectories = new CompareDirectories(
                scenarioOne + "left",
                scenarioOne + "right",
                reportDirectory.toFile().getAbsolutePath());

        DiffResults diffResults = compareDirectories.execute();

        assertThat(diffResults.getLeftFilesNotPresentInRight(),
                Matchers.hasSize(1));
        assertThat(diffResults.getRightFilesNotPresentInLeft(),
                Matchers.hasSize(1));

        File[] reportFiles = reportDirectory.toFile().listFiles();

        assertThat(reportFiles, Matchers.arrayWithSize(3));

        Arrays.stream(reportFiles).forEach(file -> {
            BigInteger fileSizeInBytes = FileUtils.sizeOfAsBigInteger(file);
            assertTrue(fileSizeInBytes.compareTo(BigInteger.ONE) >= 0);
        });
    }
}
