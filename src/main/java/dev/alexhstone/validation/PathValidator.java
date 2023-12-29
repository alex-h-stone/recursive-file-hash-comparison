package dev.alexhstone.validation;

import dev.alexhstone.exception.InvalidPathException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathValidator {

    public Path validateExists(String absolutePath) {
        Path path = Paths.get(absolutePath);
        if (path.toFile().exists()) {
            return path;
        }
        String message = "The path [%s] does not exist"
                .formatted(absolutePath);
        throw new InvalidPathException(message);
    }
}
