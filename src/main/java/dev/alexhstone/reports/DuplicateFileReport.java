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

    public void execute() {
        // identify same hash but different absolute path and different working directory
        // iterate through whole colelction
        // for each entry do a get and compare
        persistenceService.applyToAll(new Consumer<HashResult>() {
            @Override
            public void accept(HashResult sourceHashResult) {
                if (!"[Cannot calculate hash for a directory]".equals(sourceHashResult.getHashValue())) {
                    String hashValue = sourceHashResult.getHashValue();
                    List<HashResult> matchingHashResults = persistenceService.getByHashValue(hashValue);
                    matchingHashResults.forEach(new Consumer<HashResult>() {
                        @Override
                        public void accept(HashResult hashResultToCheck) {
                            log.debug("Processing {}", hashResultToCheck.getAbsolutePath());
                            if (!sourceHashResult.getAbsolutePath().equals(hashResultToCheck.getAbsolutePath())) {
                                log.info("Found duplicate files: [{}] and [{}]", sourceHashResult, hashResultToCheck);
                            }
                        }
                    });
                }
            }
        });
    }
}
