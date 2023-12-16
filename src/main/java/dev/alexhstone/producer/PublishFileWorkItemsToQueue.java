package dev.alexhstone.producer;

import dev.alexhstone.model.queue.FileWorkItem;
import dev.alexhstone.util.DirectoryValidator;
import dev.alexhstone.util.PathWalker;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
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
        Set<Path> workingDirectories = Arrays.stream(args)
                .map(directoryValidator::validateExists)
                .collect(Collectors.toSet());

        PublishFileWorkItemsToQueue publishFileWorkItemsToQueue =
                new PublishFileWorkItemsToQueue(workingDirectories);
        publishFileWorkItemsToQueue.execute();

    }

    private void execute() {
        workingDirectories
                .parallelStream()
                .forEach(processWorkingDirectory());
    }

    private Consumer<Path> processWorkingDirectory() {
        // TODO add write to
        return workingDirectory -> toFileWorkItemsStream(workingDirectory)
                .forEach(fileWorkItem -> System.out.println("fileWorkItem: " + fileWorkItem));
    }

    private Stream<FileWorkItem> toFileWorkItemsStream(Path workingDirectory) {
        Function<File, FileWorkItem> toFileWorkItemMapper = new FileToFileWorkItemMapper()
                .asFunction(workingDirectory);
        PathWalker pathWalker = new PathWalker(workingDirectory);

        return pathWalker
                .walk()
                .parallel()
                .map(Path::toFile)
                .map(toFileWorkItemMapper::apply);
    }
}
