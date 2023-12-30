package dev.alexhstone.producer;

import dev.alexhstone.model.queue.WorkItem;
import dev.alexhstone.queue.DurableQueueImpl;
import dev.alexhstone.queue.QueuePublisher;
import dev.alexhstone.util.Clock;
import dev.alexhstone.util.PathWalker;
import dev.alexhstone.validation.DirectoryValidator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PublishWorkItemsToQueue {

    private final QueuePublisher queue;
    private final Set<Path> workingDirectories;

    public PublishWorkItemsToQueue(QueuePublisher queuePublisher,
                                   Set<Path> workingDirectories) {
        this.queue = queuePublisher;
        this.workingDirectories = Collections.unmodifiableSet(workingDirectories);
    }

    public static void main(String[] args) {
        List<String> workingDirectoriesList = Arrays.asList(args);
        log.debug("About to publish file work items from the working directories [{}] to the queue",
                workingDirectoriesList);

        DirectoryValidator directoryValidator = new DirectoryValidator();
        Set<Path> validatedWorkingDirectories = workingDirectoriesList
                .stream()
                .map(directoryValidator::validateExists)
                .collect(Collectors.toSet());

        PublishWorkItemsToQueue publishWorkItemsToQueue =
                new PublishWorkItemsToQueue(new DurableQueueImpl(),
                        validatedWorkingDirectories);
        publishWorkItemsToQueue.execute();
    }

    private void execute() {
        queue.initialise();
        workingDirectories
                .parallelStream()
                .forEach(workingDirectory -> toStreamOfWorkItems(workingDirectory)
                        .forEach(publishToQueue()));
        queue.destroy();
    }

    private Consumer<WorkItem> publishToQueue() {
        return workItem -> {
            log.debug("About to add WorkItem to the queue: {}", workItem);
            queue.publish(workItem);
        };
    }

    private Stream<WorkItem> toStreamOfWorkItems(Path workingDirectory) {
        FileToWorkItemMapper fileToWorkItemMapper = new FileToWorkItemMapper(new Clock());
        Function<File, WorkItem> toFileWorkItemMapper = fileToWorkItemMapper
                .asFunction(workingDirectory);
        PathWalker pathWalker = new PathWalker(workingDirectory);

        return pathWalker
                .walk()
                .parallel()
                .map(Path::toFile)
                .map(toFileWorkItemMapper);
    }
}
