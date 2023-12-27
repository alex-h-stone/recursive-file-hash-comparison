package dev.alexhstone.producer;

import dev.alexhstone.model.queue.FileWorkItem;
import dev.alexhstone.util.PathWalker;
import dev.alexhstone.validation.DirectoryValidator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PublishFileWorkItemsToQueue {

    private final DurableQueue queue;
    private final Set<Path> workingDirectories;

    public PublishFileWorkItemsToQueue(DurableQueue durableQueue,
                                       Set<Path> workingDirectories) {
        this.queue = durableQueue;
        this.workingDirectories = Collections.unmodifiableSet(workingDirectories);
    }

    public static void main(String[] args) {
        log.debug("About to publish file work items from the working directories [{}] to the queue",
                Arrays.asList(args));
        DirectoryValidator directoryValidator = new DirectoryValidator();
        Set<Path> workingDirectories = Arrays.stream(args)
                .map(directoryValidator::validateExists)
                .collect(Collectors.toSet());

        DurableQueue durableQueue = new DurableQueue();
        durableQueue.initialise();

        PublishFileWorkItemsToQueue publishFileWorkItemsToQueue =
                new PublishFileWorkItemsToQueue(durableQueue,
                        workingDirectories);
        publishFileWorkItemsToQueue.execute();
    }

    private void execute() {
        workingDirectories
                .parallelStream()
                .forEach(processWorkingDirectory());
    }

    private Consumer<Path> processWorkingDirectory() {
        return workingDirectory -> toFileWorkItemsStream(workingDirectory)
                .forEach(publishToQueue());
    }

    private Consumer<FileWorkItem> publishToQueue() {
        return fileWorkItem -> {
            log.debug("About to add FileWorkItem to the queue: {}", fileWorkItem);
            queue.publish(fileWorkItem);
        };
    }

    private Stream<FileWorkItem> toFileWorkItemsStream(Path workingDirectory) {
        Function<File, FileWorkItem> toFileWorkItemMapper = new FileToFileWorkItemMapper()
                .asFunction(workingDirectory);
        PathWalker pathWalker = new PathWalker(workingDirectory);

        return pathWalker
                .walk()
                .parallel()
                .map(Path::toFile)
                .map(toFileWorkItemMapper);
    }
}
