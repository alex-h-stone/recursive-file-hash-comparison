package dev.alexhstone;

import dev.alexhstone.model.DiffResults;
import dev.alexhstone.model.FileHashResult;
import dev.alexhstone.model.FolderHierarchy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiffResultsCalculator {
    public String compareResults(List<FileHashResult> reportOne, List<FileHashResult> reportTwo) {
        return "TODO";
    }

    public DiffResults process(FolderHierarchy leftHashResults,
                               FolderHierarchy rightHashResults) {

        Set<FileHashResult> left = new HashSet<>(leftHashResults.getFileHashResults());
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
}
