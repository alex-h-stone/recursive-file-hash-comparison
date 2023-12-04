package org.backup;


import lombok.extern.slf4j.Slf4j;
import org.backup.model.FileHashResult;
import org.backup.model.FolderHierarchy;

import java.util.List;

@Slf4j
public class Main {
    public static void main(String[] args) {
        RecursiveFileHashGenerator hashReportGenerator = new RecursiveFileHashGenerator();

        FolderHierarchy hashResults = calculateHashes(hashReportGenerator);

        deserialiseAndPersist(hashResults);

        ReportComparison reportComparison = new ReportComparison();
        //String report = reportComparison.compareResults(hashResultsOne, hashResultsTwo);

    }

    private static void deserialiseAndPersist(FolderHierarchy folderHierarchy) {
        HashReportPersist hashReportPersist = new HashReportPersist("F:");

        String reportFileName = "folderHierarchy.json";
        log.info("About to persist to: ["+reportFileName+"]");
        hashReportPersist.persist(folderHierarchy, reportFileName);
        log.info("Completed persisting to: ["+reportFileName+"]");
    }

    private static FolderHierarchy calculateHashes(RecursiveFileHashGenerator hashReportGenerator) {
        String absolutePathToFolder = "F:\\tmp";
        log.info("About to process all hashes for ["+ absolutePathToFolder+"]");
        FolderHierarchy folderHierarchy = hashReportGenerator.process(absolutePathToFolder);
        log.info("Processed and found: " + folderHierarchy.getFileHashResults().size());
        return folderHierarchy;
    }
}