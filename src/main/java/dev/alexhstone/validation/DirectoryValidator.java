package dev.alexhstone.validation;

import dev.alexhstone.exception.InvalidPathException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;

@Slf4j
public class DirectoryValidator {

    private final PathValidator pathValidator = new PathValidator();

    public Path validateExists(Path path) {
        File file = path.toFile();
        if (!file.exists()) {
            String message = "The path does not exist [%s]".formatted(file.getAbsolutePath());
            log.warn(message);
            throw new InvalidPathException(message);
        }

        if (!file.isDirectory()) {
            String message = "The path is not a directory [%s]".formatted(file.getAbsolutePath());
            log.warn(message);
            throw new InvalidPathException(message);
        }

        return path;
    }

    public Path validateExists(String absolutePath) {
        Path validatedPath = pathValidator.validateExists(absolutePath);
        return validateExists(validatedPath);
    }
}
