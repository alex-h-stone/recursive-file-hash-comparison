package org.backup;

import org.backup.exception.InvalidFileHashPathException;
import org.backup.model.FileHashResult;
import org.backup.model.FolderHierarchy;
import org.backup.util.HashCalculator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecursiveFileHashGenerator {

    private final FileHashResultGenerator fileHashResultGenerator;

    public RecursiveFileHashGenerator() {
        fileHashResultGenerator = new FileHashResultGenerator(new HashCalculator());
    }

    public FolderHierarchy process(String absolutePathToFolder) {
        Path path = Paths.get(absolutePathToFolder);
        if (!Files.exists(path)) {
            String message = "The supplied path [" + absolutePathToFolder + "] does not exist, expected something of the form " +
                    "C:\\directory. The supplied path was resolved to the absolute path [" + path.toAbsolutePath().toFile().getAbsolutePath() + "]";
            throw new InvalidFileHashPathException(message);
        }
        Stream<Path> pathToWalk = walk(path);
        List<FileHashResult> fileHashResults = pathToWalk.parallel()
                .map(Path::toFile)
                .filter(File::isFile)
                .map(fileHashResultGenerator::process)
                .collect(Collectors.toList());

        fileHashResults.sort(Comparator.comparing(FileHashResult::getAbsolutePath));

        return FolderHierarchy.builder()
                .rootFolderAbsolutePath(path.toFile().getAbsolutePath())
                .fileHashResults(fileHashResults)
                .build();
    }

    private Stream<Path> walk(Path path) {
        try {
            return Files.walk(path);
        } catch (IOException e) {
            String message = "Unable to walk the folder hierarchy at [%s] because of: %s"
                    .formatted(path.toAbsolutePath(), e.getMessage());
            throw new RuntimeException(message);
        }
    }
}
