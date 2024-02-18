package dev.alexhstone.model.hashresult;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Objects;

@Slf4j
public enum FileSystemType {
    FILE, DIRECTORY;

    public static FileSystemType valueOfFile(File file) {
        if (Objects.isNull(file)) {
            String message = "Unable to determine FileSystemType for [null]";
            log.warn(message);
            throw new IllegalArgumentException(message);
        }

        if (file.isFile()) {
            return FILE;
        }

        if (file.isDirectory()) {
            return DIRECTORY;
        }

        String message = "Unable to determine FileSystemType for [%s]".formatted(file);
        log.warn(message);
        throw new IllegalStateException(message);
    }
}
