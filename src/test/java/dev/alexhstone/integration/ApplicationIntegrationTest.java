package dev.alexhstone.integration;

import dev.alexhstone.Application;
import dev.alexhstone.model.DiffResults;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;

@Tag("long-running")
@Tag("integration")
public class ApplicationIntegrationTest {

    @TempDir
    private Path reportDirectory;

    @Test
    void scenarioOne_ShouldBeNoDifferencesAsAllFilesAreIdentical() {
        String scenarioOne = "C:\\Users\\Alex\\github_projects\\recursive-file-hash-comparison\\src\\test\\resources\\integration\\scenarioOne\\";

        Application application = new Application(
                scenarioOne + "folderOne",
                scenarioOne + "folderTwo",
                reportDirectory.toFile().getAbsolutePath());

        DiffResults diffResults = application.execute();

        assertThat(diffResults.getLeftFilesNotPresentInRight(),
        Matchers.hasSize(0));
        assertThat(diffResults.getRightFilesNotPresentInLeft(),
                Matchers.hasSize(0));

        File[] reportFiles = reportDirectory.toFile().listFiles();

        assertThat(reportFiles, Matchers.arrayWithSize(3));

        Arrays.stream(reportFiles).forEach(new Consumer<File>() {
            @Override
            public void accept(File file) {
                BigInteger fileSizeInBytes = FileUtils.sizeOfAsBigInteger(file);
                // TODO assert >= 1
            }
        });
    }
}
