package dev.alexhstone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.alexhstone.model.FolderHierarchy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HashReportPersist {

    private final Path reportPath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public HashReportPersist(String reportDirectory) {
        reportPath = Paths.get(reportDirectory);
    }

    public void persist(FolderHierarchy results, String reportFileName) {
        String jsonResults = gson.toJson(results);
        Path resolved = reportPath.resolve(reportFileName);
        try {
            Files.writeString(resolved, jsonResults);
        } catch (IOException e) {
            String message = "Unable to write %d to: [%s] with absolute path: [%s]"
                    .formatted(results.getFileHashResults().size(), reportFileName, resolved.toFile().getAbsolutePath());
            throw new RuntimeException(message, e);
        }
    }
}
