package dev.alexhstone.validation;

import dev.alexhstone.exception.InvalidFileException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class FileValidator {

    private final PathValidator pathValidator = new PathValidator();

    public File validateExists(File file) {
        if (!file.exists()) {
            String message = "The file does not exist [%s]".formatted(file.getAbsolutePath());
            log.warn(message);
            throw new InvalidFileException(message);
        }

        return file;
    }

    public File validateIsFile(File file) {
        File fileExists = validateExists(file);

        if (fileExists.isDirectory()) {
            String message = "The file is actually a directory [%s]".formatted(file.getAbsolutePath());
            log.warn(message);
            throw new InvalidFileException(message);
        }

        return file;
    }

    public File validateExists(String absolutePathToFile) {
        File file = pathValidator.validateExists(absolutePathToFile).toFile();
        return validateExists(file);
    }
}
