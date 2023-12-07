package dev.alexhstone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PersistToReportDirectoryAsJSON {

    private final Path reportPath;
    private final Gson prettyPrinting = new GsonBuilder().setPrettyPrinting().create();

    public PersistToReportDirectoryAsJSON(Path reportPath) {
        this.reportPath = reportPath;
    }

    public void persist(Object toBePersisted, String fileName) {
        String jsonToBePersisted = prettyPrinting.toJson(toBePersisted);
        Path resolvedPath = reportPath.resolve(fileName);
        File resolvedPathToFile = resolvedPath.toFile();
        if (resolvedPathToFile.exists()) {
            String message = "Unable to persist to [" + resolvedPathToFile.getAbsolutePath() + "] as the file already exists";
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
