package dev.alexhstone;

import dev.alexhstone.model.DiffResults;
import dev.alexhstone.model.FolderHierarchy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {

    private final String absolutePathToFolderOne;
    private final String absolutePathToFolderTwo;
    private final String absolutePathToReportFolder;
    private final RecursiveFileHashCalculator recursiveFileHashCalculator;
    private PersistToReportDirectoryAsJSON persistToReportDirectoryAsJSON;

    public Application(String absolutePathToFolderOne,
                       String absolutePathToFolderTwo,
                       String absolutePathToReportFolder) {
        this.absolutePathToFolderOne = absolutePathToFolderOne;
        this.absolutePathToFolderTwo = absolutePathToFolderTwo;
        this.absolutePathToReportFolder = absolutePathToReportFolder;
        this.recursiveFileHashCalculator = new RecursiveFileHashCalculator();
        this.persistToReportDirectoryAsJSON = new PersistToReportDirectoryAsJSON(absolutePathToReportFolder);

    }

    public DiffResults execute() {

        FolderHierarchy folderOneHashResults = calculateFolderHierarchy(absolutePathToFolderOne,
                "folderOne");
        FolderHierarchy folderTwoHashResults = calculateFolderHierarchy(absolutePathToFolderTwo,
                "folderTwo");

        DiffResultsCalculator diffResultsCalculator = new DiffResultsCalculator();
        DiffResults diffResults = diffResultsCalculator.process(folderOneHashResults, folderTwoHashResults);
        persistToReportDirectoryAsJSON.persist(diffResults, "diffResults.json");

        return diffResults;
    }

    private FolderHierarchy calculateFolderHierarchy(String absolutePathToFolder,
                                                     String reportFileNamePrefix) {
        FolderHierarchy folderOneHashResults = calculateHashes(recursiveFileHashCalculator, absolutePathToFolder);
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

    private FolderHierarchy calculateHashes(RecursiveFileHashCalculator hashReportGenerator, String absolutePathToFolder) {
        log.info("About to process all hashes for [" + absolutePathToFolder + "]");
        FolderHierarchy folderHierarchy = hashReportGenerator.process(absolutePathToFolder);
        log.info("Processed and found: " + folderHierarchy.getFileHashResults().size());
        return folderHierarchy;
    }
}
