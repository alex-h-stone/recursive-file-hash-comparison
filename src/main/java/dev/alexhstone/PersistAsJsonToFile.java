package dev.alexhstone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PersistAsJsonToFile {

    private final Path workingDirectory;
    private final Gson prettyPrinting;

    public PersistAsJsonToFile(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
        this.prettyPrinting = new GsonBuilder().setPrettyPrinting().create();
    }

    public void persist(Object objectToBePersisted, String fileName) {
        String jsonToBePersisted = prettyPrinting.toJson(objectToBePersisted);

        Path resolvedPath = workingDirectory.resolve(fileName);
        File resolvedPathToFile = resolvedPath.toFile();
        if (resolvedPathToFile.exists()) {
            String message = "Unable to persist to [%s] as the file already exists".formatted(resolvedPathToFile.getAbsolutePath());
            throw new IllegalArgumentException(message);
        }

        try {
            Files.writeString(resolvedPath, jsonToBePersisted);
        } catch (IOException e) {
            String message = "Unable to write to object with absolute path: [%s] failed with error message[%s]"
                    .formatted(resolvedPathToFile.getAbsolutePath(),
                            e.getMessage());
            throw new RuntimeException(message, e);
        }
    }
}
