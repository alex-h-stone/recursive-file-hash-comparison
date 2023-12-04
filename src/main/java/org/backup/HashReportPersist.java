package org.backup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.backup.model.FileHashResult;
import org.backup.model.FolderHierarchy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
