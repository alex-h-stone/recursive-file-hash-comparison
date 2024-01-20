package dev.alexhstone.model.hashresult;

import java.io.File;
import java.util.Objects;

public enum FileSystemType {
    FILE, DIRECTORY;

    public static FileSystemType valueOfFile(File file) {
        if (Objects.isNull(file)) {
            throw new IllegalArgumentException("Unable to determine FileSystemType for [null]");
        }

        if (file.isFile()) {
            return FILE;
        }

        if (file.isDirectory()) {
            return DIRECTORY;
        }

        throw new IllegalStateException("Unable to determine FileSystemType for [" + file + "]");
    }
}
