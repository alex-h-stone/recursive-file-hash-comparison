package dev.alexhstone.test.util;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class FileSystemUtils {

    private final Path workingDirectory;

    public File createFileWithContent(String fileName, String content) {
        Path resolved = workingDirectory.resolve(fileName);

        if(Files.exists(resolved)){
            String message = "Cannot create new file with path [%s] as a file with that name already exists".formatted(resolved.toFile().getAbsolutePath());
            throw new IllegalArgumentException(message);
        }

        try {
            Files.writeString(resolved, content);
        } catch (IOException e) {
            String message = "Unable to create the new file [%s] in the working directory [%s] with content [%s]"
                    .formatted(fileName, absolutePathOfWorkingDirectory(), content);
            throw new IllegalArgumentException(message, e);
        }

        return resolved.toFile();
    }

    public Path createDirectory(String newDirectoryName) {
        Path resolved = workingDirectory.resolve(newDirectoryName);
        try {
            return Files.createDirectory(resolved);
        } catch (IOException e) {
            String message = "Unable to create the new directory [%s] in the working directory [%s]"
                    .formatted(newDirectoryName, absolutePathOfWorkingDirectory());
            throw new IllegalArgumentException(message, e);
        }
    }

    private String absolutePathOfWorkingDirectory() {
        return workingDirectory.toFile().getAbsolutePath();
    }
}
