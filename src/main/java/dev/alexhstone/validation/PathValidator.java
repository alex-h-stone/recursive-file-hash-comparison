package dev.alexhstone.validation;

import dev.alexhstone.exception.InvalidPathException;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class PathValidator {

    public Path validateExists(String absolutePath) {
        Path path = Paths.get(absolutePath);
        if (path.toFile().exists()) {
            return path;
        }
        String message = "The path [%s] does not exist"
                .formatted(absolutePath);
        log.warn(message);
        throw new InvalidPathException(message);
    }
}
