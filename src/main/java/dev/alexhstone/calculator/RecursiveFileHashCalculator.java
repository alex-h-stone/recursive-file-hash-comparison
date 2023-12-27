package dev.alexhstone.calculator;

import dev.alexhstone.model.FileHashResult;
import dev.alexhstone.util.PathWalker;
import dev.alexhstone.validation.DirectoryValidator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Slf4j
public class RecursiveFileHashCalculator {

    private final FileHashResultCalculator fileHashResultCalculator;
    private final DirectoryValidator directoryValidator;

    public RecursiveFileHashCalculator() {
        HashDetailsCalculator hashCalculator = new HashDetailsCalculator();
        fileHashResultCalculator = new FileHashResultCalculator(hashCalculator);
        directoryValidator = new DirectoryValidator();
    }

    public void process(Path workingDirectory, Consumer<FileHashResult> consumer) {
        Path validPath = directoryValidator.validateExists(workingDirectory);

        PathWalker pathWalker = new PathWalker(workingDirectory);

        List<File> files = pathWalker.walk()
                .parallel()
                .map(Path::toFile)
                .filter(File::isFile)
                .toList();
        String workingDirectoryPathString = workingDirectory.toFile().getAbsolutePath();
        log.info("About to calculate hashes for {} files under the working directory [{}]",
                files.size(), workingDirectoryPathString);

        // TODO chunk this
        files.parallelStream()
                .filter(new Predicate<File>() {
                    @Override
                    public boolean test(File file) {
                        return false;
                    }
                })
                .forEach(file -> {
                    FileHashResult hashResult =
                            fileHashResultCalculator.process(validPath, file);
                    consumer.accept(hashResult);
                });
        log.info("Completed calculating hashes for {} files under the working directory [{}]",
                files.size(), workingDirectoryPathString);
    }
}
