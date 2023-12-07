package dev.alexhstone.calculator;

import dev.alexhstone.exception.InvalidFileHashPathException;
import dev.alexhstone.model.FileHashResult;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class RecursiveFileHashCalculator {

    private final FileHashResultCalculator fileHashResultCalculator;

    public RecursiveFileHashCalculator() {
        HashDetailsCalculator hashCalculator = new HashDetailsCalculator();
        fileHashResultCalculator = new FileHashResultCalculator(hashCalculator);
    }

    public void process(Path workingDirectoryPath, Consumer<FileHashResult> consumer) {
        String workingDirectoryPathString = workingDirectoryPath.toFile().getAbsolutePath();
        if (!Files.exists(workingDirectoryPath)) {
            String message = "The supplied path [" + workingDirectoryPath + "] does not exist, expected something of the form " +
                    "C:\\directory. The supplied path was resolved to the absolute path [" + workingDirectoryPathString + "]";
            throw new InvalidFileHashPathException(message);
        }
        Stream<Path> pathToWalk = walk(workingDirectoryPath);
        List<File> files = pathToWalk
                .parallel()
                .map(Path::toFile)
                .filter(File::isFile)
                .toList();
        log.info("About to calculate hashes for {} files under the working directory [{}]",
                files.size(), workingDirectoryPathString);

        files.parallelStream()
                .forEach(file -> {
                    FileHashResult hashResult =
                            fileHashResultCalculator.process(workingDirectoryPath, file);
                    consumer.accept(hashResult);
                });
        log.info("Completed calculating hashes for {} files under the working directory [{}]",
                files.size(), workingDirectoryPathString);
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
