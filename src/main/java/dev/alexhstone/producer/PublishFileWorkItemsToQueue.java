package dev.alexhstone.producer;

import dev.alexhstone.model.queue.FileWorkItem;
import dev.alexhstone.storage.FileWorkItemQueueFacade;
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

    private final FileWorkItemQueueFacade queueFacade;
    private final Set<Path> workingDirectories;

    public PublishFileWorkItemsToQueue(FileWorkItemQueueFacade queueFacade,
                                       Set<Path> workingDirectories) {
        this.queueFacade = queueFacade;
        this.workingDirectories = Collections.unmodifiableSet(workingDirectories);
    }

    public static void main(String[] args) {
        DirectoryValidator directoryValidator = new DirectoryValidator();
        Set<Path> workingDirectories = Arrays.stream(args)
                .map(directoryValidator::validateExists)
                .collect(Collectors.toSet());

        PublishFileWorkItemsToQueue publishFileWorkItemsToQueue =
                new PublishFileWorkItemsToQueue(new FileWorkItemQueueFacade(),
                        workingDirectories);
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
                .forEach(new Consumer<FileWorkItem>() {
                    @Override
                    public void accept(FileWorkItem fileWorkItem) {
                        log.debug("About to add FileWorkItem to the queue: {}", fileWorkItem);
                        queueFacade.publish(fileWorkItem);
                    }
                });
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
