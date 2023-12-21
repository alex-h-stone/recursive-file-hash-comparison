package dev.alexhstone.config;

import dev.alexhstone.util.DirectoryValidator;

import java.nio.file.Path;

public class ApplicationConfiguration {

    public static Path getLocationOfFileBackedQueue() {
        Path fileBackedLocation = Path.of("C:\\big_queue");
        return new DirectoryValidator().validateExists(fileBackedLocation);
    }
}
