package dev.alexhstone.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

@Tag("long-running")
@Tag("integration")
public class IntegrationTest {

    @TempDir
    private Path directory;

    @Test
    @Disabled
    void scenarioOne() {
        // TODO do some integration tests
    }
}
