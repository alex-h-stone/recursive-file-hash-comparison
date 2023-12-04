package dev.alexhstone.test.util;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class FileCreator {

    private final Path path;

    public File createFileWithContent(String fileName, String content) {
        Path resolved = path.resolve(fileName);

        try {
            Files.writeString(resolved, content);
        } catch (IOException e) {
            String message = "Unable to create the file [%s] with content [%s]"
                    .formatted(fileName, content);
            throw new RuntimeException(message, e);
        }

        return resolved.toFile();
    }
}
