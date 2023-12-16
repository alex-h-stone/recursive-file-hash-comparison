package dev.alexhstone.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class PathWalker {

    private final Path validRootPath;

    public PathWalker(Path validRootPath) {
        DirectoryValidator directoryValidator = new DirectoryValidator();
        this.validRootPath = directoryValidator.validateExists(validRootPath);
    }

    public Stream<Path> walk() {
        try {
            return Files.walk(validRootPath);
        } catch (IOException e) {
            String message = "Unable to walk the folder hierarchy at [%s] because of: [%s]"
                    .formatted(validRootPath.toAbsolutePath(), e.getMessage());
            throw new RuntimeException(message);
        }
    }
}
