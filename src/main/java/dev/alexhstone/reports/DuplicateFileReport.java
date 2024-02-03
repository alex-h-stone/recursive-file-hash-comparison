package dev.alexhstone.reports;

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
public class DuplicateFileReport implements RunnableApplication {

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
        persistenceService.applyToAll(sourceHashResult -> {
            if (!"[Cannot calculate hash for a directory]".equals(sourceHashResult.getHashValue())) {
                String hashValue = sourceHashResult.getHashValue();
                List<HashResult> matchingHashResults = persistenceService.getByHashValueAndPartitionUuid(hashValue,
                        sourceHashResult.getPartitionUuid());

                if (!matchingHashResults.isEmpty()) {
                    log.warn("Found duplicate files: [{}]", matchingHashResults);
                }
            }
        });
    }
}
