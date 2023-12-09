package dev.alexhstone.calculator;

import dev.alexhstone.model.FileHashResult;
import dev.alexhstone.model.FolderHierarchy;
import dev.alexhstone.model.HashDiffResults;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class DiffResultsCalculator {

    public HashDiffResults compareResults(Set<String> leftHashes, Set<String> rightHashes) {

        log.debug("About to calculate leftHashesNotPresentInRight from {} leftHashes", leftHashes.size());
        Set<String> leftHashesNotPresentInRight = leftHashes
                .parallelStream()
                .filter(leftHashResult -> !(rightHashes.contains(leftHashResult)))
                .collect(Collectors.toSet());

        log.debug("About to calculate rightHashesNotPresentInLeft from {} rightHashes", rightHashes.size());
        Set<String> rightHashesNotPresentInLeft = rightHashes
                .parallelStream()
                .filter(rightHashResult -> !(leftHashes.contains(rightHashResult)))
                .collect(Collectors.toSet());

        // TODO add fuzzy matching of misses?

        return HashDiffResults.builder()
                .leftHashesNotPresentInRight(leftHashesNotPresentInRight)
                .rightHashesNotPresentInLeft(rightHashesNotPresentInLeft)
                .build();
    }

    private Set<FileHashResult> getFileHashResults(FolderHierarchy folderHierarchy) {
        List<FileHashResult> fileHashResultsList = folderHierarchy.getFileHashResults();
        Set<FileHashResult> fileHashResultsSet = new HashSet<>(fileHashResultsList);

        if (fileHashResultsList.size() == fileHashResultsSet.size()) {
            return fileHashResultsSet;
        }

        Map<FileHashResult, Long> countOfFileHashResult = new HashMap<>();
        for (FileHashResult fileHashResult : fileHashResultsList) {
            countOfFileHashResult.putIfAbsent(fileHashResult, 0L);

            Long currentCount = countOfFileHashResult.get(fileHashResult);
            countOfFileHashResult.put(fileHashResult, currentCount + 1);
        }
        List<Map.Entry<FileHashResult, Long>> duplicates = countOfFileHashResult
                .entrySet()
                .parallelStream()
                .filter(new Predicate<Map.Entry<FileHashResult, Long>>() {
                    @Override
                    public boolean test(Map.Entry<FileHashResult, Long> fileHashResultLongEntry) {
                        return fileHashResultLongEntry.getValue() > 1;
                    }
                })
                .toList();

        String string = "Expected sizes of List and Set to match, found unexpected equivalent files in fileHashResultsList: [%s]"
                .formatted(duplicates);
        throw new IllegalStateException(string);
    }
}
