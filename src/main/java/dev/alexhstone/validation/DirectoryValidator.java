package dev.alexhstone.validation;

import dev.alexhstone.exception.InvalidPathException;

import java.io.File;
import java.nio.file.Path;

public class DirectoryValidator {

    private final PathValidator pathValidator = new PathValidator();

    public Path validateExists(Path path) {
        File file = path.toFile();
        if (!file.exists()) {
            String message = "The path does not exist [%s]".formatted(file.getAbsolutePath());
            throw new InvalidPathException(message);
        }

        if (!file.isDirectory()) {
            String message = "The path is not a directory [%s]".formatted(file.getAbsolutePath());
            throw new InvalidPathException(message);
        }

        return path;
    }

    public Path validateExists(String absolutePath) {
        Path validatedPath = pathValidator.validateExists(absolutePath);
        return validateExists(validatedPath);
    }
}
