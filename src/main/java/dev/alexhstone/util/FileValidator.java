package dev.alexhstone.util;

import dev.alexhstone.exception.InvalidPathException;

import java.io.File;
import java.nio.file.Paths;

public class FileValidator {

    public File validateExists(File file) {
        if (!file.exists()) {
            String message = "The file does not exist [%s]".formatted(file.getAbsolutePath());
            throw new InvalidPathException(message);
        }

        return file;
    }

    public File validateIsFile(File file){
        File fileExists = validateExists(file);

        if (fileExists.isDirectory()) {
            String message = "The file is actually a directory [%s]".formatted(file.getAbsolutePath());
            throw new InvalidPathException(message);
        }

        return file;
    }

    public File validateExists(String absolutePathToFile) {
        File file = Paths.get(absolutePathToFile).toFile();
        return validateExists(file);
    }
}
