package dev.alexhstone.util;

import dev.alexhstone.exception.InvalidPathException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FolderValidator {

    public Path validateExistsAndWritable(Path path) {
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

    public Path validateExistsAndWritable(String absolutePath) {
        Path path = Paths.get(absolutePath);
        return validateExistsAndWritable(path);
    }
}
