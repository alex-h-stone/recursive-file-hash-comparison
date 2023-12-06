package dev.alexhstone;

import dev.alexhstone.model.FolderHierarchy;
import dev.alexhstone.exception.InvalidFileHashPathException;
import dev.alexhstone.model.FileHashResult;
import dev.alexhstone.util.HashCalculator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecursiveFileHashCalculator {

    private final FileHashResultCalculator fileHashResultCalculator;

    public RecursiveFileHashCalculator() {
        HashCalculator hashCalculator = new HashCalculator();
        fileHashResultCalculator = new FileHashResultCalculator(hashCalculator);
    }

    public FolderHierarchy process(Path absolutePathToWorkingDirectory) {
        String workingDirectoryPathString = absolutePathToWorkingDirectory.toFile().getAbsolutePath();
        if (!Files.exists(absolutePathToWorkingDirectory)) {
            String message = "The supplied path [" + absolutePathToWorkingDirectory + "] does not exist, expected something of the form " +
                    "C:\\directory. The supplied path was resolved to the absolute path [" + workingDirectoryPathString + "]";
            throw new InvalidFileHashPathException(message);
        }
        Stream<Path> pathToWalk = walk(absolutePathToWorkingDirectory);
        List<FileHashResult> fileHashResults = pathToWalk.parallel()
                .map(Path::toFile)
                .filter(File::isFile)
                .map((File file) ->
                        fileHashResultCalculator
                                .process(absolutePathToWorkingDirectory, file))
                .collect(Collectors.toList());

        fileHashResults.sort(Comparator.comparing(FileHashResult::getAbsolutePathToFile));

        return FolderHierarchy.builder()
                .absolutePathToWorkingDirectory(workingDirectoryPathString)
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
