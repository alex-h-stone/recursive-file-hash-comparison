package dev.alexhstone;

import dev.alexhstone.model.DiffResults;
import dev.alexhstone.model.FileHashResult;
import dev.alexhstone.model.FolderHierarchy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DiffResultsCalculator {
    public String compareResults(List<FileHashResult> reportOne, List<FileHashResult> reportTwo) {
        return "TODO";
    }

    public DiffResults process(FolderHierarchy leftHashResults,
                               FolderHierarchy rightHashResults) {

        Set<FileHashResult> left = getFileHashResults(leftHashResults);
        Set<FileHashResult> right = new HashSet<>(rightHashResults.getFileHashResults());

        // TODO add validation logic , size as list equals size as set

        List<FileHashResult> leftFilesNotPresentInRight = left
                .parallelStream()
                .filter(leftHashResult -> !(right.contains(leftHashResult)))
                .toList();

        List<FileHashResult> rightFilesNotPresentInLeft = right
                .parallelStream()
                .filter(rightHashResult -> !(left.contains(rightHashResult)))
                .toList();

        // TODO add fuzzy matching of misses?

        return DiffResults.builder()
                .leftFilesNotPresentInRight(leftFilesNotPresentInRight)
                .rightFilesNotPresentInLeft(rightFilesNotPresentInLeft)
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
