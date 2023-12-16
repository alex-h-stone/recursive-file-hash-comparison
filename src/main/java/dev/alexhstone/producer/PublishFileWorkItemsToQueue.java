package dev.alexhstone.producer;

import dev.alexhstone.model.queue.FileWorkItem;
import dev.alexhstone.util.DirectoryValidator;
import dev.alexhstone.util.PathWalker;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PublishFileWorkItemsToQueue {

    private final Set<Path> workingDirectories;

    public PublishFileWorkItemsToQueue(Set<Path> workingDirectories) {
        this.workingDirectories = workingDirectories;
    }

    public static void main(String[] args) {
        DirectoryValidator directoryValidator = new DirectoryValidator();
        Set<Path> workingDirectories = Arrays.stream(args).map(new Function<String, Path>() {
            @Override
            public Path apply(String string) {
                return directoryValidator.validateExists(string);
            }
        }).collect(Collectors.toSet());

        PublishFileWorkItemsToQueue publishFileWorkItemsToQueue = new PublishFileWorkItemsToQueue(workingDirectories);
        publishFileWorkItemsToQueue.execute();

    }

    private void execute() {
        // TODO traverse file tree/path and populate queue with all work items

        workingDirectories
                .parallelStream()
                .forEach(processWorkingDirectory());
    }

    @NotNull
    private Consumer<Path> processWorkingDirectory() {
        return new Consumer<Path>() {
            @Override
            public void accept(Path workingDirectory) {
                getStream(workingDirectory)
                        .forEach(fileWorkItem -> System.out.println("fileWorkItem: " + fileWorkItem));
            }
        };
    }

    @NotNull
    private Stream<FileWorkItem> getStream(Path workingDirectory) {
        Function<File, FileWorkItem> toFileWorkItemMapper = new FileToFileWorkItemMapper()
                .asFunction(workingDirectory);

        Stream<Optional<FileWorkItem>> fileWorkItems =
                new PathWalker(workingDirectory)
                        .walk()
                        .parallel()
                        .map(path -> {
                            File file = path.toFile();
                            if (file.isFile()) {
                                return Optional.of(toFileWorkItemMapper.apply(file));
                            }
                            return Optional.empty();
                        });

        return fileWorkItems
                .filter(Optional::isPresent)
                .map(Optional::get);
    }
}
