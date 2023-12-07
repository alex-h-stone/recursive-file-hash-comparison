package dev.alexhstone;

import dev.alexhstone.model.DiffResults;
import dev.alexhstone.model.FolderHierarchy;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class CompareDirectories {

    private final String leftAbsolutePath;
    private final String rightAbsolutePath;
    private final RecursiveFileHashCalculator recursiveFileHashCalculator = new RecursiveFileHashCalculator();
    private final PersistToReportDirectoryAsJSON persistToReportDirectoryAsJSON;

    public CompareDirectories(String leftAbsolutePath,
                              String rightAbsolutePath,
                              String reportDirectoryAbsolutePath) {
        // TODO add validate for methods parameters
        this.leftAbsolutePath = leftAbsolutePath;
        this.rightAbsolutePath = rightAbsolutePath;
        this.persistToReportDirectoryAsJSON = new PersistToReportDirectoryAsJSON(Paths.get(reportDirectoryAbsolutePath));
    }

    public DiffResults execute() {

        FolderHierarchy folderOneHashResults = calculateFolderHierarchy(leftAbsolutePath,
                "left");
        FolderHierarchy folderTwoHashResults = calculateFolderHierarchy(rightAbsolutePath,
                "right");

        DiffResultsCalculator diffResultsCalculator = new DiffResultsCalculator();
        DiffResults diffResults = diffResultsCalculator.process(folderOneHashResults, folderTwoHashResults);
        persistToReportDirectoryAsJSON.persist(diffResults, "diffResults.json");

        return diffResults;
    }

    private FolderHierarchy calculateFolderHierarchy(String absolutePathToWorkingDirectory,
                                                     String reportFileNamePrefix) {
        FolderHierarchy folderOneHashResults = calculateHashes(recursiveFileHashCalculator,
                Paths.get(absolutePathToWorkingDirectory));
        deserialiseAndPersist(folderOneHashResults,
                reportFileNamePrefix);
        return folderOneHashResults;
    }

    private void deserialiseAndPersist(FolderHierarchy folderHierarchy,
                                       String reportFileNamePrefix) {
        String reportFileName = reportFileNamePrefix + "_folderHierarchy.json";
        log.info("About to persist to: [" + reportFileName + "]");
        persistToReportDirectoryAsJSON.persist(folderHierarchy, reportFileName);
        log.info("Completed persisting to: [" + reportFileName + "]");
    }

    private FolderHierarchy calculateHashes(RecursiveFileHashCalculator hashReportGenerator, Path absolutePathToWorkingDirectory) {
        log.info("About to process all hashes for [" + absolutePathToWorkingDirectory + "]");
        FolderHierarchy folderHierarchy = hashReportGenerator.process(absolutePathToWorkingDirectory);
        log.info("Processed and found: " + folderHierarchy.getFileHashResults().size());
        return folderHierarchy;
    }
}
