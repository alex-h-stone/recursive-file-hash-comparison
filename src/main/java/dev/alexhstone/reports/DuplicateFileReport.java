package dev.alexhstone.reports;

import dev.alexhstone.datastore.HashResultPersistenceService;
import dev.alexhstone.model.hashresult.HashResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@AllArgsConstructor
public class DuplicateFileReport {

    private final HashResultPersistenceService persistenceService;

    /**
     * Identify all instances where two or more files have same hash but different partition UUIDs.
     */
    public void execute() {
        persistenceService.applyToAll(new Consumer<HashResult>() {
            @Override
            public void accept(HashResult sourceHashResult) {
                if (!"[Cannot calculate hash for a directory]".equals(sourceHashResult.getHashValue())) {
                    String hashValue = sourceHashResult.getHashValue();
                    List<HashResult> matchingHashResults = persistenceService.getByHashValueAndPartitionUuid(hashValue,
                            sourceHashResult.getPartitionUuid());

                    if (!matchingHashResults.isEmpty()) {
                        log.warn("Found duplicate files: [{}]", matchingHashResults);
                    }
                }
            }
        });
    }
}
