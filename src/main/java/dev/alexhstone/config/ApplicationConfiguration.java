package dev.alexhstone.config;

import dev.alexhstone.validation.DirectoryValidator;

import java.nio.file.Path;

public class ApplicationConfiguration {

    public static Path getLocationOfFileBackedQueue() {
        Path fileBackedLocation = Path.of("C:\\big_queue");
        return new DirectoryValidator().validateExists(fileBackedLocation);
    }

    public static String getActiveMQBrokerURL() {
        return "tcp://localhost:61616";
    }
}
