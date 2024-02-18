package dev.alexhstone.reports;

import dev.alexhstone.model.hashresult.HashResult;

import java.util.List;
import java.util.stream.Collectors;

public class DuplicatedFiles {

    private final HashResult sourceFile;
    private final List<HashResult> duplicateFiles;

    public DuplicatedFiles(HashResult sourceFile, List<HashResult> duplicateFiles) {

        this.sourceFile = sourceFile;
        this.duplicateFiles = duplicateFiles;
    }

    public String toText() {
        String absolutePathPathsOfDuplicateFiles = duplicateFiles.stream()
                .map(HashResult::getAbsolutePath)
                .collect(Collectors.joining(","));
        
        return "SourceFile with AbsolutePath [%s] is identical to the files with absolute paths: [%s]"
                .formatted(sourceFile.getAbsolutePath(), absolutePathPathsOfDuplicateFiles);
    }
}
