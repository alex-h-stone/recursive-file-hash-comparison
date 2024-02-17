package dev.alexhstone.reports;

import dev.alexhstone.ProgressLogging;
import dev.alexhstone.RunnableApplication;
import dev.alexhstone.datastore.HashResultPersistenceService;
import dev.alexhstone.model.hashresult.HashResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DuplicateFileReportGenerator implements RunnableApplication {

    private static final int LOGGING_INTERVAL_IN_ITEMS = 5_000;
    private final ProgressLogging progressLogging =
            new ProgressLogging("Processed another {} HashResult documents", LOGGING_INTERVAL_IN_ITEMS);
    private final HashResultPersistenceService persistenceService;

    @Override
    public boolean matches(String applicationNameToMatch) {
        return "findDuplicateFiles".equalsIgnoreCase(applicationNameToMatch);
    }

    @Override
    public String getApplicationName() {
        return "duplicateFilesReport";
    }

    /**
     * Identify all instances where two or more files have same hash but different partition UUIDs.
     */
    public void execute() {
        DuplicateFileReport duplicateFileReport = new DuplicateFileReport();
        persistenceService.applyToAll(sourceHashResult -> {
            log.debug("Processing HashResult Document with absolutePath: [{}]", sourceHashResult.getAbsolutePath());
            List<HashResult> matchingHashResults = persistenceService.getByHashValueAndPartitionUuid(sourceHashResult.getHashValue(),
                    sourceHashResult.getPartitionUuid());

            if (!matchingHashResults.isEmpty()) {
                // TODO should save results to a new MongoDB collection
                log.debug("Found files which are duplicates of [{}] duplicate files are: [{}]",
                        sourceHashResult, matchingHashResults);
                duplicateFileReport.addDuplicates(sourceHashResult, matchingHashResults);
            }

            progressLogging.incrementProgress();
        });
        persistenceService.store(duplicateFileReport);
    }
}
