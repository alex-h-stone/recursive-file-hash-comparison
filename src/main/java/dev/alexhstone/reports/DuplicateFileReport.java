package dev.alexhstone.reports;

import dev.alexhstone.model.hashresult.HashResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Slf4j
public class DuplicateFileReport {

    private final Queue<DuplicatedFiles> duplicated = new ConcurrentLinkedQueue<>();

    public void addDuplicates(HashResult sourceFile, List<HashResult> duplicateFiles) {
        DuplicatedFiles duplicatedFiles = new DuplicatedFiles(sourceFile, duplicateFiles);
        duplicated.add(duplicatedFiles);
        log.debug("Added duplicatedFiles with sourceFile AbsolutePath: [{}] and {} duplicate files",
                sourceFile.getAbsolutePath(), duplicateFiles.size());
    }

    public String toText() {
        return duplicated.stream()
                .map(DuplicatedFiles::toText)
                .collect(Collectors.joining("\n\r"));
    }
}
