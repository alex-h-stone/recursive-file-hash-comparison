package dev.alexhstone.reports;

import dev.alexhstone.datastore.HashResultRepository;
import dev.alexhstone.model.hashresult.HashResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class DuplicateFileReport {

    private final HashResultRepository hashResultRepository;

    public static void main(String[] args) {
        DuplicateFileReport duplicateFileReport = new DuplicateFileReport();
        duplicateFileReport.execute();
    }

    public DuplicateFileReport() {
        hashResultRepository = new HashResultRepository();
    }

    private void execute() {
        // identify same hash but different absolute path and different working directory
        // iterate through whole colelction
        // for each entry do a get and compare
        hashResultRepository.applyToAll(new Consumer<HashResult>() {
            @Override
            public void accept(HashResult sourceHashResult) {
                if (!"[Cannot calculate hash for a directory]".equals(sourceHashResult.getHashValue())) {
                    String hashValue = sourceHashResult.getHashValue();
                    List<HashResult> matchingHashResults = hashResultRepository.getByHashValue(hashValue);
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
