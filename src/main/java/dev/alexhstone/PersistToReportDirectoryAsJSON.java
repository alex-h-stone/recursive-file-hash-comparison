package dev.alexhstone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PersistToReportDirectoryAsJSON {

    private final Path reportPath;
    private final Gson prettyPrinting = new GsonBuilder().setPrettyPrinting().create();

    public PersistToReportDirectoryAsJSON(String reportDirectory) {
        reportPath = Paths.get(reportDirectory);
    }

    public void persist(Object toBePersisted, String fileName) {
        String jsonToBePersisted = prettyPrinting.toJson(toBePersisted);
        Path resolvedPathToFile = reportPath.resolve(fileName);
        // TODO add check if the file already exists, or otherwise is empty?
        try {
            Files.writeString(resolvedPathToFile, jsonToBePersisted);
        } catch (IOException e) {
            String message = "Unable to write to [%s] with absolute path: [%s] failed with error message[%s]"
                    .formatted(fileName,
                            resolvedPathToFile.toFile().getAbsolutePath(),
                            e.getMessage());
            throw new RuntimeException(message, e);
        }
    }
}
