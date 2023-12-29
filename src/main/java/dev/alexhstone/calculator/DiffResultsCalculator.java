package dev.alexhstone.calculator;

import dev.alexhstone.model.FolderHierarchy;
import dev.alexhstone.model.HashDiffResults;
import dev.alexhstone.model.datastore.WorkItemHashResult;
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

    private Set<WorkItemHashResult> getFileHashResults(FolderHierarchy folderHierarchy) {
        List<WorkItemHashResult> workItemHashResultsList = folderHierarchy.getWorkItemHashResults();
        Set<WorkItemHashResult> workItemHashResultsSet = new HashSet<>(workItemHashResultsList);

        if (workItemHashResultsList.size() == workItemHashResultsSet.size()) {
            return workItemHashResultsSet;
        }

        Map<WorkItemHashResult, Long> countOfFileHashResult = new HashMap<>();
        for (WorkItemHashResult workItemHashResult : workItemHashResultsList) {
            countOfFileHashResult.putIfAbsent(workItemHashResult, 0L);

            Long currentCount = countOfFileHashResult.get(workItemHashResult);
            countOfFileHashResult.put(workItemHashResult, currentCount + 1);
        }
        List<Map.Entry<WorkItemHashResult, Long>> duplicates = countOfFileHashResult
                .entrySet()
                .parallelStream()
                .filter(new Predicate<Map.Entry<WorkItemHashResult, Long>>() {
                    @Override
                    public boolean test(Map.Entry<WorkItemHashResult, Long> fileHashResultLongEntry) {
                        return fileHashResultLongEntry.getValue() > 1;
                    }
                })
                .toList();

        String string = "Expected sizes of List and Set to match, found unexpected equivalent files in workItemHashResultsList: [%s]"
                .formatted(duplicates);
        throw new IllegalStateException(string);
    }
}
