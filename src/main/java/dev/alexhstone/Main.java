package dev.alexhstone;


import dev.alexhstone.model.FolderHierarchy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        RecursiveFileHashGenerator hashReportGenerator = new RecursiveFileHashGenerator();

        FolderHierarchy hashResults = calculateHashes(hashReportGenerator, "F:\\tmp");

        deserialiseAndPersist(hashResults, "F:");

        ReportComparison reportComparison = new ReportComparison();
        //String report = reportComparison.compareResults(hashResultsOne, hashResultsTwo);

    }

    private static void deserialiseAndPersist(FolderHierarchy folderHierarchy, String reportDirectory) {
        HashReportPersist hashReportPersist = new HashReportPersist(reportDirectory);

        String reportFileName = "folderHierarchy.json";
        log.info("About to persist to: [" + reportFileName + "]");
        hashReportPersist.persist(folderHierarchy, reportFileName);
        log.info("Completed persisting to: [" + reportFileName + "]");
    }

    private static FolderHierarchy calculateHashes(RecursiveFileHashGenerator hashReportGenerator, String absolutePathToFolder) {
        log.info("About to process all hashes for [" + absolutePathToFolder + "]");
        FolderHierarchy folderHierarchy = hashReportGenerator.process(absolutePathToFolder);
        log.info("Processed and found: " + folderHierarchy.getFileHashResults().size());
        return folderHierarchy;
    }
}