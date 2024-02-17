package dev.alexhstone.reports;

import dev.alexhstone.model.hashresult.HashResult;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DuplicateFileReport {

    private final Queue<DuplicatedFiles> duplicated = new ConcurrentLinkedQueue();

    public void addDuplicates(HashResult sourceFile, List<HashResult> duplicateFiles) {
        DuplicatedFiles duplicatedFiles = new DuplicatedFiles(sourceFile, duplicateFiles);
        // TODO implemen this
    }


    public String toText() {
        return duplicated.toString();
    }
}
